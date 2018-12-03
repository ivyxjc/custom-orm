package xyz.ivyxjc.orm.service;

import java.util.List;
import java.util.Set;
import xyz.ivyxjc.orm.annotation.ZColumn;

public interface ColumnsContainer {

    void addColumn(ZColumn column);

    List<ZColumn> getColumnList();

    List<ZColumn> getVersionList();

    List<ZColumn> getColumnWithDefaultList();

    Set<ZColumn> getRawTypeSet();
}
