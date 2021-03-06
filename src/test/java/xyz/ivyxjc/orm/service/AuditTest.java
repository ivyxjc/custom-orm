package xyz.ivyxjc.orm.service;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import xyz.ivyxjc.orm.bean.DataBeanPO;
import xyz.ivyxjc.orm.service.impl.BeanPersistenceServiceImpl;

/**
 * @author Ivyxjc
 * @since 11/22/2018
 */

//@RunWith(SpringRunner.class)
//@SpringBootTest
//@PropertySource( {"classpath:application-test.yaml"})
public class AuditTest {

    private static final Logger log = LoggerFactory.getLogger(BeanPersistenceServiceImpl.class);

    @Autowired
    private BeanPersistenceService beanPersistenceService;

    @Test
    public void update_0() {
        DataBeanPO po = new DataBeanPO();
        String guid = UUID.randomUUID().toString();

        po.setGuid(guid);
        log.info("guid is: {}", guid);
        po.setUniqueId(UUID.randomUUID().toString().substring(0, 10));
        po.setEventId("ABC");
        po.setValueDate(LocalDateTime.now());
        po.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        po.setCreatedBy("UTUT001");
        beanPersistenceService.insert(po);
        //beanPersistenceService.insertAudit(po, AuditType.IN_APP, "I", "GUID");

        DataBeanPO newPo = (DataBeanPO) beanPersistenceService.query(po, "GUID").get(0);

        newPo.setEventId("CDE");
        beanPersistenceService.update(newPo, "GUID");

        //beanPersistenceService.insertAudit(newPo, AuditType.IN_APP, "U", "GUID");
    }

    @Test
    public void doSome2() throws UnsupportedEncodingException {
        String res = DigestUtils.md5DigestAsHex("abcdef".getBytes("utf-8"));
        System.out.print(StringUtils.upperCase(res));
    }
}
