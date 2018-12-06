package xyz.ivyxjc.orm.bean;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import lombok.Data;
import xyz.ivyxjc.orm.annotation.AuditColumn;
import xyz.ivyxjc.orm.annotation.AuditTable;
import xyz.ivyxjc.orm.annotation.ZColumn;
import xyz.ivyxjc.orm.annotation.ZTable;
import xyz.ivyxjc.orm.interfaces.PoBean;

@Data
@AuditTable(name = "TEST_BEAN_AUDIT")
@ZTable(name = "TEST_BEAN")
@AuditColumn(name = "AUDITED_AT", defaultValue = "sysdate()")
@AuditColumn(name = "AUDITED_BY", defaultValue = "'UTUT001'")
@AuditColumn(name = "AUDIT_ACTION_CD")
public class TestBeanPO implements PoBean {

    //@ZColumn(name = "GUID", isRawType = true, updatable = false)
    //private String guid;

    @ZColumn(name = "UNIQUE_ID", unique = true, nullable = false, updatable = false)
    private String uniqueId;

    @ZColumn(name = "EVENT_ID")
    private String eventId;

    @ZColumn(name = "NUMBER_VALUE")
    private Long numberValue;

    @ZColumn(name = "DECIMAL_VALUE")
    private BigDecimal decimalValue;

    @ZColumn(name = "EVENT_DATE")
    private Date eventDate;

    @ZColumn(name = "VALUE_TIME")
    private Timestamp valueTime;

    @ZColumn(name = "Version")
    private Integer version;

    @ZColumn(name = "CREATED_AT", defaultValue = "systimestamp", updatable = false)
    private Timestamp createdAt;

    //@ZColumn(name = "CREATED_BY", defaultValue = "sys_context('USERENV','SESSION_USER')",
    //         updatable = false)
    //private String createdBy;
    //
    //@ZColumn(name = "CREATED_FROM", defaultValue = "sys_context('USERENV','HOST')",
    //         updatable = false)
    //private String createdFrom;

    @ZColumn(name = "UPDATED_AT", defaultValue = "systimestamp", insertable = false)
    private Timestamp updatedAt;

   /* @ZColumn(name = "UPDATED_BY", defaultValue = "sys_context('USERENV','SESSION_USER')",
             insertable = false)
    private String updatedBy;

    @ZColumn(name = "UPDATED_FROM", defaultValue = "sys_context('USERENV','HOST')",
             insertable = false)
    private String updatedFrom;*/
}
