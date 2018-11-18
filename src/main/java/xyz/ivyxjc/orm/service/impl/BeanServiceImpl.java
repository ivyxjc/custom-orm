package xyz.ivyxjc.orm.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.lang.reflect.Field;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import xyz.ivyxjc.orm.bean.ColumnManager;
import xyz.ivyxjc.orm.enumerations.JdbcOperationType;
import xyz.ivyxjc.orm.enumerations.SupportedTypes;
import xyz.ivyxjc.orm.interfaces.PoBean;
import xyz.ivyxjc.orm.service.BeanService;

/**
 * @author Ivyxjc
 * @since 11/17/2018
 */
@Slf4j
@Repository
public class BeanServiceImpl implements BeanService {

    private static final String INSERT_SQL_TEMPLATE = "INSERT INTO %s (%s) VALUES (%s)";
    private static final String UPDATE_SQL_TEMPLATE = "UPDATE %s SET %s WHERE ";
    private static final String DELETE_SQL_TEMPLATE = "DELETE FROM %s WHERE ";
    private static final String SELECT_SQL_TEMPLATE = "SELECT %s FROM %s WHERE ";

    private static final Cache<String, String> sqlCache =
        CacheBuilder.newBuilder().maximumSize(300).build();

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * @param poBean
     * @return
     */
    private static MapSqlParameterSource buildParameterSource(PoBean poBean) {
        final MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        Field[] fields = poBean.getClass().getDeclaredFields();
        Arrays.stream(fields)
            .filter(
                field -> {
                    Column ann = field.getAnnotation(Column.class);
                    return ann != null;
                })
            .forEach(
                field -> {
                    Column ann = field.getAnnotation(Column.class);
                    String columnName = ann.name();
                    try {
                        field.setAccessible(true);
                        Object object = field.get(poBean);
                        addValueIntoParameterSource(columnName, object, sqlParameterSource);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                });

        return sqlParameterSource;
    }

    /**
     * @param sqlParameterSource
     * @param object
     */
    private static void addValueIntoParameterSource(
        @NotNull String columnName,
        @Nullable Object object,
        @NotNull final MapSqlParameterSource sqlParameterSource) {
        if (object == null) {
            sqlParameterSource.addValue(columnName, null);
            return;
        }
        switch (object.getClass().toString()) {
            case SupportedTypes.javaSqlDate:
                sqlParameterSource.addValue(columnName, object, Types.DATE);
                break;
            case SupportedTypes.javaSqlTimestamp:
                sqlParameterSource.addValue(columnName, object, Types.TIMESTAMP);
                break;
            case SupportedTypes.javaUtilDate:
                sqlParameterSource.addValue(columnName, object, Types.DATE);
                break;
            case SupportedTypes.javaTimeLocalDate:
                sqlParameterSource.addValue(columnName, object, Types.DATE);
                break;
            case SupportedTypes.javaTimeLocalDateTime:
                sqlParameterSource.addValue(columnName, object, Types.TIMESTAMP);
                break;
            default:
                sqlParameterSource.addValue(columnName, object);
        }
    }

    /**
     * 根据bean生成对应的CRUD语句 其中 generate crud statement
     */
    private static void buildSql(Class<? extends PoBean> clz) {
        log.info("buildSql starts: {}", clz);
        ColumnManager columnManager = new ColumnManager();
        Field[] fields = clz.getDeclaredFields();
        Table table = clz.getDeclaredAnnotation(Table.class);
        Arrays.stream(fields)
            .map(
                item -> {
                    Column ann = item.getAnnotation(Column.class);
                    return ann;
                })
            .filter(Objects::nonNull)
            .forEach(
                column -> {
                    columnManager.addSelectColumn(column.name());
                    if (column.insertable()) {
                        columnManager.addInsertColumn(column.name());
                    }
                    if (column.updatable()) {
                        columnManager.addUpdateColumn(column.name());
                    }
                });
        if (!columnManager.getSelectColumns().isEmpty()) {
            String selectColumns = StringUtils.join(columnManager.getSelectColumns(), ",");
            sqlCache.put(
                buildCacheKey(clz, JdbcOperationType.SELECT),
                String.format(SELECT_SQL_TEMPLATE, selectColumns, table.name()));
        }
        if (!columnManager.getInsertColumns().isEmpty()) {
            String insertColumns = StringUtils.join(columnManager.getInsertColumns(), ",");
            String insertValues = StringUtils.join(columnManager.getInsertValueColumns(), ",");
            sqlCache.put(
                buildCacheKey(clz, JdbcOperationType.INSERT),
                String.format(INSERT_SQL_TEMPLATE, table.name(), insertColumns, insertValues));
        }
        if (!columnManager.getUpdateColumns().isEmpty()) {
            String updateColumns = StringUtils.join(columnManager.getUpdateColumns(), ",");
            sqlCache.put(
                buildCacheKey(clz, JdbcOperationType.UPDATE),
                String.format(UPDATE_SQL_TEMPLATE, table.name(), updateColumns));
        }
        sqlCache.put(buildCacheKey(clz, JdbcOperationType.DELETE), table.name());
    }

    /**
     * generate sql's key based on operation's type
     *
     * @param clz @Notnull
     * @param type
     * @return
     */
    private static String buildCacheKey(Class<? extends PoBean> clz, JdbcOperationType type) {
        return clz.getName().concat(type.name());
    }

    @Override
    public void insert(PoBean poBean) {
        String sql =
            sqlCache.getIfPresent(buildCacheKey(poBean.getClass(), JdbcOperationType.INSERT));
        if (StringUtils.isBlank(sql)) {
            buildSql(poBean.getClass());
        }
        sql = sqlCache.getIfPresent(buildCacheKey(poBean.getClass(), JdbcOperationType.INSERT));

        MapSqlParameterSource sqlParameterSource = buildParameterSource(poBean);
        log.info("insert sql is: {}", sql);
        jdbcTemplate.update(sql, sqlParameterSource);
    }

    @Override
    public void batchInsert(@NotNull List<PoBean> list) {
        if (list.isEmpty()) {
            return;
        }
        PoBean firstOne = list.get(0);
        String sql =
            sqlCache.getIfPresent(buildCacheKey(firstOne.getClass(), JdbcOperationType.INSERT));
        if (StringUtils.isBlank(sql)) {
            buildSql(firstOne.getClass());
        }
        sql = sqlCache.getIfPresent(buildCacheKey(firstOne.getClass(), JdbcOperationType.INSERT));
        SqlParameterSource[] parameterSourceArray = new SqlParameterSource[list.size()];
        for (int i = 0; i < list.size(); i++) {
            parameterSourceArray[i] = buildParameterSource(list.get(i));
        }
        Long t1 = System.currentTimeMillis();
        jdbcTemplate.batchUpdate(sql, parameterSourceArray);
        Long t2 = System.currentTimeMillis();
        log.info("sql execute time is: {}", t2 - t1);
    }

    @Override
    public void update(PoBean poBean, String... whereColumnsNames) {
    }
}
