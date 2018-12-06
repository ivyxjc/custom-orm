package xyz.ivyxjc.orm.service.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.DigestUtils;
import xyz.ivyxjc.orm.bean.TestBeanPO;
import xyz.ivyxjc.orm.service.AuditPersisitenceService;
import xyz.ivyxjc.orm.service.BeanPersistenceService;

/**
 * @author Ivyxjc
 * @since 11/22/2018
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@PropertySource( {"classpath:application-test.yaml"})
public class AuditPersistenceServiceImplTest {

    private static final Logger log = LoggerFactory.getLogger(BeanPersistenceServiceImpl.class);

    @Autowired
    private BeanPersistenceService beanPersistenceService;

    @Autowired
    private AuditPersisitenceService auditPersisitenceService;

    @Test
    public void update_0() throws IllegalAccessException {
        TestBeanPO po = new TestBeanPO();
        String guid = UUID.randomUUID().toString();
        guid = guid.replaceAll("-", "");
        //po.setGuid(guid);
        log.info("guid is: {}", guid);
        po.setUniqueId(UUID.randomUUID().toString().substring(0, 10));
        po.setNumberValue(230L);
        po.setDecimalValue(new BigDecimal(100));
        po.setEventId("ABC");
        po.setVersion(0);
        po.setEventDate(Date.valueOf(LocalDate.now()));
        po.setValueTime(Timestamp.valueOf(LocalDateTime.now()));
        //po.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        //po.setCreatedBy("UTUT001");
        //po.setCreatedFrom("abadsfs");
        beanPersistenceService.insert(po);
        auditPersisitenceService.insertAudit(po, "I");

        //DataBeanPO newPo = (DataBeanPO) beanPersistenceService.query(po, "GUID").get(0);
        //
        //newPo.setEventId("CDE");
        //beanPersistenceService.update(newPo, "GUID");

        //beanPersistenceService.insertAudit(newPo, AuditType.IN_APP, "U", "GUID");
    }

    @Test
    public void doSome2() throws UnsupportedEncodingException {
        String res = DigestUtils.md5DigestAsHex("abcdef".getBytes(StandardCharsets.UTF_8));
        System.out.print(StringUtils.upperCase(res));
    }
}
