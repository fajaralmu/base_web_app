package com.fajar.entitymanagement.service;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.entitymanagement.dto.Filter;
import com.fajar.entitymanagement.dto.WebRequest;
import com.fajar.entitymanagement.dto.WebResponse;
import com.fajar.entitymanagement.entity.BaseEntity;
import com.fajar.entitymanagement.entity.UserRole;
import com.fajar.entitymanagement.entity.setting.EntityManagementConfig;
import com.fajar.entitymanagement.repository.EntityRepository;
import com.fajar.entitymanagement.repository.RepositoryCustomImpl;
import com.fajar.entitymanagement.service.entity.BaseEntityUpdateService;
import com.fajar.entitymanagement.util.CollectionUtil;
import com.fajar.entitymanagement.util.EntityUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EntityService {

	public static final String ORIGINAL_PREFFIX = "{ORIGINAL>>";

	@Autowired
	private RepositoryCustomImpl repositoryCustom;
	@Autowired
	private EntityRepository entityRepository;

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	private EntityManagementConfig getEntityManagementConfig(String key) {
		return entityRepository.getConfiguration(key);
	}

	/**
	 * add & update entity
	 * 
	 * @param request
	 * @param servletRequest
	 * @param newRecord
	 * @return
	 */
	public WebResponse saveEntity(WebRequest request, HttpServletRequest servletRequest, boolean newRecord) {

		try {

			final String key = request.getEntity().toLowerCase();
			EntityManagementConfig entityConfig = getEntityManagementConfig(key);
			BaseEntityUpdateService updateService = entityConfig.getEntityUpdateService();
			String fieldName = entityConfig.getFieldName();
			Object entityValue = null;

			try {
				Field entityField = EntityUtil.getDeclaredField(WebRequest.class, fieldName);
				entityValue = entityField.get(request);

				log.info("save {}: {}", entityField.getName(), entityValue);
				log.info("newRecord: {}", newRecord);

			} catch (Exception e) {
				e.printStackTrace();
				return WebResponse.failed();
			}

			if (entityValue != null)
				return updateService.saveEntity((BaseEntity) entityValue, newRecord);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return WebResponse.builder().code("01").message("failed").build();
	}

	/**
	 * get list of entities filtered
	 * 
	 * @param request
	 * @return
	 */
	public WebResponse filter(WebRequest request) {
		Class<? extends BaseEntity> entityClass = null;

		Filter filter = EntityUtil.cloneSerializable(request.getFilter());

		if (filter == null) {
			filter = new Filter();
		}
		if (filter.getFieldsFilter() == null) {
			filter.setFieldsFilter(new HashMap<String, Object>());
		}

		try {

			String entityName = request.getEntity().toLowerCase();
			EntityManagementConfig config = getEntityManagementConfig(entityName);
			log.info("entityName: {}, config: {}", entityName, config);
			entityClass = config.getEntityClass();

			if (null == entityClass) {
				throw new Exception("Invalid entity");
			}

			/**
			 * Generate query string
			 */
			EntityResult entityResult = filterEntities(filter, entityClass);

			return WebResponse.builder().entities(EntityUtil.validateDefaultValue(entityResult.entities))
					.totalData(entityResult.count).filter(request.getFilter()).entityClass(entityClass).build();

		} catch (Exception ex) {
			ex.printStackTrace();
			return WebResponse.failed();
		}
	}

	private <T extends BaseEntity> EntityResult filterEntities(Filter filter, Class<T> entityClass) {
		final List<T> entities = new ArrayList<>();
		final Map<String, Long> count = new HashMap<>();
		try {
//			Thread thread = ThreadUtil.run(() -> {
				List<T> resultList = repositoryCustom.filterAndSortv2(entityClass, filter);
				entities.addAll(resultList);
//			});
//			Thread thread2 = ThreadUtil.run(() -> {
				long resultCount = repositoryCustom.getRowCount(entityClass, filter);
				count.put("value", resultCount);
//			});
//			thread.join();
//			thread2.join();
			
		}catch (Exception e) {
			log.error("Error filterEntities: {}", e.getCause() );
			count.put("value", 0L);
			e.printStackTrace();
		}
		return EntityResult.builder().entities(CollectionUtil.convertList(entities)).count(count.get("value").intValue())
				.build();
	}

	/**
	 * delete entity
	 * 
	 * @param request
	 * @return
	 */
	public WebResponse delete(WebRequest request) {

		try {
			Map<String, Object> filter = request.getFilter().getFieldsFilter();
			Long id = Long.parseLong(filter.get("id").toString());
			String entityName = request.getEntity().toLowerCase();

			Class<? extends BaseEntity> entityClass = getEntityManagementConfig(entityName).getEntityClass();

			if (null == entityClass) {
				throw new Exception("Invalid entity");
			}

			boolean result = entityRepository.deleteById(id, entityClass);

			return WebResponse.builder().code("00").message("deleted :"+result).build();

		} catch (Exception ex) {

			ex.printStackTrace();
			return WebResponse.builder().code("01").message("failed: " + ex.getMessage()).build();
		}
	}

	public List<UserRole> getAllUserRole() {
		return entityRepository.findAll(UserRole.class);
	}

	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	static class EntityResult implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7627112916142073122L;
		List<BaseEntity> entities;
		int count;
	}

//	
//	public List<Cost> getAllCostType() {
//		return findAll(Cost.class);
//	}
// 
//
//	public List<Capital> getAllCapitalType() {
//		return findAll(Capital.class);
//	}

	public <T extends BaseEntity> List<T> findAll(Class<T> _class) {
		List<T> resultList = entityRepository.findAll(_class);

		if (null == resultList) {
			resultList = new ArrayList<T>();
		}

		return resultList;
	}

}
