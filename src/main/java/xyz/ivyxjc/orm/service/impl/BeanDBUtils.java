package xyz.ivyxjc.orm.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.lang.reflect.Field;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import xyz.ivyxjc.orm.annotation.AuditColumn;
import xyz.ivyxjc.orm.annotation.AuditTable;
import xyz.ivyxjc.orm.enumerations.JdbcOperationType;
import xyz.ivyxjc.orm.enumerations.SupportedTypes;
import xyz.ivyxjc.orm.interfaces.PoBean;

/**
 * @author Ivyxjc
 * @since 11/19/2018
 */

/**
 * this util is just designed for the use of BeanServiceImpl, do not use it in other place
 */
@Slf4j
final class BeanDBUtils {
    private static final String INSERT_SQL_TEMPLATE = "INSERT INTO %s (%s) VALUES (%s)";
    private static final String UPDATE_SQL_TEMPLATE = "UPDATE %s SET %s WHERE ";
    private static final String DELETE_SQL_TEMPLATE = "DELETE FROM %s WHERE ";
    private static final String SELECT_SQL_TEMPLATE = "SELECT %s FROM %s WHERE ";
    private static final String AUDIT_SQL_TEMPLATE =
        "INSERT INTO %s (%s) (SELECT %s FROM %s WHERE ${WHERE_CLAUSE})";

    private static final Cache<String, String> sqlCache =
        CacheBuilder.newBuilder().maximumSize(300).build();

    /**
     * @param poBean
     * @return
     */
    static MapSqlParameterSource buildParameterSource(PoBean poBean) {
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
    static void addValueIntoParameterSource(
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
    static void buildSql(Class<? extends PoBean> clz) {
        log.info("buildSql starts: {}", clz);
        ColumnManager columnManager = new ColumnManager();
        Field[] fields = clz.getDeclaredFields();
        Table table = clz.getDeclaredAnnotation(Table.class);
        Arrays.stream(fields)
            .map(item -> item.getAnnotation(Column.class))
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
            List<String> verions = new ArrayList<>();
            Arrays.stream(fields)
                .filter(field -> field.getAnnotation(Version.class) != null)
                .forEach(field -> verions.add(field.getAnnotation(Column.class).name()));
            String updateVersionSql = buildUpdateVersionSql(verions);
            sqlCache.put(
                buildCacheKey(clz, JdbcOperationType.UPDATE),
                String.format(UPDATE_SQL_TEMPLATE, table.name(),
                    updateColumns.concat(",").concat(updateVersionSql)));
        }
        sqlCache.put(buildCacheKey(clz, JdbcOperationType.DELETE), table.name());
    }

    static void buildAuditSql(Class<? extends PoBean> clz) {
        log.info("buildAuditSql starts: {}", clz);
        ColumnManager auditColumnManager = new ColumnManager();
        ColumnManager auditValueManager = new ColumnManager();
        Field[] fields = clz.getDeclaredFields();
        AuditTable auditTable = clz.getDeclaredAnnotation(AuditTable.class);
        if (auditTable == null) {
            return;
        }
        Table table = clz.getDeclaredAnnotation(Table.class);
        Arrays.stream(fields)
            .map(item -> item.getAnnotation(Column.class))
            .filter(Objects::nonNull)
            .forEach(
                column -> {
                    auditColumnManager.addSelectColumn(column.name());
                    auditValueManager.addSelectColumn(column.name());
                });
        AuditColumn[] auditColumns = clz.getAnnotationsByType(AuditColumn.class);
        if (auditColumns != null) {
            Arrays.stream(auditColumns).forEach(t -> {
                auditColumnManager.addSelectColumn(t.name());
                if (StringUtils.isNotBlank(t.defaultStrValue()) && StringUtils.isNotBlank(
                    t.defaultTemplateValue())) {
                    throw new RuntimeException(
                        "defaultStrValue and defaultTemplateValue cannot both have value");
                }
                if (StringUtils.isNotBlank(t.defaultStrValue())) {
                    auditValueManager.addSelectColumn(String.format("'%s'", t.defaultStrValue()));
                } else if (StringUtils.isNotBlank(t.defaultTemplateValue())) {
                    auditValueManager.addSelectColumn(t.defaultTemplateValue());
                } else {
                    auditValueManager.addSelectColumn(":".concat(t.name()));
                }
            });
        }
        String auditColumnsStr = StringUtils.join(auditColumnManager.getSelectColumns(), ",");
        String auditValueStr = StringUtils.join(auditValueManager.getSelectColumns(), ",");
        String sql =
            String.format(AUDIT_SQL_TEMPLATE, auditTable.name(), auditColumnsStr, auditValueStr,
                table.name());
        sqlCache.put(buildCacheKey(clz, JdbcOperationType.AUDIT_IN_APP), sql);
    }

    /**
     * generate sql's key based on operation's type
     *
     * @param clz @Notnull
     */
    static String buildCacheKey(Class<? extends PoBean> clz, JdbcOperationType type) {
        return clz.getName().concat(type.name());
    }

    static String buildWhereClause(String... whereColumnNames) {
        ColumnManager columnManager = new ColumnManager();
        Arrays.stream(whereColumnNames).forEach(t -> columnManager.addWhereColumn(t));
        return StringUtils.join(columnManager.getWhereColumns(), " and ");
    }

    static String getCachedSql(Class<? extends PoBean> clz, JdbcOperationType type) {
        String sql = sqlCache.getIfPresent(buildCacheKey(clz, type));
        if (StringUtils.isNotBlank(sql)) {
            return sql;
        }
        buildSql(clz);
        buildAuditSql(clz);
        return sqlCache.getIfPresent(buildCacheKey(clz, type));
    }

    static String buildUpdateVersionSql(@NotNull List<String> version) {
        List<String> list = new ArrayList<>();
        version.forEach(item -> list.add(item.concat("=:").concat(item).concat("+1")));
        return StringUtils.join(list, ",");
    }
}
