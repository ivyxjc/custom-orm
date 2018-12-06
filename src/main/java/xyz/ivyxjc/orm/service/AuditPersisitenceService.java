package xyz.ivyxjc.orm.service;

import java.util.Map;
import xyz.ivyxjc.orm.interfaces.PoBean;

public interface AuditPersisitenceService {

    int insertAuditBySubQuery(PoBean poBean, String auditActionCd,
        String... whereColumnsNames);

    int insertAuditBySubQuery(PoBean poBean, String auditActionCd, Map<String, String> map,
        String... whereColumnsNames);

    int insertAudit(PoBean poBean, String auditActionCd);

    int insertAudit(PoBean poBean, String auditActionCd, Map<String, String> map);
}
