package xyz.ivyxjc.orm.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import xyz.ivyxjc.orm.annotation.ZColumn;
import xyz.ivyxjc.orm.service.ColumnsContainer;

public class UnmodifiedColumnContainer implements ColumnsContainer {
    private List<ZColumn> columnList;
    private List<ZColumn> versionList;
    private List<ZColumn> columnWithDefaultList;
    private Set<ZColumn> rawTypeSet;

    public UnmodifiedColumnContainer(ColumnsContainer columnsContainer) {
        columnList = columnsContainer.getColumnList();
        versionList = columnsContainer.getVersionList();
        columnWithDefaultList = columnsContainer.getColumnWithDefaultList();
        rawTypeSet = columnsContainer.getRawTypeSet();
    }

    @Override
    public void addColumn(ZColumn column) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ZColumn> getColumnList() {
        return Collections.unmodifiableList(columnList);
    }

    public List<ZColumn> getVersionList() {
        return Collections.unmodifiableList(versionList);
    }

    public List<ZColumn> getColumnWithDefaultList() {
        return Collections.unmodifiableList(columnWithDefaultList);
    }

    public Set<ZColumn> getRawTypeSet() {
        return Collections.unmodifiableSet(rawTypeSet);
    }
}
