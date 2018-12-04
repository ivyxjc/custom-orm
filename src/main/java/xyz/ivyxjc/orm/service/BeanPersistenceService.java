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

    List<PoBean> query(@NotNull PoBean poBean, @NotNull String... whereColumnNames)
        throws IllegalAccessException;

    int insert(@NotNull PoBean poBean) throws IllegalAccessException;

    int[] batchInsert(@NotNull List<PoBean> poBean) throws IllegalAccessException;

    int update(@NotNull PoBean poBean, @NotNull String... whereColumnsNames)
        throws IllegalAccessException;

    int update(@NotNull PoBean poBean, @NotNull Updater updater) throws IllegalAccessException;

    int delete(@NotNull PoBean poBean, @NotNull String... whereColumnsNames)
        throws IllegalAccessException;
}
