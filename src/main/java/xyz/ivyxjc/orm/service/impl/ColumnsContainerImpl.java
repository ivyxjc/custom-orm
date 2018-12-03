package xyz.ivyxjc.orm.service.impl;

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
    private List<ZColumn> columnWithDefaultList;
    private Set<ZColumn> rawTypeSet;

    public ColumnsContainerImpl() {
        columnList = new ArrayList<>();
        versionList = new ArrayList<>();
        columnWithDefaultList = new ArrayList<>();
        rawTypeSet = new HashSet<>();
    }

    public void addColumn(ZColumn column) {

        if (column.isVersion()) {
            versionList.add(column);
        } else if (StringUtils.isNotBlank(column.defaultValue())) {
            columnWithDefaultList.add(column);
        } else {
            columnList.add(column);
        }

        if (column.isRawType()) {
            rawTypeSet.add(column);
        }
    }

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
