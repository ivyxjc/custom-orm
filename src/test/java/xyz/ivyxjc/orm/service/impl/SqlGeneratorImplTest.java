package xyz.ivyxjc.orm.service.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import javax.persistence.Version;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import xyz.ivyxjc.orm.annotation.ZColumn;
import xyz.ivyxjc.orm.annotation.ZTable;
import xyz.ivyxjc.orm.enumerations.JdbcOperationType;
import xyz.ivyxjc.orm.interfaces.PoBean;
import xyz.ivyxjc.orm.service.SqlGenerator;

@RunWith(SpringRunner.class)
@SpringBootTest
@PropertySource( {"classpath:application-test.yaml"})
public class SqlGeneratorImplTest {

    @Autowired
    private SqlGenerator sqlGenerator;

    @Test
    public void testInsert() {
        String sql = sqlGenerator.getCachedSql(DataBean.class, JdbcOperationType.INSERT);
        System.out.println(sql);
    }

    @Test
    public void testUpdate() {
        String sql = sqlGenerator.getCachedSql(DataBean.class, JdbcOperationType.UPDATE);
        System.out.println(sql);
    }
}

@ZTable(name = "DATA_BEAN")
class DataBean implements PoBean {

    @ZColumn(name = "GUID", isRawType = true, updatable = false)
    private String guid;

    @ZColumn(name = "UNIQUE_ID")
    private String uniqueId;

    @ZColumn(name = "EVENT_ID")
    private String eventId;

    @ZColumn(name = "VALUE_DATE")
    private LocalDateTime valueDate;

    @ZColumn(name = "CREATED_AT", defaultValue = "systimestamp", updatable = false)
    private Timestamp createdAt;

    @ZColumn(name = "CREATED_BY", defaultValue = "sys_context('USERENV', 'SESSION_USER') ",
             updatable = false)
    private String createdBy;

    @ZColumn(name = "VERSION", isVersion = true)
    private Version version;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public LocalDateTime getValueDate() {
        return valueDate;
    }

    public void setValueDate(LocalDateTime valueDate) {
        this.valueDate = valueDate;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}


