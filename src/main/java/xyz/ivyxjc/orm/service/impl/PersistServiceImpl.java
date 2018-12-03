package xyz.ivyxjc.orm.service.impl;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import xyz.ivyxjc.orm.interfaces.PoBean;
import xyz.ivyxjc.orm.service.BeanPersistenceService;

public class PersistServiceImpl implements BeanPersistenceService {

    @Override
    public List<PoBean> query(@NotNull PoBean poBean, @NotNull String... whereColumnNames) {
        return null;
    }

    @Override
    public int insert(@NotNull PoBean poBean) {
        return 0;
    }

    @Override
    public int[] batchInsert(@NotNull List<PoBean> poBean) {
        return new int[0];
    }

    @Override
    public int update(@NotNull PoBean poBean, @NotNull String... whereColumnsNames) {
        return 0;
    }

    @Override
    public int update(@NotNull PoBean poBean, @NotNull Updater updater) {
        return 0;
    }

    @Override
    public int delete(@NotNull PoBean poBean, @NotNull String... whereColumnsNames) {
        return 0;
    }
}
