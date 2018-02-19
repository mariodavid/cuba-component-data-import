package de.diedavids.cuba.dataimport.core;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.entity.BaseGenericIdEntity;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.global.PersistenceHelper;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aleksey on 18/10/2016.
 */
public class DbHelper {
    /**
     * Constructs a query and loads entities, matching the specified parameter conditions.
     *
     * @param params key is query parameter alias, and value - needed parameter value.
     *               Aliases should be specified in jpql-like syntax (e.g. "object.property1").
     *               {@link StandardEntity} is also supported as a param value.
     */
    public static <T extends BaseGenericIdEntity> List<T> existedEntities(EntityManager em, Class<?> entityClass, Map<String, ? extends Object> params) {
        return (List<T>) createQuery(em, entityClass, params).getResultList();
    }

    /**
     * Constructs a query and loads entities, matching single specified parameter condition
     *
     * @return the first entity from the loaded list. Returns {@code null} if no existing entity matches the condition
     * @see {@link #existedEntities(EntityManager, Class, Map)}
     */
    public static <T extends BaseGenericIdEntity> T existedEntity(EntityManager em, Class<T> clazz, String paramName, Object value) {
        HashMap<String, Object> params = new HashMap<>();
        params.put(paramName, value);
        return existedEntity(em, clazz, params);
    }

    /**
     * Constructs a query and loads entities, matching the specified parameter conditions.
     *
     * @return the first entity from the loaded list. Returns {@code null} if no existing entity matches the condition
     * @see {@link #existedEntities(EntityManager, Class, Map)}
     */
    @Nullable
    public static <T extends BaseGenericIdEntity> T existedEntity(EntityManager em, Class<?> entityClass, Map<String, ? extends Object> params) {
        List results = existedEntities(em, entityClass, params);
        if (results.size() > 0) {
            return (T) results.get(0);
        }

        return null;
    }

    /**
     * Constructs a query and loads entities, matching the specified conditions.
     *
     * @see {@link #existedEntities(EntityManager, Class, Map)}
     * @see {@link Query#getSingleResult()}
     */
    @Nullable
    public static <T extends StandardEntity> T existedSingleEntity(EntityManager em, Class<?> entityClass, Map<String, Object> params) {
        Query query = createQuery(em, entityClass, params);
        return (T) query.getSingleResult();
    }

    /**
     * Constructs the query, creating a where-clause from given params map.
     *
     * @param params key is query parameter alias, and value - needed parameter value.
     *               Aliases should be specified in jpql-like syntax (e.g. "object.property1").
     *               {@link StandardEntity} is also supported as a param value.
     * @return the constructed query
     */
    public static Query createQuery(EntityManager em, Class<?> entityClass, Map<String, ? extends Object> params) {
        String selectClause = createSelectClause(entityClass);
        QueryConditionContext ctx = createConditionContext(params);

        String whereClause = ctx.whereClause;
        Map<String, Object> queryParams = ctx.queryParams;
        String qlString = selectClause + whereClause;

        Query query = em.createQuery(qlString);

        for (String parameter : queryParams.keySet()) {
            query.setParameter(parameter, queryParams.get(parameter));
        }

        return query;
    }

    protected static QueryConditionContext createConditionContext(Map<String, ? extends Object> params) {
        Map<String, Object> queryParams = new HashMap<>();
        StringBuilder whereClause = new StringBuilder();
        if (params != null) {
            for (String param : params.keySet()) {
                String condition;
                Object value = params.get(param);
                if (value == null) {
                    condition = "t." + param + " is NULL";
                } else {
                    String formattedParam = formatParameterName(param);
                    if (value instanceof String) {
                        condition = "lower(t." + param + ") = lower(:" + formattedParam + ")";
                    } else if (value instanceof BaseGenericIdEntity) {
                        condition = "t." + param + ".id = :" + formattedParam;
                    } else {
                        condition = "t." + param + " = :" + formattedParam;
                    }
                    queryParams.put(formattedParam, value);
                }

                if (whereClause.length() == 0) {
                    whereClause.append(" where ").append(condition);
                } else {
                    whereClause.append(" and ").append(condition);
                }
            }
        }

        return new QueryConditionContext(whereClause.toString(), queryParams);
    }

    protected static String createSelectClause(Class<?> entityClass) {
        return "select t from " + PersistenceHelper.getEntityName(entityClass) + " t";
    }

    protected static String formatParameterName(String parameter) {
        return parameter.replace(".", "_");
    }

    protected static class QueryConditionContext {
        String whereClause;
        Map<String, Object> queryParams;

        public QueryConditionContext(String whereClause, Map<String, Object> queryParams) {
            this.whereClause = whereClause;
            this.queryParams = queryParams;
        }
    }

}
