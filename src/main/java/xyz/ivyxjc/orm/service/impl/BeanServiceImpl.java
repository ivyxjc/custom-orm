package xyz.ivyxjc.orm.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import xyz.ivyxjc.orm.enumerations.JdbcOperationType;
import xyz.ivyxjc.orm.interfaces.PoBean;
import xyz.ivyxjc.orm.service.BeanService;

/**
 * @author Ivyxjc
 * @since 11/17/2018
 */
@Slf4j
@Repository
public class BeanServiceImpl implements BeanService {

    private static final String INSERT_SQL_TEMPLATE = "INSERT INTO %s (%s) VALUES (%s)";
    private static final String UPDATE_SQL_TEMPLATE = "UPDATE %s SET %s WHERE ";
    private static final String DELETE_SQL_TEMPLATE = "DELETE FROM %s WHERE ";
    private static final String SELECT_SQL_TEMPLATE = "SELECT %s FROM %s WHERE ";

    private static final Cache<String, String> sqlCache =
        CacheBuilder.newBuilder().maximumSize(300).build();

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * generate sql's key based on operation's type
     *
     * @param clz @Notnull
     * @param type
     * @return
     */
    private static String buildCacheKey(Class<? extends PoBean> clz, JdbcOperationType type) {
        return clz.getName().concat(type.name());
    }

    @Override
    public void insert(PoBean poBean) {
        String sql =
            sqlCache.getIfPresent(buildCacheKey(poBean.getClass(), JdbcOperationType.INSERT));
        if (StringUtils.isBlank(sql)) {
            BeanDBUtils.buildSql(poBean.getClass());
        }
        sql = sqlCache.getIfPresent(buildCacheKey(poBean.getClass(), JdbcOperationType.INSERT));

        MapSqlParameterSource sqlParameterSource = BeanDBUtils.buildParameterSource(poBean);
        log.info("insert sql is: {}", sql);
        jdbcTemplate.update(sql, sqlParameterSource);
    }

    @Override
    public void batchInsert(@NotNull List<PoBean> list) {
        if (list.isEmpty()) {
            return;
        }
        PoBean firstOne = list.get(0);
        String sql =
            sqlCache.getIfPresent(buildCacheKey(firstOne.getClass(), JdbcOperationType.INSERT));
        if (StringUtils.isBlank(sql)) {
            BeanDBUtils.buildSql(firstOne.getClass());
        }
        sql = sqlCache.getIfPresent(buildCacheKey(firstOne.getClass(), JdbcOperationType.INSERT));
        SqlParameterSource[] parameterSourceArray = new SqlParameterSource[list.size()];
        for (int i = 0; i < list.size(); i++) {
            parameterSourceArray[i] = BeanDBUtils.buildParameterSource(list.get(i));
        }
        Long t1 = System.currentTimeMillis();
        jdbcTemplate.batchUpdate(sql, parameterSourceArray);
        Long t2 = System.currentTimeMillis();
        log.info("sql execute time is: {}", t2 - t1);
    }

    @Override
    public void update(PoBean poBean, String... whereColumnsNames) {
    }
}
