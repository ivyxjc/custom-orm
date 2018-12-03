package xyz.ivyxjc.orm.service.impl;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import xyz.ivyxjc.orm.annotation.ZColumn;
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
            .map(field -> field.getAnnotation(ZColumn.class))
            .filter(
                Objects::nonNull)
            .forEach(column -> columnsContainerImpl.addColumn(column));
        columnCache.putIfAbsent(clz.getName(), new UnmodifiedColumnContainer(columnsContainerImpl));

        return new UnmodifiedColumnContainer(columnsContainerImpl);
    }
}
