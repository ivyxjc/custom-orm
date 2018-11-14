package xyz.ivyxjc.orm.bean;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Version;
import xyz.ivyxjc.orm.interfaces.StoreBean;

/**
 * @author Ivyxjc
 * @since 11/14/2018
 */
@Table(name = "DATA_REPO")
public final class DataBean implements StoreBean {

    @Column(name = "GC_GUID", unique = true, nullable = false)
    private String guid;

    @Column(name = "UNIQUE_ID", unique = true, nullable = false, updatable = false)
    private String uniqueId;

    @Column(name = "EVENT_ID")
    private String eventId;

    @Column(name = "VALUE_DATE")
    private Date valueDate;

    @Version
    private Integer version;

    public static void main(String[] args) {
        DataBean dataBean = new DataBean();
        dataBean.insertSql();
    }

    private String insertSql() {
        String insertSql = "INSERT INTO %s (%s) VALUES (%s)";
        Field[] fields = DataBean.class.getDeclaredFields();
        StringBuilder columnSB = new StringBuilder();
        StringBuilder valueSB = new StringBuilder();
        Arrays.stream(fields).map(item -> {
            Column[] anns = item.getAnnotationsByType(Column.class);
            if (anns != null && anns.length == 1) {
                return anns[0];
            } else {
                return null;
            }
        }).filter(Objects::nonNull).forEach(column -> {
            if (column.insertable()) {
                columnSB.append(column.name() + ",");
                valueSB.append(":" + column.name() + ",");
            }
        });
        String column = "";
        String value = "";
        if (columnSB.length() > 0) {
            column = columnSB.substring(0, columnSB.length() - 1);
            value = valueSB.substring(0, valueSB.length() - 1);
        }
        insertSql = String.format(insertSql, "DATE_REPO", column, value);
        System.out.println(insertSql);
        return insertSql;
    }
}
