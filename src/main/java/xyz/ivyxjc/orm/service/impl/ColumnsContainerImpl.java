package xyz.ivyxjc.orm.service.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import xyz.ivyxjc.orm.annotation.ZColumn;
import xyz.ivyxjc.orm.service.ColumnsContainer;

public class ColumnsContainerImpl implements ColumnsContainer {
    private List<ZColumn> columnList;
    private List<ZColumn> versionList;
    private List<Field> columnWithDefaultList;
    private List<ZColumn> columnWithoutDefaultList;
    private Set<String> rawTypeSet;

    public ColumnsContainerImpl() {
        columnList = new ArrayList<>();
        versionList = new ArrayList<>();
        columnWithoutDefaultList = new ArrayList<>();
        columnWithDefaultList = new ArrayList<>();
        rawTypeSet = new HashSet<>();
    }

    @Override
    public void addColumn(Field field) {
        ZColumn column = field.getAnnotation(ZColumn.class);
        columnList.add(column);
        if (StringUtils.isNotBlank(column.defaultValue())) {
            field.setAccessible(true);
            columnWithDefaultList.add(field);
        } else {
            columnWithoutDefaultList.add(column);
        }

        if (column.isRawType()) {
            rawTypeSet.add(column.name());
        }
        if (column.isVersion()) {
            versionList.add(column);
        }
    }

    @Override
    public List<ZColumn> getColumnList() {
        return Collections.unmodifiableList(columnList);
    }

    @Override
    public List<ZColumn> getVersionList() {
        return Collections.unmodifiableList(versionList);
    }

    @Override
    public List<Field> getColumnWithDefaultList() {
        return Collections.unmodifiableList(columnWithDefaultList);
    }

    @Override
    public List<ZColumn> getColumnWithoutDefaultList() {
        return columnWithoutDefaultList;
    }

    @Override
    public Set<String> getRawTypeSet() {
        return Collections.unmodifiableSet(rawTypeSet);
    }
}
