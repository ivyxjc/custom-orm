package xyz.ivyxjc.orm.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * @author Ivyxjc
 * @since 11/17/2018
 */
@Data
public class DataBeanBO {
    private String gcGuid;
    private String uniqueId;
    private String eventId;
    private LocalDate valueData;

    public DataBeanPO convertToPO() {
        DataBeanPO dataBeanPO = new DataBeanPO();
        dataBeanPO.setGuid(this.gcGuid);
        dataBeanPO.setUniqueId(this.uniqueId);
        dataBeanPO.setEventId(this.eventId);
        //dataBeanPO.setValueDate(java.sql.Date.valueOf(this.valueData));
        dataBeanPO.setCreatedAt(java.sql.Timestamp.valueOf(LocalDateTime.now()));
        dataBeanPO.setCreatedBy("UTUT001");
        dataBeanPO.setUpdatedBy("UTUT001");
        return dataBeanPO;
    }

    public DataBeanBO buildBO(DataBeanPO po) {
        DataBeanBO bo = new DataBeanBO();
        bo.gcGuid = po.getGuid();
        bo.uniqueId = po.getUniqueId();
        bo.eventId = po.getEventId();
        bo.valueData = po.getValueDate().toLocalDate();
        return bo;
    }
}
