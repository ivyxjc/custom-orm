package xyz.ivyxjc.orm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Ivyxjc
 * @since 11/14/2018
 */
@Slf4j
@SpringBootApplication
@PropertySource( {"classpath:application.yaml", "classpath:database/jdbc.properties"})
public class Runner {
    public static void main(String[] args) {
        SpringApplication.run(Runner.class);
    }

    //@Component
    //public class BasicRunner implements CommandLineRunner {
    //    @Autowired private JdbcTemplate jdbcTemplate;
    //
    //    @Autowired private BeanPersistenceServiceImpl beanServiceImpl;
    //
    //    @Override
    //    public void run(String... args) throws Exception {
    //        int res = jdbcTemplate.queryForObject("SELECT COUNT(1) FROM DATA_BEAN", Integer.class);
    //        // log.debug("data_bean count is: {}", res);
    //        UUID uuid = new UUID(16, 16);
    //        Long n1 = System.currentTimeMillis();
    //        // List<PoBean> list = new ArrayList<>();
    //        // for (int i = 0; i < 100; i++) {
    //        //    DataBeanPO po = new DataBeanPO();
    //        //    po.setGuid(uuid.randomUUID().toString());
    //        //    po.setUniqueId(uuid.randomUUID().toString());
    //        //    po.setEventId("ABC");
    //        //     po.setValueDate(Date.valueOf(LocalDate.now()));
    //        // po.setValueDate(LocalDateTime.now());
    //        // po.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
    //        // po.setCreatedBy("UTUT001");
    //        // list.add(po);
    //        // beanServiceImpl.buildInsertSql(po);
    //        // beanServiceImpl.buildMap(po);
    //        // }
    //
    //        // beanServiceImpl.batchInsert(list);
    //
    //        Long n2 = System.currentTimeMillis();
    //        log.info("cost time: {}ms", n2 - n1);
    //
    //        DataBeanPO po = new DataBeanPO();
    //        //po.setGuid();
    //        po.setUniqueId(uuid.randomUUID().toString());
    //        po.setEventId("CDE");
    //        // po.setValueDate(Date.valueOf(LocalDate.now()));
    //        po.setValueDate(LocalDateTime.now());
    //        po.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
    //        po.setCreatedBy("UTUT001");
    //        System.out.println("+++++++++");
    //        System.out.println(res);
    //    }
    //}
}
