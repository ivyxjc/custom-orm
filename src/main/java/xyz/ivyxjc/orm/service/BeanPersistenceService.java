package xyz.ivyxjc.orm.service;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import xyz.ivyxjc.orm.interfaces.PoBean;
import xyz.ivyxjc.orm.service.impl.Updater;

/**
 * @author Ivyxjc
 * @since 11/17/2018
 */
public interface BeanPersistenceService {

    List<PoBean> query(@NotNull PoBean poBean, @NotNull String... whereColumnNames);

    int insert(@NotNull PoBean poBean);

    int[] batchInsert(@NotNull List<PoBean> poBean);

    int update(@NotNull PoBean poBean, @NotNull String... whereColumnsNames);

    int update(@NotNull PoBean poBean, @NotNull Updater updater);

    int delete(@NotNull PoBean poBean, @NotNull String... whereColumnsNames);
}
