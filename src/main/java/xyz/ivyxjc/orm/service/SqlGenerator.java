package xyz.ivyxjc.orm.service;

import xyz.ivyxjc.orm.enumerations.JdbcOperationType;
import xyz.ivyxjc.orm.interfaces.PoBean;

public interface SqlGenerator {

    String getFinalSql(PoBean poBean, JdbcOperationType type) throws IllegalAccessException;

}
