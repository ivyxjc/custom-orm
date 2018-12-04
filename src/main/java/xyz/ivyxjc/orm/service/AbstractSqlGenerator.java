package xyz.ivyxjc.orm.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import xyz.ivyxjc.orm.annotation.ZColumn;
import xyz.ivyxjc.orm.annotation.ZTable;
import xyz.ivyxjc.orm.enumerations.JdbcOperationType;
import xyz.ivyxjc.orm.interfaces.PoBean;
import xyz.ivyxjc.orm.service.impl.SqlDBUtils;
import xyz.ivyxjc.orm.utils.CommonConstants;

public abstract class AbstractSqlGenerator implements SqlGenerator {

    protected static final String INSERT_SQL_TEMPLATE = "INSERT INTO %s (%s) VALUES (%s)";
    protected static final String UPDATE_SQL_TEMPLATE = "UPDATE %s SET %s WHERE ";
    protected static final String DELETE_SQL_TEMPLATE = "DELETE FROM %s WHERE ";
    protected static final String SELECT_SQL_TEMPLATE = "SELECT %s FROM %s WHERE ";

    protected ColumnsContainer getCachedColumnsContainer(Class<? extends PoBean> clz) {
        return SqlDBUtils.buildColumns(clz);
    }

    protected String buildColumnValue(@NotNull ZColumn column, @NotNull JdbcOperationType type) {

        if (column.isRawType()) {
            return "hextoraw(:".concat(column.name()).concat(")");
        }
        if (column.isVersion()) {
            switch (type) {
                case INSERT:
                    return "0";
                case UPDATE:
                    return ":".concat(column.name()).concat("+1");
                default:
                    return "0";
            }
        }

        return ":".concat(column.name());
    }

    @NotNull
    protected void buildBasciSql(@NotNull ColumnsContainer columnsContainer,
        @NotNull List<String> columns,
        @NotNull List<String> values,
        @NotNull JdbcOperationType type) {
        columnsContainer.getColumnList().stream().filter(column -> {
            switch (type) {
                case INSERT:
                    return column.insertable();
                case UPDATE:
                    return column.updatable();
                default:
                    return true;
            }
        }).forEach(column -> {
            columns.add(column.name());
            values.add(buildColumnValue(column, type));
        });
    }

    @NotNull
    protected String buildUpdateSql(@NotNull Class<? extends PoBean> clz, @NotNull String table) {
        ColumnsContainer columnsContainer = getCachedColumnsContainer(clz);
        List<String> updateColumns = new ArrayList<>();
        List<String> updateValues = new ArrayList<>();
        buildBasciSql(columnsContainer, updateColumns, updateValues, JdbcOperationType.UPDATE);
        List<String> updateClauses = new ArrayList<>();
        for (int i = 0; i < updateColumns.size(); i++) {
            updateClauses.add(updateColumns.get(i).concat("=").concat(updateValues.get(i)));
        }
        String updateClausesSql = StringUtils.join(updateClauses, ",");
        if (!columnsContainer.getColumnWithDefaultList().isEmpty()) {
            updateClausesSql =
                updateClausesSql.concat(CommonConstants.UPDATE_PLACEHOLDER);
        }
        return String.format(UPDATE_SQL_TEMPLATE, table, updateClausesSql);
    }

    @NotNull
    protected String buildInsertSql(@NotNull Class<? extends PoBean> clz, @NotNull String table) {
        ColumnsContainer columnsContainer = getCachedColumnsContainer(clz);
        List<String> insertColumns = new ArrayList<>();
        List<String> insertValues = new ArrayList<>();
        buildBasciSql(columnsContainer, insertColumns, insertValues, JdbcOperationType.INSERT);
        String columnsSql = StringUtils.join(insertColumns, ",");
        String valuesSql = StringUtils.join(insertValues, ",");
        if (!columnsContainer.getColumnWithDefaultList().isEmpty()) {
            columnsSql = columnsSql.concat(CommonConstants.INSERT_COLUMNS_PLACEHOLDER);
            valuesSql = valuesSql.concat(CommonConstants.INSERT_VALUES_PLACEHOLDER);
        }
        return String.format(INSERT_SQL_TEMPLATE, table, columnsSql,
            valuesSql);
    }

    @NotNull
    protected String buildDeleteSql(@NotNull String table) {
        return String.format(DELETE_SQL_TEMPLATE, table);
    }

    @NotNull
    protected String buildSelectSql(@NotNull Class<? extends PoBean> clz, @NotNull String table) {
        ZTable zTable = clz.getAnnotation(ZTable.class);
        ColumnsContainer columnsContainer = getCachedColumnsContainer(clz);
        List<String> selectColumns = new ArrayList<>();
        columnsContainer.getColumnList().forEach(t -> selectColumns.add(t.name()));

        return String.format(SELECT_SQL_TEMPLATE, zTable.name(),
            StringUtils.join(selectColumns, ","));
    }

    @NotNull
    protected String buildInsertFinalSql(@NotNull String cachedSql, @NotNull PoBean poBean,
        @NotNull ColumnsContainer columnsContainer) throws IllegalAccessException {
        List<String> insertColumnList = new ArrayList<>();
        List<String> insertValueList = new ArrayList<>();
        for (Field field : columnsContainer.getColumnWithDefaultList()) {
            ZColumn column = field.getAnnotation(ZColumn.class);
            if (field.get(poBean) == null) {
                insertColumnList.add(column.name());
                insertValueList.add(column.defaultValue());
            } else {
                insertColumnList.add(column.name());
                insertValueList.add(":".concat(column.name()));
            }
        }

        if (!insertColumnList.isEmpty()) {
            String insertColumnSql = StringUtils.join(insertColumnList, ",");
            String insertValueSql = StringUtils.join(insertValueList, ",");
            cachedSql = cachedSql.replace(CommonConstants.INSERT_COLUMNS_PLACEHOLDER,
                ",".concat(insertColumnSql));
            cachedSql = cachedSql.replace(CommonConstants.INSERT_VALUES_PLACEHOLDER,
                ",".concat(insertValueSql));
            return cachedSql;
        } else {
            return cachedSql;
        }
    }

    @NotNull
    protected String buildUpdateFinalSql(@NotNull String cachedSql, @NotNull PoBean poBean,
        @NotNull ColumnsContainer columnsContainer) throws IllegalAccessException {
        List<String> updateList = new ArrayList<>();
        for (Field field : columnsContainer.getColumnWithDefaultList()) {
            ZColumn column = field.getAnnotation(ZColumn.class);
            if (field.get(poBean) == null) {
                updateList.add(column.name().concat("=").concat(column.defaultValue()));
            } else {
                updateList.add(column.name().concat("=").concat(column.name()));
            }
        }

        if (!updateList.isEmpty()) {
            String updateClause = StringUtils.join(updateList, ",");
            cachedSql = cachedSql.replace(CommonConstants.UPDATE_PLACEHOLDER,
                ",".concat(updateClause));
            return cachedSql;
        } else {
            return cachedSql;
        }
    }
}

