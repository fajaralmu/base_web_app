package com.fajar.entitymanagement.service.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fajar.entitymanagement.annotation.FormField;
import com.fajar.entitymanagement.annotation.StoreValueTo;
import com.fajar.entitymanagement.entity.BaseEntity;
import com.fajar.entitymanagement.repository.EntityRepository;
import com.fajar.entitymanagement.util.CollectionUtil;
import com.fajar.entitymanagement.util.EntityUtil;

import lombok.extern.slf4j.Slf4j;

//@Service
@Slf4j
public class EntityValidation {
//	@Autowired
//	private EntityRepository entityRepository;

//	public static void validateDefaultValues(List<? extends BaseEntity> entities, EntityRepository entityRepository) {
//		for (int i = 0; i < entities.size(); i++) {
//			validateDefaultValue(entities.get(i), entityRepository);
//		}
//	}

	public static <T extends BaseEntity> List<T> validateDefaultValues(List<T> objects,
			EntityRepository entityRepository) {
		Map<Field, List<Long>> relatedObjectIdContainer = new HashMap<>();
		Map<Field, List<BaseEntity>> relatedObjectContainer = new HashMap<>();

		for (int i = 0; i < objects.size(); i++) {
			final T object = objects.get(i);
			final T validatedObject = validateDefaultValue(object, relatedObjectIdContainer);
			objects.set(i, validatedObject);
		}

		fillRequiredEntityValue(relatedObjectIdContainer, relatedObjectContainer,entityRepository); 
		

		log.info("relatedObjectIdContainer.keySet().size(); {}", relatedObjectIdContainer.keySet().size());
		log.info("relatedObjectContainer.keySet().size(); {}", relatedObjectContainer.keySet().size());

		for (int i = 0; i < objects.size(); i++) {
			final T object = objects.get(i);
			if (null == entityRepository)
				continue;
			try {
				loopp: for (Field collectionTypeField : relatedObjectContainer.keySet()) {
					List<BaseEntity> entityValues = relatedObjectContainer.get(collectionTypeField);

					String storeToFieldName = EntityUtil.getFieldAnnotation(collectionTypeField, StoreValueTo.class)
							.value();
					Field storeToField = EntityUtil.getDeclaredField(object.getClass(), storeToFieldName);
					Object idValues = storeToField.get(object);

					if (null == idValues || null == entityValues)
						continue loopp;
					
					if ("" != idValues.toString()) {
						String[] rawIdentities = idValues.toString().split("~");

						List<Long> idList = CollectionUtil.arrayToList(toLongArray(rawIdentities));
						if (null == idList) {
							continue loopp;
						}
						List<BaseEntity> thisFieldValue = new ArrayList<>();
						entityValues.forEach(v -> {
							for (int j = 0; j < idList.size(); j++) {
								if (v.getId().equals(idList.get(j))) {
									thisFieldValue.add(v);
								}
							} 
						});
						collectionTypeField.set(object, thisFieldValue);
					}

				}
				objects.set(i, object);
			} catch (Exception e) {
				 
				e.printStackTrace();
			}

		}

		return objects;
	}

	private static void fillRequiredEntityValue(Map<Field, List<Long>> relatedObjectIdContainer,
			Map<Field, List<BaseEntity>> relatedObjectContainer, EntityRepository entityRepository) {
		
		for (Field collectionTypeField : relatedObjectIdContainer.keySet()) {
			
			Type underlyingType = CollectionUtil.getGenericTypes(collectionTypeField)[0];
			List<Long> idList = relatedObjectIdContainer.get(collectionTypeField);
			Field idField = EntityUtil.getIdFieldOfAnObject((Class) underlyingType);
			List<BaseEntity> listOfEntities = entityRepository.findByKey((Class<? extends BaseEntity>) underlyingType,
					idField, idList.toArray());
			
			log.info("listOfEntities: {}", listOfEntities.size());
			relatedObjectContainer.put(collectionTypeField, listOfEntities);
		}
		
	}

	public static <T extends BaseEntity> T validateDefaultValue(T baseEntity,
			Map<Field, List<Long>> relatedObjectIdContainer) {
		List<Field> fields = EntityUtil.getDeclaredFields(baseEntity.getClass());

		for (Field field : fields) {

			try {

				field.setAccessible(true);
				FormField formField = field.getAnnotation(FormField.class);
				Object objectValue = field.get(baseEntity);

				if (field.getType().equals(String.class) && formField != null
						&& formField.defaultValue().equals("") == false) {

					if (objectValue == null || objectValue.toString().equals("")) {
						field.set(baseEntity, formField.defaultValue());
					}

				} else if (formField != null && formField.multiply().length > 1) {

					if (objectValue != null)
						continue;

					Object newValue = "1";
					String[] multiplyFields = formField.multiply();

					loop: for (String multiplyFieldName : multiplyFields) {

						Field multiplyField = EntityUtil.getDeclaredField(baseEntity.getClass(), multiplyFieldName);

						if (multiplyField == null) {
							continue loop;
						}
						multiplyField.setAccessible(true);

						Object multiplyFieldValue = multiplyField.get(baseEntity);
						String strVal = "0";

						if (multiplyFieldValue != null) {
							strVal = multiplyFieldValue.toString();
						}

						if (field.getType().equals(Long.class)) {
							newValue = Long.parseLong(newValue.toString()) * Long.parseLong(strVal);

						} else if (field.getType().equals(Integer.class)) {
							newValue = Integer.parseInt(newValue.toString()) * Integer.parseInt(strVal);

						} else if (field.getType().equals(Double.class)) {
							newValue = Double.parseDouble(newValue.toString()) * Double.parseDouble(strVal);
						}

					}
					field.set(baseEntity, newValue);

				} else if (formField != null && formField.multipleSelect()) {
					String storeToFieldName = EntityUtil.getFieldAnnotation(field, StoreValueTo.class).value();
					Field storeToField = EntityUtil.getDeclaredField(baseEntity.getClass(), storeToFieldName);

					Object idValues = storeToField.get(baseEntity);

					if (null == idValues)
						continue;
					if ("" != idValues.toString()) {
						String[] rawIdentities = idValues.toString().split("~");

						List<Long> idList = CollectionUtil.arrayToList(toLongArray(rawIdentities));
						if (relatedObjectIdContainer.get(field) == null) {
							relatedObjectIdContainer.put(field, new ArrayList<>());
						}
						relatedObjectIdContainer.get(field).addAll(idList);
						log.info("relatedObjectIdContainer.get(field).addAll(idList); {}", idList);

					}

				}
			} catch (Exception e) {
				log.error("Error validating field, will conitnue loop");
				e.printStackTrace();
			}
		}
		return baseEntity;
	}

	public static Long[] toLongArray(String[] strings) {
		Long[] longs = new Long[strings.length];

		for (int i = 0; i < strings.length; i++) {
			longs[i] = Long.valueOf(strings[i]);
		}

		return longs;
	}

}
