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
import xyz.ivyxjc.orm.interfaces.PoBean;
import xyz.ivyxjc.orm.service.impl.BeanServiceImpl;

/**
 * @author Ivyxjc
 * @since 11/21/2018
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@PropertySource( {"classpath:application-test.yaml"})
public class UpdateVersionTest {
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
        po.setVersion(0);
        beanService.insert(po);
        DataBeanPO queryPo = new DataBeanPO();
        queryPo.setGuid(guid);
        List<PoBean> list = beanService.query(queryPo, "GUID");
        ((DataBeanPO) list.get(0)).setEventId("CDE");
        //Updater updater = Updater.builder()
        //    .withClass(DataBeanPO.class)
        //    .withWhereColumnNames("GUID")
        //    .withUpdateType(UpdateType.ALL)
        //    .withVersionColumn("VERSION")
        //    .build();
        //beanService.update(list.get(0), updater);
        beanService.update(list.get(0), "GUID");
        List<PoBean> list2 = beanService.query(queryPo, "GUID");
        log.info("final res is: {}", list2.get(0));
        log.info("query result size: {}", list.size());
        list.forEach(t -> log.info("result is: {}", t));
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("CDE", ((DataBeanPO) list.get(0)).getEventId());
    }
}
