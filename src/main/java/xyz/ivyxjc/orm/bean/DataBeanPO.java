package xyz.ivyxjc.orm.bean;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.Data;
import xyz.ivyxjc.orm.annotation.AuditColumn;
import xyz.ivyxjc.orm.annotation.AuditTable;
import xyz.ivyxjc.orm.interfaces.PoBean;

/**
 * @author Ivyxjc
 * @since 11/14/2018
 */
@Data
@Table(name = "DATA_BEAN")
@AuditTable(name = "DATA_BEAN_AUDIT")
@AuditColumn(name = "AUDITED_AT", defaultTemplateValue = "sysdate()")
@AuditColumn(name = "AUDITED_BY", defaultStrValue = "UTUT001")
@AuditColumn(name = "AUDIT_ACTION_CD")
@Entity
public final class DataBeanPO implements PoBean {

    @Column(name = "GUID", unique = true, nullable = false)
    private String guid;

    @Column(name = "UNIQUE_ID", unique = true, nullable = false, updatable = false)
    private String uniqueId;

    @Column(name = "EVENT_ID")
    private String eventId;

    @Column(name = "VALUE_DATE")
    private LocalDateTime valueDate;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "CREATED_BY", nullable = false, updatable = false)
    private String createdBy;

    @Column(name = "UPDATED_AT", insertable = false)
    private Timestamp updatedAt;

    @Column(name = "UPDATED_BY", insertable = false)
    private String updatedBy;

    @Version
    @Column(name = "VERSION")
    private Integer version;
}
