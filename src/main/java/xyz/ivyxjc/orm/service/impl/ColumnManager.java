package xyz.ivyxjc.orm.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * @author Ivyxjc
 * @since 11/19/2018
 */

/**
 * this column manager is just designed for the use of BeanDBUtils, do not user it in other place
 */
final class ColumnManager {
    @NotNull
    private final List<String> selectColumns;
    @NotNull
    private final List<String> insertColumns;
    @NotNull
    private final List<String> insertValueColumns;
    @NotNull
    private final List<String> updateColumns;
    @NotNull
    private final List<String> whereColumns;

    ColumnManager() {
        this.selectColumns = new ArrayList<>();
        this.insertColumns = new ArrayList<>();
        this.insertValueColumns = new ArrayList<>();
        this.updateColumns = new ArrayList<>();
        this.whereColumns = new ArrayList<>();
    }

    void addSelectColumn(@NotNull String column) {
        selectColumns.add(column);
    }

    void addInsertColumn(@NotNull String column) {
        insertColumns.add(column);
        insertValueColumns.add(":".concat(column));
    }

    void addUpdateColumn(@NotNull String column) {
        updateColumns.add(column.concat("=:").concat(column));
    }

    void addWhereColumn(@NotNull String column) {
        whereColumns.add(column.concat("=:".concat(column)));
    }

    @NotNull
    List<String> getSelectColumns() {
        return selectColumns;
    }

    @NotNull
    List<String> getInsertColumns() {
        return insertColumns;
    }

    @NotNull
    List<String> getUpdateColumns() {
        return updateColumns;
    }

    @NotNull
    List<String> getInsertValueColumns() {
        return insertValueColumns;
    }

    @NotNull
    public List<String> getWhereColumns() {
        return whereColumns;
    }
}
