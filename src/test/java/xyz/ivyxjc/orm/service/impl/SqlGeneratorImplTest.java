package xyz.ivyxjc.orm.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import javax.persistence.Version;
import org.junit.Assert;
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
    public void testInsertCachedSql()
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method methodGetCachedSql =
            SqlGeneratorImpl.class.getDeclaredMethod("getCachedSql", Class.class,
                JdbcOperationType.class);
        String sql = (String) methodGetCachedSql.invoke(sqlGenerator, DataBean.class,
            JdbcOperationType.INSERT);
        Assert.assertEquals(
            "INSERT INTO DATA_BEAN (GUID,UNIQUE_ID,EVENT_ID,VALUE_DATE,VERSION${INSERT_COLUMNS_PLACEHOLDER}) VALUES (hextoraw(:GUID),:UNIQUE_ID,:EVENT_ID,:VALUE_DATE,0${INSERT_VALUES_PLACEHOLDER})",
            sql);
    }

    @Test
    public void testUpdateCachedSql()
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method methodGetCachedSql =
            SqlGeneratorImpl.class.getDeclaredMethod("getCachedSql", Class.class,
                JdbcOperationType.class);
        String sql = (String) methodGetCachedSql.invoke(sqlGenerator, DataBean.class,
            JdbcOperationType.UPDATE);
        Assert.assertEquals(
            "UPDATE DATA_BEAN SET UNIQUE_ID=:UNIQUE_ID,EVENT_ID=:EVENT_ID,VALUE_DATE=:VALUE_DATE,VERSION=:VERSION+1${UPDATE_PLACEHOLDER} WHERE ",
            sql);
    }

    @Test
    public void testFinalSql() throws IllegalAccessException {
        DataBean dataBean = new DataBean();
        dataBean.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        String insertSql = sqlGenerator.getFinalSql(dataBean, JdbcOperationType.INSERT);
        String deleteSql = sqlGenerator.getFinalSql(dataBean, JdbcOperationType.DELETE);
        String updateSql = sqlGenerator.getFinalSql(dataBean, JdbcOperationType.UPDATE);
        String selectSql = sqlGenerator.getFinalSql(dataBean, JdbcOperationType.SELECT);
        Assert.assertEquals(
            "INSERT INTO DATA_BEAN (GUID,UNIQUE_ID,EVENT_ID,VALUE_DATE,VERSION,CREATED_AT,CREATED_BY) VALUES (hextoraw(:GUID),:UNIQUE_ID,:EVENT_ID,:VALUE_DATE,0,:CREATED_AT,sys_context('USERENV', 'SESSION_USER') )",
            insertSql);
        Assert.assertEquals("DELETE FROM DATA_BEAN WHERE ", deleteSql);
        Assert.assertEquals(
            "UPDATE DATA_BEAN SET UNIQUE_ID=:UNIQUE_ID,EVENT_ID=:EVENT_ID,VALUE_DATE=:VALUE_DATE,VERSION=:VERSION+1,UPDATED_AT=systimestamp,UPDATED_BY=sys_context('USERENV', 'SESSION_USER')  WHERE ",
            updateSql);
        Assert.assertEquals(
            "SELECT GUID,UNIQUE_ID,EVENT_ID,VALUE_DATE,CREATED_AT,CREATED_BY,UPDATED_AT,UPDATED_BY,VERSION FROM DATA_BEAN WHERE ",
            selectSql);
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

    @ZColumn(name = "UPDATED_AT", defaultValue = "systimestamp", insertable = false)
    private Timestamp updatedAt;

    @ZColumn(name = "UPDATED_BY", defaultValue = "sys_context('USERENV', 'SESSION_USER') ",
             insertable = false)
    private String updatedBy;

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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }
}


