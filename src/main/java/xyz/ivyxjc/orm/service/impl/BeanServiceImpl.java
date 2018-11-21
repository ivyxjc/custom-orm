package xyz.ivyxjc.orm.service.impl;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.Column;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import xyz.ivyxjc.orm.enumerations.JdbcOperationType;
import xyz.ivyxjc.orm.enumerations.SupportedTypes;
import xyz.ivyxjc.orm.enumerations.UpdateType;
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

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public List<PoBean> query(PoBean poBean, String... whereColumnsNames) {
        Class<? extends PoBean> clz = poBean.getClass();
        String sql = BeanDBUtils.getCachedSql(clz, JdbcOperationType.SELECT);
        String whereClaues = BeanDBUtils.buildWhereClause(whereColumnsNames);
        sql = sql.concat(whereClaues);
        MapSqlParameterSource sqlParameterSource = BeanDBUtils.buildParameterSource(poBean);
        return jdbcTemplate.query(sql, sqlParameterSource, new CustomerMapper(clz));
    }

    @Override
    public int insert(PoBean poBean) {
        String sql = BeanDBUtils.getCachedSql(poBean.getClass(), JdbcOperationType.INSERT);

        MapSqlParameterSource sqlParameterSource = BeanDBUtils.buildParameterSource(poBean);
        log.info("insert sql is: {}", sql);
        return jdbcTemplate.update(sql, sqlParameterSource);
    }

    @Override
    public int[] batchInsert(@NotNull List<PoBean> list) {
        if (list.isEmpty()) {
            return new int[0];
        }
        PoBean firstOne = list.get(0);
        String sql = BeanDBUtils.getCachedSql(firstOne.getClass(), JdbcOperationType.INSERT);

        SqlParameterSource[] parameterSourceArray = new SqlParameterSource[list.size()];
        for (int i = 0; i < list.size(); i++) {
            parameterSourceArray[i] = BeanDBUtils.buildParameterSource(list.get(i));
        }
        Long t1 = System.currentTimeMillis();
        int[] res = jdbcTemplate.batchUpdate(sql, parameterSourceArray);
        Long t2 = System.currentTimeMillis();
        log.info("sql execute time is: {}", t2 - t1);
        return res;
    }

    @Override
    public int update(PoBean poBean, String... whereColumnsNames) {
        String sql = BeanDBUtils.getCachedSql(poBean.getClass(), JdbcOperationType.UPDATE);
        String whereClause = BeanDBUtils.buildWhereClause(whereColumnsNames);
        MapSqlParameterSource sqlParameterSource = BeanDBUtils.buildParameterSource(poBean);
        return jdbcTemplate.update(sql.concat(whereClause), sqlParameterSource);
    }

    /**
     * 目前不支持 自定义wheresql
     */
    @Override
    public int update(PoBean poBean, Updater updater) {

        MapSqlParameterSource sqlParameterSource;
        String sql;
        switch (updater.getUpdateType()) {
            case ALL:
                sqlParameterSource = BeanDBUtils.buildParameterSource(poBean);
                sql = updater.getUpdateSql();
                return jdbcTemplate.update(sql, sqlParameterSource);
            case CUSTOM:
                // TODO: 11/21/2018 optimize the buildParameterSource, just include needed columns
                sqlParameterSource = BeanDBUtils.buildParameterSource(poBean);
                sql = updater.getUpdateSql();
                return jdbcTemplate.update(sql, sqlParameterSource);
            case NOTNULL:
                // TODO: 11/21/2018 optimize the buildParameterSource, just include needed columns
                sqlParameterSource = BeanDBUtils.buildParameterSource(poBean);
                List<String> whereColumnNames = updater.getWhereColumnNames();
                List<String> whereSqls = updater.getWhereCustomSql();
                List<String> updateColumns =
                    Arrays.stream(poBean.getClass().getDeclaredFields())
                        .filter(t -> t.getAnnotation(Column.class) != null)
                        .filter(
                            t -> {
                                t.setAccessible(true);
                                try {
                                    return t.get(poBean) != null;
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                    throw new RuntimeException();
                                }
                            })
                        .map(
                            f -> {
                                Column col = f.getAnnotation(Column.class);
                                return col.name();
                            })
                        .collect(Collectors.toList());
                Updater newUpdater =
                    Updater.builder()
                        .withUpdateColumns(
                            updateColumns.toArray(new String[updateColumns.size()]))
                        .withWhereColumnNames(
                            whereColumnNames.toArray(new String[updateColumns.size()]))
                        .withCustomSqls(whereSqls.toArray(new String[whereSqls.size()]))
                        .withUpdateType(UpdateType.CUSTOM)
                        .build();

                sql = newUpdater.getUpdateSql();
                return jdbcTemplate.update(sql, sqlParameterSource);
        }
        throw new IllegalArgumentException(
            "Unsupported update type ".concat(updater.getUpdateType().name()));
    }

    class CustomerMapper<T extends PoBean> implements RowMapper<T> {
        private Class clz;

        CustomerMapper(Class<? extends PoBean> clz) {
            this.clz = clz;
        }

        @Override
        public T mapRow(ResultSet rs, int rowNum) throws SQLException {
            try {
                Object object = clz.newInstance();
                Field[] fields = clz.getDeclaredFields();

                Arrays.stream(fields)
                    .filter(t -> t.getAnnotation(Column.class) != null)
                    .forEach(
                        f -> {
                            f.setAccessible(true);
                            try {
                                Column column = f.getAnnotation(Column.class);
                                String className = f.getType().getName();
                                switch (className) {
                                    case SupportedTypes.javaLangString:
                                        f.set(object, rs.getString(column.name()));
                                        break;
                                    case SupportedTypes.javaLangInteger:
                                        f.set(object, rs.getInt(column.name()));
                                        break;
                                    case SupportedTypes.javaLangLong:
                                        f.set(object, rs.getLong(column.name()));
                                        break;
                                    case SupportedTypes.javaLangFloat:
                                        f.set(object, rs.getFloat(column.name()));
                                        break;
                                    case SupportedTypes.javaLangDouble:
                                        f.set(object, rs.getDouble(column.name()));
                                        break;
                                    case SupportedTypes.javaSqlDate:
                                        f.set(object, rs.getDate(column.name()));
                                        break;
                                    case SupportedTypes.javaSqlTimestamp:
                                        f.set(object, rs.getTimestamp(column.name()));
                                        break;
                                    case SupportedTypes.javaTimeLocalDate:
                                        f.set(
                                            object,
                                            Optional.ofNullable(
                                                rs.getDate(column.name()))
                                                .map(java.sql.Date::toLocalDate)
                                                .orElse(null));
                                        break;
                                    case SupportedTypes.javaTimeLocalDateTime:
                                        f.set(
                                            object,
                                            Optional.ofNullable(
                                                rs.getTimestamp(
                                                    column.name()))
                                                .map(Timestamp::toLocalDateTime)
                                                .orElse(null));
                                        break;
                                    case SupportedTypes.javaUtilDate:
                                        f.set(object, rs.getDate(column.name()));
                                        break;
                                    default:
                                        f.set(object, rs.getString(column.name()));
                                        break;
                                }
                            } catch (IllegalAccessException | SQLException e) {
                                e.printStackTrace();
                                throw new RuntimeException(e);
                            }
                        });

                return (T) object;
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}
