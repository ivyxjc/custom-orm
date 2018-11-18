package xyz.ivyxjc.orm.service.impl;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
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

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void insert(PoBean poBean) {
        String sql = BeanDBUtils.getCachedSql(poBean.getClass(), JdbcOperationType.INSERT);

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
        String sql = BeanDBUtils.getCachedSql(firstOne.getClass(), JdbcOperationType.INSERT);

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
        String sql = BeanDBUtils.getCachedSql(poBean.getClass(), JdbcOperationType.UPDATE);
        String whereClause = BeanDBUtils.buildWhereClause(poBean.getClass(), whereColumnsNames);
        MapSqlParameterSource sqlParameterSource = BeanDBUtils.buildParameterSource(poBean);
        jdbcTemplate.update(sql.concat(whereClause), sqlParameterSource);
    }
}
