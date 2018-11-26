package xyz.ivyxjc.orm.service;

import java.util.List;
import java.util.Map;
import xyz.ivyxjc.orm.enumerations.AuditType;
import xyz.ivyxjc.orm.interfaces.PoBean;
import xyz.ivyxjc.orm.service.impl.Updater;

/**
 * @author Ivyxjc
 * @since 11/17/2018
 */
public interface BeanPersistenceService {

    List<PoBean> query(PoBean poBean, String... whereColumnNames);

    int insert(PoBean poBean);

    int[] batchInsert(List<PoBean> poBean);

    int update(PoBean poBean, String... whereColumnsNames);

    int update(PoBean poBean, Updater updater);

    int delete(PoBean poBean, String... whereColumnsNames);

    int insertAudit(PoBean poBean, AuditType type, Map<String, String> map,
        String... whereColumnsNames);

    int insertAudit(PoBean poBean, AuditType type, String auditActionCd,
        String... whereColumnsNames);

    int insertAudit(PoBean poBean, AuditType type, Map<String, String> map);

    int insertAudit(PoBean poBean, AuditType type, String auditActionCd);
}
