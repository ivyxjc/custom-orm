package xyz.ivyxjc.orm.service;

import xyz.ivyxjc.orm.enumerations.AuditType;
import xyz.ivyxjc.orm.interfaces.PoBean;

public interface AuditPersisitenceService {

    int insertAudit(PoBean poBean, AuditType type, String auditActionCd,
        String... whereColumnsNames);

    int insertAudit(PoBean poBean, AuditType type, String auditActionCd);
}
