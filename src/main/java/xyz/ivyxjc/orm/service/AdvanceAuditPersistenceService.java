package xyz.ivyxjc.orm.service;

import java.util.Map;
import org.jetbrains.annotations.NotNull;
import xyz.ivyxjc.orm.enumerations.AuditType;
import xyz.ivyxjc.orm.interfaces.PoBean;

public interface AdvanceAuditPersistenceService {
    int insertAudit(@NotNull PoBean poBean, @NotNull AuditType type, @NotNull
        Map<String, String> map,
        @NotNull String... whereColumnsNames);

    int insertAudit(PoBean poBean, AuditType type, Map<String, String> map);
}
