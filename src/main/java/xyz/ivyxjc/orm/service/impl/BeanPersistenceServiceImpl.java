package xyz.ivyxjc.orm.service.impl;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.Column;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import xyz.ivyxjc.orm.enumerations.JdbcOperationType;
import xyz.ivyxjc.orm.enumerations.SupportedTypes;
import xyz.ivyxjc.orm.enumerations.UpdateType;
import xyz.ivyxjc.orm.interfaces.PoBean;
import xyz.ivyxjc.orm.service.BeanPersistenceService;
import xyz.ivyxjc.orm.service.SqlGenerator;

/**
 * @author Ivyxjc
 * @since 11/17/2018
 */
@Slf4j
@Repository
public class BeanPersistenceServiceImpl implements BeanPersistenceService {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private SqlGenerator sqlGenerator;

    @Override
    public List<PoBean> query(@NotNull PoBean poBean, @NotNull String... whereColumnsNames)
        throws IllegalAccessException {
        Class<? extends PoBean> clz = poBean.getClass();
        String sql = sqlGenerator.getFinalSql(poBean, JdbcOperationType.SELECT);
        String whereClaues = SqlDBUtils.buildWhereClause(poBean, whereColumnsNames);
        sql = sql.concat(whereClaues);
        MapSqlParameterSource sqlParameterSource = SqlDBUtils.buildParameterSource(poBean);
        log.debug("query sql is:{}", sql);
        return jdbcTemplate.query(sql, sqlParameterSource, new CustomerMapper(clz));
    }

    @Override
    public int insert(@NotNull PoBean poBean) throws IllegalAccessException {
        String sql = sqlGenerator.getFinalSql(poBean, JdbcOperationType.INSERT);
        MapSqlParameterSource sqlParameterSource = SqlDBUtils.buildParameterSource(poBean);
        log.debug("insert sql is: {}", sql);
        log.debug("insert poBean is: {}", poBean);
        return jdbcTemplate.update(sql, sqlParameterSource);
    }

    @Override
    public int[] batchInsert(@NotNull List<PoBean> list) throws IllegalAccessException {
        if (list.isEmpty()) {
            return new int[0];
        }
        PoBean firstOne = list.get(0);
        String sql = sqlGenerator.getFinalSql(firstOne, JdbcOperationType.INSERT);
        log.debug("batch insert sql is: {}", sql);
        SqlParameterSource[] parameterSourceArray = new SqlParameterSource[list.size()];
        for (int i = 0; i < list.size(); i++) {
            parameterSourceArray[i] = SqlDBUtils.buildParameterSource(list.get(i));
        }
        return jdbcTemplate.batchUpdate(sql, parameterSourceArray);
    }

    @Override
    public int update(@NotNull PoBean poBean, @NotNull String... whereColumnsNames)
        throws IllegalAccessException {
        String sql = sqlGenerator.getFinalSql(poBean, JdbcOperationType.UPDATE);
        String whereClause = SqlDBUtils.buildWhereClause(poBean, whereColumnsNames);
        MapSqlParameterSource sqlParameterSource = SqlDBUtils.buildParameterSource(poBean);
        log.debug("update sql is:{}", sql);
        return jdbcTemplate.update(sql.concat(whereClause), sqlParameterSource);
    }

    /**
     * 目前不支持 自定义wheresql
     */
    @Override
    public int update(@NotNull PoBean poBean, @NotNull Updater updater)
        throws IllegalAccessException {

        MapSqlParameterSource sqlParameterSource;
        String sql;
        switch (updater.getUpdateType()) {
            case ALL:
                return update(poBean, (String[]) updater.getWhereColumnNames().toArray());
            case CUSTOM:
                // TODO: 11/21/2018 optimize the buildParameterSource, just include needed columns
                sqlParameterSource = SqlDBUtils.buildParameterSource(poBean);
                sql = updater.getUpdateSql();
                return jdbcTemplate.update(sql, sqlParameterSource);
            case NOTNULL:
                // TODO: 11/21/2018 optimize the buildParameterSource, just include needed columns
                sqlParameterSource = SqlDBUtils.buildParameterSource(poBean);
                List<String> whereColumnNames = updater.getWhereColumnNames();
                List<String> whereSqls = updater.getWhereCustomSql();
                List<String> updateColumns =
                    Arrays.stream(poBean.getClass().getDeclaredFields())
                        .filter(t -> t.getAnnotation(Column.class) != null)
                        .filter(
                            t -> {
                                t.setAccessible(true);
                                try {
                                    return t.get(poBean) != null;
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                    throw new RuntimeException();
                                }
                            })
                        .map(
                            f -> {
                                Column col = f.getAnnotation(Column.class);
                                return col.name();
                            })
                        .collect(Collectors.toList());
                Updater newUpdater =
                    Updater.builder()
                        .withUpdateColumns(
                            updateColumns.toArray(new String[updateColumns.size()]))
                        .withWhereColumnNames(
                            whereColumnNames.toArray(new String[updateColumns.size()]))
                        .withCustomSqls(whereSqls.toArray(new String[whereSqls.size()]))
                        .withUpdateType(UpdateType.CUSTOM)
                        .build();

                sql = newUpdater.getUpdateSql();
                return jdbcTemplate.update(sql, sqlParameterSource);
            default:
                throw new IllegalArgumentException(
                    "Unsupported update type ".concat(updater.getUpdateType().name()));
        }
    }

    @Override
    public int delete(@NotNull PoBean poBean, @NotNull String... whereColumnsNames)
        throws IllegalAccessException {
        // TODO: 11/21/2018 optimize the buildParameterSource, just include needed columns
        MapSqlParameterSource sqlParameterSource = SqlDBUtils.buildParameterSource(poBean);
        String whereClauses = SqlDBUtils.buildWhereClause(poBean, whereColumnsNames);
        String sql = sqlGenerator.getFinalSql(poBean, JdbcOperationType.DELETE);
        sql = sql.concat(whereClauses);
        log.debug("delete sql is:{}", sql);
        return jdbcTemplate.update(sql, sqlParameterSource);
    }

    class CustomerMapper<T extends PoBean> implements RowMapper<T> {
        private Class clz;

        CustomerMapper(Class<? extends PoBean> clz) {
            this.clz = clz;
        }

        @Override
        public T mapRow(ResultSet rs, int rowNum) throws SQLException {
            try {
                Object object = clz.newInstance();
                Field[] fields = clz.getDeclaredFields();

                Arrays.stream(fields)
                    .filter(t -> t.getAnnotation(Column.class) != null)
                    .forEach(
                        f -> {
                            f.setAccessible(true);
                            try {
                                Column column = f.getAnnotation(Column.class);
                                String className = f.getType().getName();
                                switch (className) {
                                    case SupportedTypes.javaLangString:
                                        f.set(object, rs.getString(column.name()));
                                        break;
                                    case SupportedTypes.javaLangInteger:
                                        f.set(object, rs.getInt(column.name()));
                                        break;
                                    case SupportedTypes.javaLangLong:
                                        f.set(object, rs.getLong(column.name()));
                                        break;
                                    case SupportedTypes.javaLangFloat:
                                        f.set(object, rs.getFloat(column.name()));
                                        break;
                                    case SupportedTypes.javaLangDouble:
                                        f.set(object, rs.getDouble(column.name()));
                                        break;
                                    case SupportedTypes.javaSqlDate:
                                        f.set(object, rs.getDate(column.name()));
                                        break;
                                    case SupportedTypes.javaSqlTimestamp:
                                        f.set(object, rs.getTimestamp(column.name()));
                                        break;
                                    case SupportedTypes.javaTimeLocalDate:
                                        f.set(
                                            object,
                                            Optional.ofNullable(
                                                rs.getDate(column.name()))
                                                .map(java.sql.Date::toLocalDate)
                                                .orElse(null));
                                        break;
                                    case SupportedTypes.javaTimeLocalDateTime:
                                        f.set(
                                            object,
                                            Optional.ofNullable(
                                                rs.getTimestamp(
                                                    column.name()))
                                                .map(Timestamp::toLocalDateTime)
                                                .orElse(null));
                                        break;
                                    case SupportedTypes.javaUtilDate:
                                        f.set(object, rs.getDate(column.name()));
                                        break;
                                    default:
                                        f.set(object, rs.getString(column.name()));
                                        break;
                                }
                            } catch (IllegalAccessException | SQLException e) {
                                e.printStackTrace();
                                throw new RuntimeException(e);
                            }
                        });

                return (T) object;
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}
