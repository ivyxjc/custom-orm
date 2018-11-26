package xyz.ivyxjc.orm.service.impl;

import com.google.protobuf.ExperimentalApi;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.Assert;
import xyz.ivyxjc.orm.annotation.NotImplementedAPI;
import xyz.ivyxjc.orm.enumerations.UpdateType;
import xyz.ivyxjc.orm.interfaces.PoBean;

/**
 * @author Ivyxjc
 * @since 11/19/2018
 */

/**
 * <b>Not thread-safe</b>
 * But if you just use Builder to build it once and not use reflect to modify it,
 * you can see it as a immutable object, and it is thread-safe to some extent
 */
@Slf4j
public class Updater {
    @Nullable
    private final List<String> updateColumnNames;
    @NotNull
    private final List<String> whereColumnNames;
    @Nullable
    private final List<String> whereCustomSql;
    @NotNull
    private UpdateType updateType;
    @NotNull
    private String updateSql;
    @Nullable
    private Set<String> updateColumnsSet;
    @NotNull
    private Set<String> whereColumnsSet;

    private Updater(Builder builder) {
        this.updateColumnNames = builder.updateColumns;
        this.whereColumnNames = builder.whereColumnNames;
        this.whereCustomSql = builder.whereCustomSqls;
        this.updateType = builder.updateType;
        this.updateSql = builder.updateSql;
        if (builder.updateColumns != null) {
            this.updateColumnsSet = new HashSet<>(builder.updateColumns);
        }
        if (builder.updateColumns == null) {
            this.updateColumnsSet = null;
        }
        this.whereColumnsSet = new HashSet<>(builder.whereColumnNames);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Nullable
    public List<String> getUpdateColumnNames() {
        return updateColumnNames;
    }

    @NotNull
    public List<String> getWhereColumnNames() {
        return whereColumnNames;
    }

    @Nullable
    public List<String> getWhereCustomSql() {
        return whereCustomSql;
    }

    @NotNull
    public UpdateType getUpdateType() {
        return updateType;
    }

    @NotNull
    public String getUpdateSql() {
        return updateSql;
    }

    @Nullable
    public Set<String> getUpdateColumnsSet() {
        return updateColumnsSet;
    }

    @NotNull
    public Set<String> getWhereColumnsSet() {
        return whereColumnsSet;
    }

    public static class Builder {
        private static final String UPDATE_SQL_TEMPLATE = "UPDATE %s SET %s WHERE ";
        private final List<String> whereColumnNames;
        @Nullable
        private final List<String> whereCustomSqls;
        @Nullable
        private final List<String> updateColumns;
        private Class<? extends PoBean> beanClz;
        private UpdateType updateType;
        private String updateSql;
        @Nullable
        private final List<String> versions;

        private Builder() {
            updateColumns = new ArrayList<>();
            whereColumnNames = new ArrayList<>();
            whereCustomSqls = new ArrayList<>();
            versions = new ArrayList<>();
            updateType = UpdateType.ALL;
        }

        public Builder withClass(Class<? extends PoBean> clz) {
            this.beanClz = clz;
            return this;
        }

        public Builder withUpdateColumns(@NotNull String... updateColumns) {
            this.updateColumns.addAll(Arrays.asList(updateColumns));
            return this;
        }

        public Builder withWhereColumnNames(@NotNull String... whereColumnNames) {
            this.whereColumnNames.addAll(Arrays.asList(whereColumnNames));
            return this;
        }

        @NotImplementedAPI
        @ExperimentalApi
        public Builder withCustomSqls(@NotNull String... sqls) {

            this.whereCustomSqls.addAll(Arrays.asList(sqls));
            return this;
        }

        public Builder withUpdateType(@NotNull UpdateType type) {
            this.updateType = type;
            return this;
        }

        public Builder withVersionColumn(String... versionColumn) {
            this.versions.addAll(Arrays.asList(versionColumn));
            return this;
        }

        public Updater build() {
            ColumnManager columnManager = new ColumnManager();
            Table table = beanClz.getDeclaredAnnotation(Table.class);
            switch (updateType) {
                case ALL:
                    Field[] fields = beanClz.getDeclaredFields();
                    Arrays.stream(fields)
                        .map(item -> {
                            if (item.getAnnotation(Version.class) == null) {
                                return item.getAnnotation(Column.class);
                            } else {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .forEach(
                            column -> {
                                if (column.updatable()) {
                                    columnManager.addUpdateColumn(column.name());
                                }
                            });
                    break;
                case CUSTOM:
                    Assert.notNull(
                        updateColumns,
                        "If you choose custom update type, the update columns should not be null");
                    Assert.notEmpty(
                        updateColumns,
                        "If you choose custom update type, the update columns should not be empty");
                    updateColumns.forEach(t -> columnManager.addUpdateColumn(t));
            }

            String updateClause = StringUtils.join(columnManager.getUpdateColumns(), ",");
            if (!versions.isEmpty()) {
                updateClause =
                    updateClause.concat(",").concat(BeanDBUtils.buildUpdateVersionSql(versions));
            }
            String updateSql = String.format(UPDATE_SQL_TEMPLATE, table.name(), updateClause);
            whereColumnNames.forEach(t -> columnManager.addWhereColumn(t));
            versions.forEach(t -> columnManager.addWhereColumn(t));
            String whereClause = StringUtils.join(columnManager.getWhereColumns(), " and ");
            this.updateSql = updateSql.concat(whereClause);
            log.info("update sql is: {}", this.updateSql);
            return new Updater(this);
        }
    }
}
