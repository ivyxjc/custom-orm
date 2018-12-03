package xyz.ivyxjc.orm.service.impl;

import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import xyz.ivyxjc.orm.annotation.ZTable;
import xyz.ivyxjc.orm.enumerations.JdbcOperationType;
import xyz.ivyxjc.orm.interfaces.PoBean;
import xyz.ivyxjc.orm.service.AbstractSqlGenerator;

@Service
public class SqlGeneratorImpl extends AbstractSqlGenerator {

    private static final ConcurrentHashMap<String, String> sqlCache = new ConcurrentHashMap<>();

    /**
     * generate sql's key based on operation's type
     *
     * @param clz @Notnull
     */
    @NotNull
    static String buildCacheKey(@NotNull Class<? extends PoBean> clz,
        @NotNull JdbcOperationType type) {
        return clz.getName().concat(type.name());
    }

    @Override
    public String getCachedSql(Class<? extends PoBean> clz, JdbcOperationType type) {
        ZTable table = clz.getAnnotation(ZTable.class);
        if (table == null) {
            throw new RuntimeException("Bean should have annotation ZTable");
        }
        String cachedSql = sqlCache.get(buildCacheKey(clz, type));
        if (StringUtils.isNotBlank(cachedSql)) {
            return cachedSql;
        }

        switch (type) {
            case INSERT:
                cachedSql = buildInsertSql(clz, table.name());
                sqlCache.putIfAbsent(buildCacheKey(clz, type), cachedSql);
                return cachedSql;
            case UPDATE:
                cachedSql = buildUpdateSql(clz, table.name());
                sqlCache.putIfAbsent(buildCacheKey(clz, type), cachedSql);
                return cachedSql;
            case DELETE:
                cachedSql = buildDeleteSql(table.name());
                sqlCache.putIfAbsent(buildCacheKey(clz, type), cachedSql);
                return cachedSql;
            case SELECT:
                cachedSql = buildDeleteSql(table.name());
                sqlCache.putIfAbsent(buildCacheKey(clz, type), cachedSql);
                return cachedSql;
            default:
                return null;
        }
    }
}
