package xyz.ivyxjc.orm.service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import xyz.ivyxjc.orm.annotation.ZColumn;

public interface ColumnsContainer {

    void addColumn(Field field);

    List<ZColumn> getColumnList();

    List<Field> getColumnWithDefaultList();

    List<ZColumn> getColumnWithoutDefaultList();

    List<ZColumn> getVersionList();

    Set<String> getRawTypeSet();
}
