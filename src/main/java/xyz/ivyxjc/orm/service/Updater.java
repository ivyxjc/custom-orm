package xyz.ivyxjc.orm.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * @author Ivyxjc
 * @since 11/19/2018
 */
public class Updater {
    private List<String> updateColumns;
    private List<String> whereColumnNames;
    private List<String> whereCustomSql;

    private Updater(Builder builder) {
        this.updateColumns = builder.updateColumns;
        this.whereColumnNames = builder.whereColumnNames;
        this.whereCustomSql = builder.whereCustomSqls;
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<String> getUpdateColumns() {
        return updateColumns;
    }

    public List<String> getWhereColumnNames() {
        return whereColumnNames;
    }

    public List<String> getWhereCustomSql() {
        return whereCustomSql;
    }

    public static class Builder {
        @NotNull
        private final List<String> updateColumns;
        @NotNull
        private final List<String> whereColumnNames;
        @NotNull
        private final List<String> whereCustomSqls;

        private Builder() {
            updateColumns = new ArrayList<>();
            whereColumnNames = new ArrayList<>();
            whereCustomSqls = new ArrayList<>();
        }

        public Builder withUpdateColumns(@NotNull String... updateColumns) {
            this.updateColumns.addAll(Arrays.asList(updateColumns));
            return this;
        }

        public Builder withWhereColumnNames(@NotNull String... whereColumnNames) {
            this.whereColumnNames.addAll(Arrays.asList(whereColumnNames));
            return this;
        }

        public Builder withCustomSqls(@NotNull String... sqls) {
            this.whereCustomSqls.addAll(Arrays.asList(sqls));
            return this;
        }

        public Updater build() {
            return new Updater(this);
        }
    }
}
