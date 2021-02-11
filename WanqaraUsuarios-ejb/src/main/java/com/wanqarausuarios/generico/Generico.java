/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wanqarausuarios.generico;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import lombok.Getter;

/**
 *
 * @author Marco
 */
public class Generico<T> {

    @PersistenceContext(unitName = "WANQARA_PU")

    @Getter
    private EntityManager entityManager;

    public void guardar(final T t) {
        try {
            if (!constraintValidationsDetected(t)) {
                getEntityManager().merge(t);
            }
        } catch (RuntimeException e) {
            System.err.println(e);
        }
    }

    public void guardarSalida(final T t) {

        try {
            if (!constraintValidationsDetected(t)) {
                getEntityManager().persist(t);
            }

        } catch (RuntimeException e) {
            System.err.println(e);
        }
    }

    public void guardarLote(final List<T> lista) {
        try {
            for (T t : lista) {
                getEntityManager().merge(t);
            }
        } catch (RuntimeException e) {
            System.err.println(e);
        }
    }

    public List<T> listarPorConsultaJpaNombrada(final String jpaQl,
            final Map<String, Object> parametros) {
        try {
            Query query = getEntityManager().createNamedQuery(jpaQl);
            if (parametros != null) {
                for (Map.Entry en : parametros.entrySet()) {
                    query.setParameter(en.getKey().toString(), en.getValue());

                }
            }
            return query.getResultList();
        } catch (RuntimeException e) {
            System.err.println(e);
            return new ArrayList<>();
        }

    }

    public T obtenerPorConsultaJpaNombrada(final String jpaQl,
            final Map<String, Object> parametros) {
        try {
            Query query = getEntityManager().createNamedQuery(jpaQl);
            if (parametros != null) {
                for (Map.Entry en : parametros.entrySet()) {
                    query.setParameter(en.getKey().toString(), en.getValue());
                }
            }
            return (T) query.getSingleResult();
        } catch (RuntimeException e) {
            System.err.println(e);
            return null;
        }

    }

    public List<T> listarPorConsultaJpa(final String jpaQl,
            final Map<String, Object> parametros) {
        try {
            Query query = getEntityManager().createQuery(jpaQl);
            if (parametros != null) {
                for (Map.Entry en : parametros.entrySet()) {
                    query.setParameter(en.getKey().toString(), en.getValue());
                }
            }
            return query.getResultList();
        } catch (RuntimeException e) {
            System.err.println(e);
            return new ArrayList<>();
        }

    }

    public T obtenerPorConsultaJpa(final String jpaQl,
            final Map<String, Object> parametros) {
        try {
            Query query = getEntityManager().createQuery(jpaQl);
            if (parametros != null) {
                for (Map.Entry en : parametros.entrySet()) {
                    query.setParameter(en.getKey().toString(), en.getValue());
                }
            }
            return (T) query.getSingleResult();
        } catch (RuntimeException e) {
            System.err.println(e);
            return null;
        }

    }

    public List<T> listarPorConsultaNativa(final String sql,
            final Map<String, Object> parametros) {
        try {
            Query query = getEntityManager().createNativeQuery(sql);
            if (parametros != null) {
                for (Map.Entry en : parametros.entrySet()) {
                    query.setParameter(en.getKey().toString(), en.getValue());
                }
            }
            return query.getResultList();
        } catch (RuntimeException e) {
            System.err.println(e);
            return new ArrayList<>();
        }

    }

    public T obtenerPorConsultaNativa(final String sql,
            final Map<String, Object> parametros) {
        try {
            Query query = getEntityManager().createNativeQuery(sql);
            if (parametros != null) {
                for (Map.Entry en : parametros.entrySet()) {
                    query.setParameter(en.getKey().toString(), en.getValue());
                }
            }
            return (T) query.getSingleResult();
        } catch (RuntimeException e) {
            System.err.println(e);
            return null;
        }

    }

    public List listarPorConsultaNativa(final String sql,
            final Map<String, Object> parametros,
            final Class clase) {
        try {
            Query query = getEntityManager().createNativeQuery(sql, clase);
            if (parametros != null) {
                for (Map.Entry en : parametros.entrySet()) {
                    query.setParameter(en.getKey().toString(), en.getValue());
                }
            }
            return query.getResultList();
        } catch (RuntimeException e) {
            System.err.println(e);
            return new ArrayList<>();
        }

    }

    public Object obtenerPorConsultaNativa(final String sql,
            final Map<String, Object> parametros,
            final Class clase) {
        try {
            Query query = getEntityManager().createNativeQuery(sql, clase);
            if (parametros != null) {
                for (Map.Entry en : parametros.entrySet()) {
                    query.setParameter(en.getKey().toString(), en.getValue());
                }
            }
            return query.getSingleResult();
        } catch (RuntimeException e) {
            System.err.println(e);
            return null;
        }

    }

    //Metodos agregados de clase abstractFacade
    public void create(T entity) {
        try {
            auxConstraintValidationsDetected(entity);
            getEntityManager().persist(entity);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void edit(T entity) {
        if (!constraintValidationsDetected(entity)) {
            getEntityManager().merge(entity);

        }
    }

    private boolean constraintValidationsDetected(T entity) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(entity);
        if (constraintViolations.size() > 0) {
            Iterator<ConstraintViolation<T>> iterator = constraintViolations.iterator();
            while (iterator.hasNext()) {
                ConstraintViolation<T> cv = iterator.next();
                System.err.println(cv.getMessage());
            }
            return true;
        } else {
            return false;
        }
    }

    private void auxConstraintValidationsDetected(T entity) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(entity);
        if (constraintViolations.size() > 0) {
            Iterator<ConstraintViolation<T>> iterator = constraintViolations.iterator();
            while (iterator.hasNext()) {
                ConstraintViolation<T> cv = iterator.next();
                System.err.println(cv.getMessage());
            }

        }
    }

    public void remove(T entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    public T find(T entity, Object id) {
        return (T) getEntityManager().find(entity.getClass(), id);
    }

    public List<T> findRange(T entity, int[] range) {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entity.getClass()));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0] + 1);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    public T guardarRetorno(T t) {
        try {
            T tipo = getEntityManager().merge(t);
            getEntityManager().flush();
            return tipo;
        } catch (RuntimeException e) {
            System.err.println(e);
            return null;
        }
    }

}
