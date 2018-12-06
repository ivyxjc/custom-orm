package xyz.ivyxjc.orm.service.impl;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import xyz.ivyxjc.orm.annotation.NotImplementedAPI;
import xyz.ivyxjc.orm.enumerations.JdbcOperationType;
import xyz.ivyxjc.orm.interfaces.PoBean;
import xyz.ivyxjc.orm.service.AuditPersisitenceService;
import xyz.ivyxjc.orm.service.SqlGenerator;

@Slf4j
@Service
public class AuditPersistenceServiceImpl implements AuditPersisitenceService {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private SqlGenerator sqlGenerator;

    @Override
    public int insertAuditBySubQuery(@NotNull PoBean poBean, @NotNull String auditActionCd,
        @NotNull String... whereColumnsNames) {
        String sql = sqlGenerator.getFinalAuditSql(poBean, JdbcOperationType.AUDIT_IN_APP);
        String whereClause = SqlDBUtils.buildWhereClause(poBean, whereColumnsNames);
        sql = sql.replace("${WHERE_CLAUSE}", whereClause);
        MapSqlParameterSource sqlParameterSource = SqlDBUtils.buildParameterSource(poBean);
        sqlParameterSource.addValue("AUDIT_ACTION_CD", auditActionCd);
        log.debug("insert audit sql is: {}", sql);
        return jdbcTemplate.update(sql, sqlParameterSource);
    }

    @NotImplementedAPI
    @Override
    public int insertAudit(PoBean poBean, String auditActionCd) {
        return 0;
    }

    @Override
    public int insertAuditBySubQuery(PoBean poBean, String auditActionCd, Map<String, String> map,
        String... whereColumnsNames) {
        return 0;
    }

    @Override
    public int insertAudit(PoBean poBean, String auditActionCd, Map<String, String> map) {
        return 0;
    }
}
