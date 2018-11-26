package xyz.ivyxjc.orm.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import xyz.ivyxjc.orm.annotation.NotImplementedAPI;
import xyz.ivyxjc.orm.enumerations.AuditType;
import xyz.ivyxjc.orm.enumerations.JdbcOperationType;
import xyz.ivyxjc.orm.interfaces.PoBean;
import xyz.ivyxjc.orm.service.AuditPersisitenceService;

@Slf4j
public class AuditPersistenceServiceImpl implements AuditPersisitenceService {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public int insertAudit(PoBean poBean, AuditType type, String auditActionCd,
        @NotNull String... whereColumnsNames) {
        String sql = BeanDBUtils.getCachedSql(poBean.getClass(), JdbcOperationType.AUDIT_IN_APP);
        String whereClause = BeanDBUtils.buildWhereClause(whereColumnsNames);
        sql = sql.replace("${WHERE_CLAUSE}", whereClause);
        MapSqlParameterSource sqlParameterSource = BeanDBUtils.buildParameterSource(poBean);
        sqlParameterSource.addValue("AUDIT_ACTION_CD", auditActionCd);
        log.info("insert audit sql is: {}", sql);
        return jdbcTemplate.update(sql, sqlParameterSource);
    }

    @NotImplementedAPI
    @Override
    public int insertAudit(PoBean poBean, AuditType type, String auditActionCd) {
        return 0;
    }
}
