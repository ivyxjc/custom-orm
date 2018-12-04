package xyz.ivyxjc.orm.service.impl;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import xyz.ivyxjc.orm.annotation.ZColumn;
import xyz.ivyxjc.orm.service.ColumnsContainer;

public class UnmodifiedColumnContainer implements ColumnsContainer {
    private List<ZColumn> columnList;
    private List<ZColumn> versionList;
    private List<Field> columnWithDefaultList;
    private List<ZColumn> columnWithoutDefaultList;
    private Set<String> rawTypeSet;

    public UnmodifiedColumnContainer(ColumnsContainer columnsContainer) {
        columnList = columnsContainer.getColumnList();
        versionList = columnsContainer.getVersionList();
        columnWithDefaultList = columnsContainer.getColumnWithDefaultList();
        columnWithoutDefaultList = columnsContainer.getColumnWithoutDefaultList();
        rawTypeSet = columnsContainer.getRawTypeSet();
    }

    @Override
    public void addColumn(Field field) {
        throw new UnsupportedOperationException();
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
        return Collections.unmodifiableList(columnWithoutDefaultList);
    }

    @Override
    public Set<String> getRawTypeSet() {
        return Collections.unmodifiableSet(rawTypeSet);
    }
}
