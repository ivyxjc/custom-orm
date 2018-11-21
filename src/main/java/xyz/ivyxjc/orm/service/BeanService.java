package xyz.ivyxjc.orm.service;

import java.util.List;
import xyz.ivyxjc.orm.interfaces.PoBean;
import xyz.ivyxjc.orm.service.impl.Updater;

/**
 * @author Ivyxjc
 * @since 11/17/2018
 */
public interface BeanService {

    List<PoBean> query(PoBean poBean, String... whereColumnNames);

    int insert(PoBean poBean);

    int[] batchInsert(List<PoBean> poBean);

    int update(PoBean poBean, String... whereColumnsNames);

    int update(PoBean poBean, Updater updater);

    int delete(PoBean poBean, String... whereColumnsNames);
}
