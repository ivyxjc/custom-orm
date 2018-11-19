package xyz.ivyxjc.orm.service;

import java.util.List;
import xyz.ivyxjc.orm.enumerations.UpdateType;
import xyz.ivyxjc.orm.interfaces.PoBean;

/**
 * @author Ivyxjc
 * @since 11/17/2018
 */
public interface BeanService {
    void insert(PoBean poBean);

    void update(PoBean poBean, String... whereColumnsNames);

    void update(PoBean poBean, Updater updater, UpdateType type);

    void batchInsert(List<PoBean> poBean);
}
