package com.fajar.entitymanagement.repository;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.entitymanagement.annotation.CustomEntity;
import com.fajar.entitymanagement.dto.Filter;
import com.fajar.entitymanagement.entity.BaseEntity;
import com.fajar.entitymanagement.querybuilder.CriteriaBuilder;
import com.fajar.entitymanagement.querybuilder.QueryHolder;
import com.fajar.entitymanagement.querybuilder.QueryUtil;
import com.fajar.entitymanagement.util.EntityUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RepositoryCustomImpl implements RepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private Session hibernateSession;

	boolean removeTransactionAfterPersistence = true;

	public boolean isTransactionNotKept() {
		return removeTransactionAfterPersistence;
	}

	public void keepTransaction() {
		this.removeTransactionAfterPersistence = false;
	}

	public void notKeepingTransaction() {
		this.removeTransactionAfterPersistence = true;
	}

	private Transaction currentTransaction;

	public RepositoryCustomImpl() {
	}

	@Override
	public <T> List<T> filterAndSort(String sql, Class<? extends T> clazz) {

		log.info("==============GET LIST FROM NATIVE SQL: " + sql);
		List<T> resultList = entityManager.createNativeQuery(sql, clazz).getResultList();
		if (resultList == null) {
			resultList = new ArrayList<>();
		}
		log.info("==============SQL OK: {}", resultList.size());
		return resultList;

	}

	@Override
	public Object getSingleResult(String sql) {
		log.info("=============GETTING SINGLE RESULT SQL: {}", sql);
		Object result = entityManager.createNativeQuery(sql).getSingleResult();
		log.info("=============RESULT SQL: {}, type: {}", result,
				result != null ? result.getClass().getCanonicalName() : null);
		return result;
	}

	@Override
	public Query createNativeQuery(String sql) {
		Query q = entityManager.createNativeQuery(sql);
		return q;
	}

	@Override
	public <O> O getCustomedObjectFromNativeQuery(String sql, Class<O> objectClass) {
		log.info("SQL for result object: {}", sql);
		try {
			Query result = entityManager.createNativeQuery(sql);
			Object resultObject = result.getSingleResult();
			log.info("object ,{}", resultObject);
			/**
			 * check if object has custom entity annotation
			 */
			if (null == resultObject || objectClass.getAnnotation(CustomEntity.class) == null) {
				return null;
			}

			/**
			 * mapping result list to object fields based on information from the
			 * CustomEntity annotation
			 */
			CustomEntity customEntitySetting = (CustomEntity) objectClass.getAnnotation(CustomEntity.class);
			Object[] propertiesArray = (Object[]) resultObject;
			String[] propertyOrder = customEntitySetting.propOrder();

			O singleObject = fillObject(objectClass, propertiesArray, propertyOrder);
			log.info("RESULT OBJECT: {}", singleObject);
			return singleObject;
		} catch (Exception e) {
			log.error("ERROR GET RECORD: " + e.getMessage());
			return null;
		}

	}

	public static <O> O fillObject(Class<O> objectClass, Object[] propertiesArray, String[] propertyOrder)
			throws Exception {
		O object = objectClass.getDeclaredConstructor().newInstance();

		for (int j = 0; j < propertiesArray.length; j++) {
			String propertyName = propertyOrder[j];
			Object propertyValue = propertiesArray[j];
			Field field = EntityUtil.getDeclaredField(object.getClass(), propertyName);
//					object.getClass().getDeclaredField(propertyName);
//			field.setAccessible(true);

			if (field != null && propertyValue != null) {
				final Class<?> fieldType = field.getType();
				log.info("Type: {} : {}", fieldType, propertyValue);

				if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
					propertyValue = Integer.parseInt(propertyValue.toString());
				} else if (field.getType().equals(Long.class) || fieldType.equals(long.class)) {
					propertyValue = Long.parseLong(propertyValue.toString());
				} else if (field.getType().equals(Double.class) || fieldType.equals(double.class)) {
					propertyValue = Double.parseDouble(propertyValue.toString());
				}

				field.set(object, propertyValue);
			}

		}
		return object;
	}

	@Override
	public <T> List<T> filterAndSort(QueryHolder queryHolder, Class<? extends T> objectClass) {

		return filterAndSort(queryHolder.getSqlSelect(), objectClass);
	}

	@Override
	public Object getSingleResult(QueryHolder queryHolder) {

		return getSingleResult(queryHolder.getSqlSingleResult());
	}

	@Override
	public <T extends BaseEntity> List<T> filterAndSortv2(Class<T> _class, Filter filter) {
		try {
			CriteriaBuilder criteriaBuilder = new CriteriaBuilder(hibernateSession, _class);
			Criteria criteria = criteriaBuilder.createCriteria(_class, filter, false);
			List<T> resultList = criteria.list();

			if (null == resultList) {
				resultList = new ArrayList<>();
			}

			log.info("resultList length: {}", resultList.size());
			return resultList;
		} catch (Exception e) {
			log.error("Error filter and sort v2: {}", e);
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	@Override
	public long getRowCount(Class<? extends BaseEntity> _class, Filter filter) {

		try {
			CriteriaBuilder criteriaBuilder = new CriteriaBuilder(hibernateSession, _class);
			Criteria criteria = criteriaBuilder.createRowCountCriteria(_class, filter);
			return (long) criteria.uniqueResult();
		} catch (Exception e) {
			return 0;
		}
	}

	public <T extends BaseEntity> T validateJoinColumns(T rawEntity) throws Exception {
		List<Field> joinColumnFields = QueryUtil.getJoinColumnFields(rawEntity.getClass());

		T entity = EntityUtil.cloneSerializable(rawEntity);

		if (0 == joinColumnFields.size()) {
			return entity;
		}
		for (Field field : joinColumnFields) {
			BaseEntity fieldValue = (BaseEntity) field.get(entity);
			// check from DB
			log.info("check join column field: {}->value: {}", field.getName(), fieldValue);
			if (null == fieldValue)
				continue;
			Object dbValue = hibernateSession.get(fieldValue.getClass(), fieldValue.getId());

			if (null == dbValue)
				continue;

			field.set(entity, dbValue);
		}

		return entity;

	}

	@Override
	@Transactional
	public <T extends BaseEntity> T saveObject(final T rawEntity) {

		if (null == rawEntity) {
			log.error("rawEntity IS NULL");
			return null;
		}
 
		
		PersistenceOperation<T> persistenceOperation = new PersistenceOperation<T>() {

			@Override
			public T doPersist(Session hibernateSession) {
				T result, entity;
				try {
					entity = validateJoinColumns(rawEntity);
				} catch (Exception e) {
					entity = rawEntity;
					e.printStackTrace();
				}

				if (entity.getId() == null) {
					log.debug("Will save new entity ");
					rawEntity.setCreatedDate(new Date());
					
					Long newId = (Long) hibernateSession.save(entity);
					result = entity;
					result.setId(newId);

					log.debug("success add new record of {} with new ID: {}", entity.getClass(), newId);
				} else {
					log.debug("Will update entity ");
					entity.setModifiedDate(new Date());
					
					result = (T) hibernateSession.merge(entity);

					log.debug("success update record of {}", entity.getClass());
				}

				log.info("success save Object of {} >> savedObject: {}", entity.getClass(), result);
				return result;
			}
		};

		T result = this.pesistOperation(persistenceOperation);
		if (null != result) {
			log.info("success save Object: {}", result.getClass());
		}

		return result;
	}

	/**
	 * save and add new inserted Id
	 * 
	 * @param <T>
	 * @param entity
	 * @param hibernateSession
	 */
	public static <T extends BaseEntity> void saveNewRecord(T entity, Session hibernateSession) {

		Long id = (Long) hibernateSession.save(entity);
		entity.setId(id);
	}

	@Override
	public <T> T pesistOperation(PersistenceOperation<T> persistenceOperation) {

		try {
			if (null == persistenceOperation) {
				throw new Exception("persistenceOperation must not be NULL");
			}
			if (null == currentTransaction) {
				currentTransaction = hibernateSession.beginTransaction();
			}

			T result = persistenceOperation.doPersist(hibernateSession);

			if (isTransactionNotKept()) {
				currentTransaction.commit();
				log.info("==**COMMITED Transaction**==");
			}

			log.info("success persist operation commited: {}", removeTransactionAfterPersistence);
			return result;

		} catch (Exception e) {
			log.error("Error persist operation: {}", e);

			if (currentTransaction != null) {
				log.info("Rolling back.... ");
				currentTransaction.rollback();
			}
			notKeepingTransaction();

			e.printStackTrace();
		} finally {

			if (isTransactionNotKept()) {
				currentTransaction = null;
			}
		}
		return null;
	}

	public boolean deleteObjectById(Class<? extends BaseEntity> _class, Long id) {
		PersistenceOperation<Boolean> deleteOperation = new PersistenceOperation<Boolean>() {

			@Override
			public Boolean doPersist(Session hibernateSession) {
				try {
					Object existingObject = hibernateSession.load(_class, id);
					if (null == existingObject) {
						log.info("existingObject of {} with id: {} does not exist!!", _class, id);
						return false;
					}
					hibernateSession.delete(existingObject);
					log.debug("Deleted Successfully");
					return true;
				} catch (Exception e) {
					log.error("Error deleting object!");
					e.printStackTrace();
					return false;
				}
			}
		};
		try {
			return this.pesistOperation(deleteOperation);
		} catch (Exception e) {
			return false;
		}
	}

}
