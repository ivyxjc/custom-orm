package xyz.ivyxjc.orm.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Version;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import xyz.ivyxjc.orm.enumerations.JdbcOperationType;
import xyz.ivyxjc.orm.interfaces.PoBean;

@RunWith(SpringRunner.class)
@SpringBootTest
@PropertySource( {"classpath:application-test.yaml"})
public class BeanDBUtilsTest {

    private static final Logger log = LoggerFactory.getLogger(BeanDBUtilsTest.class);

    @Test
    public void testBuildSqlCache()
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method buildSqlMethod = BeanDBUtils.class.getDeclaredMethod("buildSql", Class.class);
        buildSqlMethod.setAccessible(true);
        buildSqlMethod.invoke(null, TestBean.class);

        String selectSql = BeanDBUtils.getCachedSql(TestBean.class, JdbcOperationType.SELECT);
        String insertSql = BeanDBUtils.getCachedSql(TestBean.class, JdbcOperationType.INSERT);
        String updateSql = BeanDBUtils.getCachedSql(TestBean.class, JdbcOperationType.UPDATE);
        String deleteSql = BeanDBUtils.getCachedSql(TestBean.class, JdbcOperationType.DELETE);

        log.info("selectSql is:{}", selectSql);
        log.info("insertSql is:{}", insertSql);
        log.info("deleteSql is:{}", deleteSql);
        log.info("updateSql is:{}", updateSql);

        Assert.assertEquals(
            "SELECT GUID,UNIQUE_ID,EVENT_ID,VALUE_DATE,CREATED_AT,VERSION FROM TEST_BEAN WHERE ",
            selectSql);
        Assert.assertEquals(
            "INSERT INTO TEST_BEAN (GUID,UNIQUE_ID,EVENT_ID,VALUE_DATE,CREATED_AT,VERSION) VALUES (:GUID,:UNIQUE_ID,:EVENT_ID,:VALUE_DATE,:CREATED_AT,:VERSION)",
            insertSql);
        Assert.assertEquals("DELETE FROM TEST_BEAN WHERE ", deleteSql);
        Assert.assertEquals(
            "UPDATE TEST_BEAN SET GUID=:GUID,EVENT_ID=:EVENT_ID,VALUE_DATE=:VALUE_DATE,VERSION=:VERSION,VERSION=:VERSION+1 WHERE ",
            updateSql);
    }
}

@Table(name = "TEST_BEAN")
class TestBean implements PoBean {

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

    @Version
    @Column(name = "VERSION")
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