package com.learnrest.dao;

import com.learnrest.dao.connection.DatabaseConnection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.slf4j.Logger;

/**
 *
 * @author fernando
 * @param <T> EntityClass to manage
 * @param <K> Datatype from ID
 */
public abstract class GenericDAO<T, K> implements DAO<T, K> {

    private final Class<T> entityClass;

    public GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public void save(T entity) {
        try {
            getEntityManager().getTransaction().begin();
            getEntityManager().persist(entity);
            getEntityManager().getTransaction().commit();
        } catch (Exception ex) {
            getEntityManager().getTransaction().rollback();
            getLogger().error("Error to persist: " + ex.getMessage());
            throw new DAOException(ex);
        } finally {
            getEntityManager().close();
        }
    }

    @Override
    public void update(T entity) {
        try {
            getEntityManager().getTransaction().begin();
            getEntityManager().merge(entity);
            getEntityManager().getTransaction().commit();
        } catch (Exception ex) {
            getEntityManager().getTransaction().rollback();
            getLogger().error("Error to merge: " + ex.getMessage());
            throw new DAOException(ex);
        } finally {
            getEntityManager().close();
        }
    }

    @Override
    public void delete(T entity) {
        try {
            getEntityManager().getTransaction().begin();
            entity = getEntityManager().merge(entity);
            getEntityManager().remove(entity);
            getEntityManager().getTransaction().commit();
        } catch (Exception ex) {
            getEntityManager().getTransaction().rollback();
            getLogger().error("Error to delete: " + ex.getMessage());
            throw new DAOException(ex);
        } finally {
            getEntityManager().close();
        }
    }

    @Override
    public T merge(T entity) {
        try {
            getEntityManager().getTransaction().begin();
            entity = getEntityManager().merge(entity);
            getEntityManager().getTransaction().commit();
            return entity;
        } catch (Exception ex) {
            getEntityManager().getTransaction().rollback();
            getLogger().error("Error to merge: " + ex.getMessage());
            throw new DAOException(ex);
        } finally {
            getEntityManager().close();
        }
    }

    @Override
    public T findById(K id) {
        return getEntityManager().find(entityClass, id);
    }

    @Override
    public List<T> findAll() {
        StringBuilder sb = new StringBuilder();
        sb.append("select obj from ");
        sb.append(entityClass.getSimpleName());
        sb.append(" obj order by obj.id");
        Query query = getEntityManager().createQuery(sb.toString());
        return query.getResultList();
    }

    @Override
    public void deleteAll() {
        List<T> objects = findAll();
        for (T obj : objects) {
            delete(obj);
        }
    }

    @Override
    public EntityManager getEntityManager() {
        return DatabaseConnection.newInstance().getEntityManager();
    }

    @Override
    public Class<T> getEntityClass() {
        return entityClass;
    }

    @Override
    public abstract Logger getLogger();

}
