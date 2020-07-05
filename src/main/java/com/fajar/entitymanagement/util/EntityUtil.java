package com.fajar.entitymanagement.util;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.lang3.SerializationUtils;

import com.fajar.entitymanagement.annotation.AdditionalQuestionField;
import com.fajar.entitymanagement.annotation.Dto;
import com.fajar.entitymanagement.annotation.FormField;
import com.fajar.entitymanagement.dto.FieldType;
import com.fajar.entitymanagement.entity.BaseEntity;
import com.fajar.entitymanagement.entity.setting.EntityElement;
import com.fajar.entitymanagement.entity.setting.EntityProperty;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EntityUtil {

	public static EntityProperty createEntityProperty(Class<?> clazz, HashMap<String, List<?>> additionalObjectList)
			throws Exception {
		if (clazz == null || getClassAnnotation(clazz, Dto.class) == null) {
			return null;
		}

		Dto dto = (Dto) getClassAnnotation(clazz, Dto.class);
		final boolean ignoreBaseField = dto.ignoreBaseField();
		final boolean isQuestionare = dto.quistionare();

		EntityProperty entityProperty = EntityProperty.builder().ignoreBaseField(ignoreBaseField)
				.entityName(clazz.getSimpleName().toLowerCase()).isQuestionare(isQuestionare).build();
		try {

			List<Field> fieldList = getDeclaredFields(clazz);

			if (isQuestionare) {
				Map<String, List<Field>> groupedFields = sortListByQuestionareSection(fieldList);
				fieldList = CollectionUtil.mapOfListToList(groupedFields);
				Set<String> groupKeys = groupedFields.keySet();
				String[] keyNames = CollectionUtil.toArrayOfString(groupKeys.toArray());

				entityProperty.setGroupNames(keyNames);
			}
			List<EntityElement> entityElements = new ArrayList<>();
			List<String> fieldNames = new ArrayList<>();
			String fieldToShowDetail = "";

			for (Field field : fieldList) {

				final EntityElement entityElement = new EntityElement(field, entityProperty, additionalObjectList);

				if (false == entityElement.build()) {
					continue;
				}
				if (entityElement.isDetailField()) {
					fieldToShowDetail = entityElement.getId();
				}

				fieldNames.add(entityElement.getId());
				entityElements.add(entityElement);
			}

			entityProperty.setAlias(dto.value().isEmpty() ? clazz.getSimpleName() : dto.value());
			entityProperty.setEditable(dto.editable());
			entityProperty.setElementJsonList();
			entityProperty.setElements(entityElements);
			entityProperty.setDetailFieldName(fieldToShowDetail);
			entityProperty.setDateElementsJson(MyJsonUtil.listToJson(entityProperty.getDateElements()));
			entityProperty.setFieldNames(MyJsonUtil.listToJson(fieldNames));
			entityProperty.setFieldNameList(fieldNames);
			entityProperty.setFormInputColumn(dto.formInputColumn().value);
			entityProperty.determineIdField();

			log.info("============ENTITY PROPERTY: {} ", entityProperty);

			return entityProperty;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	public static void main(String[] args) {
//		List<Field> fields = getDeclaredFields(StudentParent.class);
//		fields = sortListByQuestionareSection(fields);
//		
//		for (Field field : fields) {
//			log.debug("{}", field.getName());
//		}
//		
//		Object[] arrayOfFields = fields.toArray();
	}

	static boolean isIdField(Field field) {
		return field.getAnnotation(Id.class) != null;
	}

	private static Map<String, List<Field>> sortListByQuestionareSection(List<Field> fieldList) {
		Map<String, List<Field>> temp = MapUtil.singleMap(AdditionalQuestionField.DEFAULT_GROUP_NAME,
				new ArrayList<>());

		String key = AdditionalQuestionField.DEFAULT_GROUP_NAME;
		for (Field field : fieldList) {
			FormField formField = field.getAnnotation(FormField.class);
			boolean isIDField = isIdField(field);

			if (null == formField) {
				continue;
			}
			AdditionalQuestionField additionalQuestionField = field.getAnnotation(AdditionalQuestionField.class);
			if (null == additionalQuestionField || isIDField) {
				key = "OTHER";
				log.debug("{} has no additionalQuestionareField", field.getName());
			} else {
				key = additionalQuestionField.value();
			}
			if (temp.get(key) == null) {
				temp.put(key, new ArrayList<>());
			}
			temp.get(key).add(field);
			log.debug("{}: {}", key, field.getName());
		}
		log.debug("QUestionare Map: {}", temp);
		return (temp);

	}

	public static <T extends Annotation> T getClassAnnotation(Class<?> entityClass, Class<T> annotation) {
		try {
			return entityClass.getAnnotation(annotation);
		} catch (Exception e) {
			return null;
		}
	}

	public static <T extends Annotation> T getFieldAnnotation(Field field, Class<T> annotation) {
		try {
			return field.getAnnotation(annotation);
		} catch (Exception e) {
			return null;
		}
	}

	public static Field getDeclaredField(Class<?> clazz, String fieldName) {
		try {
			Field field = clazz.getDeclaredField(fieldName);
			if (field == null) {

			}
			field.setAccessible(true);
			return field;

		} catch (Exception e) {
			log.error("Error get declared field in the class, and try access super class");
		}
		if (clazz.getSuperclass() != null) {

			try {
				log.info("TRY ACCESS SUPERCLASS");

				Field superClassField = clazz.getSuperclass().getDeclaredField(fieldName);
				superClassField.setAccessible(true);
				return superClassField;
			} catch (Exception e) {

				log.error("FAILED Getting FIELD: " + fieldName);
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * get fields of a class, accessible true
	 * 
	 * @param clazz
	 * @return
	 */
	public static List<Field> getDeclaredFields(Class<?> clazz) {
		Field[] baseField = clazz.getDeclaredFields();
//
//		List<EntityElement> entityElements = new ArrayList<EntityElement>();
		List<Field> fieldList = new ArrayList<>();

		for (Field field : baseField) {
			field.setAccessible(true);
			fieldList.add(field);
		}
		if (clazz.getSuperclass() != null) {

			Field[] parentFields = clazz.getSuperclass().getDeclaredFields();

			for (Field field : parentFields) {
				field.setAccessible(true);
				fieldList.add(field);
			}

		}
		return fieldList;
	}

	public static Field getIdFieldOfAnObject(Class<?> clazz) {
		log.info("Get ID FIELD FROM :" + clazz.getCanonicalName());

		if (getClassAnnotation(clazz, Entity.class) == null) {
			return null;
		}
		List<Field> fields = getDeclaredFields(clazz);

		for (Field field : fields) {

			if (field.getAnnotation(Id.class) != null) {

				return field;
			}
		}

		return null;
	}

	public static boolean isNumericField(Field field) {
		return field.getType().equals(Integer.class) || field.getType().equals(Double.class)
				|| field.getType().equals(Long.class) || field.getType().equals(BigDecimal.class)
				|| field.getType().equals(BigInteger.class);
	}

	/**
	 * copy object with option ID included or NOT
	 * 
	 * @param source
	 * @param targetClass
	 * @param withId
	 * @return
	 */
	public static BaseEntity copyFieldElementProperty(BaseEntity source, Class<? extends BaseEntity> targetClass,
			boolean withId) {
		log.info("Will Copy Class :" + targetClass.getCanonicalName());

		BaseEntity targetObject = null;
		try {
			targetObject = (BaseEntity) targetClass.newInstance();

		} catch (Exception e) {
			log.error("Error when create instance");
			e.printStackTrace();
		}
		List<Field> fields = getDeclaredFields(source.getClass());

		for (Field field : fields) {

			if (field.getAnnotation(Id.class) != null && !withId) {
				continue;
			}
			if (isStaticField(field)) {
				continue;
			}

			Field currentField = getDeclaredField(targetClass, field.getName());

			if (currentField == null)
				continue;

			currentField.setAccessible(true);
			field.setAccessible(true);

			try {
				currentField.set(targetObject, field.get(source));

			} catch (Exception e) {
				log.error("Error set new value");
				e.printStackTrace();
			}

		}

		if (targetObject.getCreatedDate() == null) {
			targetObject.setCreatedDate(new Date());
		}
		targetObject.setModifiedDate(new Date());

		return targetObject;
	}

	public static void validateDefaultValues(List<? extends BaseEntity> entities) {
		for (int i = 0; i < entities.size(); i++) {
			validateDefaultValue(entities.get(i));
		}
	}

	public static boolean isStaticField(Field field) {
		return Modifier.isStatic(field.getModifiers());
	}

	public static <T extends BaseEntity> T validateDefaultValue(T baseEntity) {
		List<Field> fields = EntityUtil.getDeclaredFields(baseEntity.getClass());

		for (Field field : fields) {

			try {

				field.setAccessible(true);
				FormField formField = field.getAnnotation(FormField.class);

				if (field.getType().equals(String.class) && formField != null
						&& formField.defaultValue().equals("") == false) {

					Object value = field.get(baseEntity);

					if (value == null || value.toString().equals("")) {
						field.set(baseEntity, formField.defaultValue());
					}

				}

				if (formField != null && formField.multiply().length > 1) {
					Object objectValue = field.get(baseEntity);

					if (objectValue != null)
						continue;

					Object newValue = "1";
					String[] multiplyFields = formField.multiply();

					loop: for (String multiplyFieldName : multiplyFields) {

						Field multiplyField = getDeclaredField(baseEntity.getClass(), multiplyFieldName);

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

				}
			} catch (Exception e) {
				log.error("Error validating field, will conitnue loop");
				e.printStackTrace();
			}
		}
		return baseEntity;
	}

	public static <T extends BaseEntity> List<T> validateDefaultValue(List<T> entities) {
		for (T baseEntity : entities) {
			baseEntity = validateDefaultValue(baseEntity);
		}
		return entities;
	}

	public static <T> T getObjectFromListByFieldName(final String fieldName, final Object value, final List<T> list) {

		for (T object : list) {
			Field field = EntityUtil.getDeclaredField(object.getClass(), fieldName);
			field.setAccessible(true);
			try {
				Object fieldValue = field.get(object);

				if (fieldValue != null && fieldValue.equals(value)) {
					return (T) object;
				}

			} catch (Exception e) {

				e.printStackTrace();
			}
		}

		return null;
	}

	public static <T> boolean existInList(T o, List<T> list) {
		if (null == list) {
			log.error("LIST IS NULL");
			return false;
		}
		for (T object : list) {
			if (object.equals(o)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Clone Serializable Object
	 * 
	 * @param <T>
	 * @param serializable
	 * @return
	 */
	public static <T extends Serializable> T cloneSerializable(T serializable) {
		try {
			return SerializationUtils.clone(serializable);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<Field> getFixedListFields(Class<? extends BaseEntity> entityClass) {
		List<Field> fields = new ArrayList<>();
		
		List<Field> declaredFields = getDeclaredFields(entityClass);
		for (int i = 0; i < declaredFields.size(); i++) {
			final Field field = declaredFields.get(i);
			
			FormField formField = getFieldAnnotation(field, FormField.class);
			if(null == formField) {
				continue;
			}
			
			boolean isBaseEntitySubClass = field.getType().getSuperclass().equals(BaseEntity.class);
			
			if(isBaseEntitySubClass && formField.type().equals(FieldType.FIELD_TYPE_FIXED_LIST)) {
				fields.add(field);
			}
			
		}
		return   fields;
	}

}
