package xyz.ivyxjc.orm.bean;

import java.sql.Date;
import java.sql.Timestamp;
import javax.persistence.Column;
import lombok.Data;
import xyz.ivyxjc.orm.interfaces.PoBean;

/**
 * @author Ivyxjc
 * @since 11/22/2018
 */
@Data
public final class DataBeanChildPO implements PoBean {
    @Column(name = "GUID")
    private String guid;

    @Column(name = "CHILD_EVENT")
    private String childEvent;

    @Column(name = "CHILD_DATE")
    private Date childDate;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "CREATED_BY", nullable = false, updatable = false)
    private String createdBy;

    @Column(name = "UPDATED_AT", insertable = false)
    private Timestamp updatedAt;

    @Column(name = "UPDATED_BY", insertable = false)
    private String updatedBy;
}
