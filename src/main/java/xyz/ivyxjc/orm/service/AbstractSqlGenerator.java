package xyz.ivyxjc.orm.service;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import xyz.ivyxjc.orm.annotation.ZColumn;
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

    protected String buildColumnValue(@Nonnull ZColumn column, @Nonnull JdbcOperationType type) {

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

    @Nonnull
    protected void buildBasciSql(@Nonnull ColumnsContainer columnsContainer,
        @Nonnull List<String> columns,
        @Nonnull List<String> values,
        @Nonnull JdbcOperationType type) {
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

    @Nonnull
    protected String buildUpdateSql(@Nonnull Class<? extends PoBean> clz, @Nonnull String table) {
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
                updateClausesSql.concat(",").concat(CommonConstants.UPDATE_PLACEHOLDER);
        }
        return String.format(UPDATE_SQL_TEMPLATE, table, updateClausesSql);
    }

    @Nonnull
    protected String buildInsertSql(@Nonnull Class<? extends PoBean> clz, @Nonnull String table) {
        ColumnsContainer columnsContainer = getCachedColumnsContainer(clz);
        List<String> insertColumns = new ArrayList<>();
        List<String> insertValues = new ArrayList<>();
        buildBasciSql(columnsContainer, insertColumns, insertValues, JdbcOperationType.INSERT);
        String columnsSql = StringUtils.join(insertColumns, ",");
        String valuesSql = StringUtils.join(insertValues, ",");
        if (!columnsContainer.getColumnWithDefaultList().isEmpty()) {
            columnsSql = columnsSql.concat(",").concat(CommonConstants.INSERT_COLUMNS_PLACEHOLDER);
            valuesSql = valuesSql.concat(",").concat(CommonConstants.INSERT_VALUES_PLACEHOLDER);
        }
        return String.format(INSERT_SQL_TEMPLATE, table, columnsSql,
            valuesSql);
    }

    @Nonnull
    protected String buildDeleteSql(@Nonnull String table) {
        return String.format(DELETE_SQL_TEMPLATE, table);
    }

    @Nonnull
    protected String buildSelectSql(@Nonnull Class<? extends PoBean> clz, @Nonnull String table) {
        return "";
    }
}

