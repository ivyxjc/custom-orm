package xyz.ivyxjc.orm.service.impl;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import xyz.ivyxjc.orm.annotation.ZColumn;
import xyz.ivyxjc.orm.enumerations.JdbcOperationType;
import xyz.ivyxjc.orm.enumerations.SupportedTypes;
import xyz.ivyxjc.orm.interfaces.PoBean;
import xyz.ivyxjc.orm.service.ColumnsContainer;

public class SqlDBUtils {

    private static ConcurrentHashMap<String, ColumnsContainer> columnCache =
        new ConcurrentHashMap<>();

    public static ColumnsContainer buildColumns(Class<? extends PoBean> clz) {
        if (columnCache.get(clz.getName()) != null) {
            return columnCache.get(clz.getName());
        }
        ColumnsContainerImpl columnsContainerImpl = new ColumnsContainerImpl();
        Arrays.stream(clz.getDeclaredFields())
            .filter(field -> {
                ZColumn column = field.getAnnotation(ZColumn.class);
                return column != null;
            })
            .forEach(field -> columnsContainerImpl.addColumn(field));
        columnCache.putIfAbsent(clz.getName(), new UnmodifiedColumnContainer(columnsContainerImpl));

        return new UnmodifiedColumnContainer(columnsContainerImpl);
    }

    /**
     * generate sql's key based on operation's type
     *
     * @param clz @Notnull
     */
    @NotNull
    static String buildCacheKey(@NotNull Class<? extends PoBean> clz,
        @NotNull JdbcOperationType type) {
        return clz.getName().concat(type.name());
    }

    @NotNull
    static String buildWhereClause(@NotNull PoBean poBean, @NotNull String... whereColumnNames) {
        ColumnsContainer container = buildColumns(poBean.getClass());
        container.getRawTypeSet();
        List<String> whereList = new ArrayList<>();
        Arrays.stream(whereColumnNames).forEach(t -> {
            if (container.getRawTypeSet().contains(t)) {
                whereList.add(t.concat("=hextoraw(:").concat(t).concat(")"));
            } else {
                whereList.add(t.concat("=:").concat(t));
            }
        });
        return StringUtils.join(whereList, " and ");
    }

    /**
     * build MapSqlParameterSource based on bean
     *
     * put the the value of the field which has Annotation Column
     *
     * @param poBean instance of (sub class of PoBean)
     *
     * @return
     */
    @NotNull
    static MapSqlParameterSource buildParameterSource(PoBean poBean) {
        final MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        Field[] fields = poBean.getClass().getDeclaredFields();
        Arrays.stream(fields)
            .filter(
                field -> {
                    ZColumn ann = field.getAnnotation(ZColumn.class);
                    return ann != null;
                })
            .forEach(
                field -> {
                    ZColumn ann = field.getAnnotation(ZColumn.class);
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
     * add value into parameterSource
     *
     * @param columnName
     * @param object
     * @param sqlParameterSource
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
}


