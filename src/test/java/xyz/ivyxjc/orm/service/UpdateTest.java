package xyz.ivyxjc.orm.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import xyz.ivyxjc.orm.bean.DataBeanPO;
import xyz.ivyxjc.orm.enumerations.UpdateType;
import xyz.ivyxjc.orm.interfaces.PoBean;
import xyz.ivyxjc.orm.service.impl.BeanServiceImpl;
import xyz.ivyxjc.orm.service.impl.Updater;

/**
 * @author Ivyxjc
 * @since 11/21/2018
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@PropertySource( {"classpath:application-test.yaml"})
public class UpdateTest {
    private static final Logger log = LoggerFactory.getLogger(BeanServiceImpl.class);

    @Autowired
    private BeanService beanService;

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
        beanService.insert(po);
        DataBeanPO newPo = new DataBeanPO();
        newPo.setEventId("CDE");
        newPo.setGuid(guid);
        beanService.update(newPo, "GUID");
        List<PoBean> list = beanService.query(newPo, "GUID");
        log.info("query result size: {}", list.size());
        list.forEach(t -> log.info("result is: {}", t));
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("CDE", ((DataBeanPO) list.get(0)).getEventId());
    }

    @Test
    public void update_1() {
        DataBeanPO po = new DataBeanPO();
        String guid = UUID.randomUUID().toString();

        po.setGuid(guid);
        log.info("guid is: {}", guid);
        po.setUniqueId(UUID.randomUUID().toString().substring(0, 10));
        po.setEventId("ABC");
        po.setValueDate(LocalDateTime.now());
        po.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        po.setCreatedBy("UTUT001");
        beanService.insert(po);
        DataBeanPO newPo = new DataBeanPO();
        newPo.setEventId("CDE");
        newPo.setGuid(guid);
        Updater updater =
            Updater.builder()
                .withClass(DataBeanPO.class)
                .withUpdateColumns("EVENT_ID")
                .withUpdateType(UpdateType.CUSTOM)
                .withWhereColumnNames("GUID")
                .build();
        beanService.update(newPo, updater);
    }

    @Test
    public void update_2() {
        DataBeanPO po = new DataBeanPO();
        String guid = UUID.randomUUID().toString();
        String uniqueId = UUID.randomUUID().toString().substring(0, 9);
        po.setGuid(guid);
        po.setUniqueId(uniqueId);
        log.info("guid is: {}", guid);
        log.info("unique id is: {}", uniqueId);

        po.setEventId("ABC");
        po.setValueDate(LocalDateTime.now());
        po.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        po.setCreatedBy("UTUT001");
        beanService.insert(po);
        DataBeanPO newPo = new DataBeanPO();
        newPo.setEventId("CDE");
        newPo.setGuid(guid);
        newPo.setUniqueId(uniqueId);
        Updater updater =
            Updater.builder()
                .withClass(DataBeanPO.class)
                .withUpdateColumns("EVENT_ID")
                .withUpdateType(UpdateType.CUSTOM)
                .withWhereColumnNames("GUID", "UNIQUE_ID")
                .build();
        beanService.update(newPo, updater);
    }
}
