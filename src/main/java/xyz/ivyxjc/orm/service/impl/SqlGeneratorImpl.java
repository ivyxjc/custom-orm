package xyz.ivyxjc.orm.service.impl;

import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import xyz.ivyxjc.orm.annotation.ZTable;
import xyz.ivyxjc.orm.enumerations.JdbcOperationType;
import xyz.ivyxjc.orm.interfaces.PoBean;
import xyz.ivyxjc.orm.service.AbstractSqlGenerator;
import xyz.ivyxjc.orm.service.ColumnsContainer;

@Service
public class SqlGeneratorImpl extends AbstractSqlGenerator {

    private static final ConcurrentHashMap<String, String> sqlCache = new ConcurrentHashMap<>();

    @Override
    public String getFinalSql(PoBean poBean, JdbcOperationType type) throws IllegalAccessException {
        String cachedSql = getCachedSql(poBean.getClass(), type);

        if (JdbcOperationType.DELETE.equals(type) || JdbcOperationType.SELECT.equals(type)) {
            return cachedSql;
        }
        ColumnsContainer columnsContainer = getCachedColumnsContainer(poBean.getClass());

        if (JdbcOperationType.INSERT.equals(type)) {
            return buildInsertFinalSql(cachedSql, poBean, columnsContainer);
        }
        if (JdbcOperationType.UPDATE.equals(type)) {
            return buildUpdateFinalSql(cachedSql, poBean, columnsContainer);
        }
        return cachedSql;
    }

    public String getCachedSql(Class<? extends PoBean> clz, JdbcOperationType type) {
        ZTable table = clz.getAnnotation(ZTable.class);
        if (table == null) {
            throw new RuntimeException("Bean should have annotation ZTable");
        }
        String cachedSql = sqlCache.get(SqlDBUtils.buildCacheKey(clz, type));
        if (StringUtils.isNotBlank(cachedSql)) {
            return cachedSql;
        }

        switch (type) {
            case INSERT:
                cachedSql = buildInsertSql(clz, table.name());
                sqlCache.putIfAbsent(SqlDBUtils.buildCacheKey(clz, type), cachedSql);
                return cachedSql;
            case UPDATE:
                cachedSql = buildUpdateSql(clz, table.name());
                sqlCache.putIfAbsent(SqlDBUtils.buildCacheKey(clz, type), cachedSql);
                return cachedSql;
            case DELETE:
                cachedSql = buildDeleteSql(table.name());
                sqlCache.putIfAbsent(SqlDBUtils.buildCacheKey(clz, type), cachedSql);
                return cachedSql;
            case SELECT:
                cachedSql = buildSelectSql(clz, table.name());
                sqlCache.putIfAbsent(SqlDBUtils.buildCacheKey(clz, type), cachedSql);
                return cachedSql;
            default:
                return null;
        }
    }
}
