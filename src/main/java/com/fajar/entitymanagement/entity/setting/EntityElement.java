package com.fajar.entitymanagement.entity.setting;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;
import javax.persistence.JoinColumn;

import com.fajar.entitymanagement.annotation.AdditionalQuestionField;
import com.fajar.entitymanagement.annotation.BaseField;
import com.fajar.entitymanagement.annotation.Dto;
import com.fajar.entitymanagement.annotation.FormField;
import com.fajar.entitymanagement.dto.FieldType;
import com.fajar.entitymanagement.entity.BaseEntity;
import com.fajar.entitymanagement.util.EntityUtil;
import com.fajar.entitymanagement.util.MyJsonUtil;
import com.fajar.entitymanagement.util.StringUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@AllArgsConstructor
@Dto
@Slf4j
public class EntityElement implements Serializable {

	static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	/**
	 * 
	 */
	private static final long serialVersionUID = -6768302238247458766L;
	private String id;
	private String type;
	private String className;
	private boolean identity;
	private boolean required;
	private boolean idField;
	private String lableName;
	private List<BaseEntity> options;
	private String jsonList;
	private String optionItemName;
	private String optionValueName;
	private String entityReferenceName;
	private String entityReferenceClass;
	private boolean multiple;
	private boolean showDetail;
	private String detailFields;
	private String[] defaultValues;
	private List<Object> plainListValues;

	private final boolean isGrouped;
	private String inputGroupname;

	private boolean detailField;

	// not shown in view

	public final Field field;
	public final boolean ignoreBaseField;
	public EntityProperty entityProperty;
	public Map<String, List<?>> additionalMap;

	private FormField formField;
	private BaseField baseField;
	private boolean skipBaseField;
	private boolean hasJoinColumn;

//	public static void main(String[] args) {
//		String json = "[{\\\"serialVersionUID\\\":\\\"4969863194918869183\\\",\\\"name\\\":\\\"Kebersihan\\\",\\\"description\\\":\\\"1111111\\t\\t\\t\\t\\t\\\",\\\"serialVersionUID\\\":\\\"-8161890497812023383\\\",\\\"id\\\":1,\\\"color\\\":null,\\\"fontColor\\\":null,\\\"createdDate\\\":\\\"2020-05-14 21:06:03.0\\\",\\\"modifiedDate\\\":\\\"2020-05-14 21:06:03.0\\\",\\\"deleted\\\":\\\"false\\\"},{\\\"serialVersionUID\\\":\\\"4969863194918869183\\\",\\\"name\\\":\\\"Mukafaah\\\",\\\"description\\\":\\\"dfdffd\\\",\\\"serialVersionUID\\\":\\\"-8161890497812023383\\\",\\\"id\\\":2,\\\"color\\\":\\\"#000000\\\",\\\"fontColor\\\":\\\"#000000\\\",\\\"createdDate\\\":\\\"2020-05-12 21:16:58.0\\\",\\\"modifiedDate\\\":\\\"2020-05-12 21:16:58.0\\\",\\\"deleted\\\":\\\"false\\\"},{\\\"serialVersionUID\\\":\\\"4969863194918869183\\\",\\\"name\\\":\\\"Alat Tulis\\\",\\\"description\\\":\\\"alat tulis kantor\\t\\t\\t\\t\\t\\t\\\",\\\"serialVersionUID\\\":\\\"-8161890497812023383\\\",\\\"id\\\":3,\\\"color\\\":null,\\\"fontColor\\\":null,\\\"createdDate\\\":\\\"2020-05-12 21:56:36.0\\\",\\\"modifiedDate\\\":\\\"2020-05-12 21:56:36.0\\\",\\\"deleted\\\":\\\"false\\\"}]";
//		System.out.println(json.replace("\\t", ""));
//	}

	public EntityElement(Field field, EntityProperty entityProperty) {
		this.field = field;
		this.ignoreBaseField = entityProperty.isIgnoreBaseField();
		this.entityProperty = entityProperty;
		this.isGrouped = entityProperty.isQuestionare();
		init();
	}

	public EntityElement(Field field, EntityProperty entityProperty, Map<String, List<?>> additionalMap) {
		this.field = field;
		this.ignoreBaseField = entityProperty.isIgnoreBaseField();
		this.entityProperty = entityProperty;
		this.additionalMap = additionalMap;
		this.isGrouped = entityProperty.isQuestionare();
		init();
	}

	private void init() {
		formField = field.getAnnotation(FormField.class);
		baseField = field.getAnnotation(BaseField.class);

		idField = field.getAnnotation(Id.class) != null;
		skipBaseField = !idField && (baseField != null && ignoreBaseField);

		identity = idField;
		hasJoinColumn = field.getAnnotation(JoinColumn.class) != null;

		checkIfGroupedInput();
	}

	public String getJsonListString(boolean removeBeginningAndEndIndex) {
		try {
			String jsonStringified = OBJECT_MAPPER.writeValueAsString(jsonList).trim();
			if (removeBeginningAndEndIndex) {
				StringBuilder stringBuilder = new StringBuilder(jsonStringified);
				stringBuilder.setCharAt(0, ' ');
				stringBuilder.setCharAt(jsonStringified.length() - 1, ' ');
				jsonStringified = stringBuilder.toString().trim();
				log.info("jsonStringified: {}", jsonStringified);
			}
			jsonStringified = jsonStringified.replace("\\t", "");
			jsonStringified = jsonStringified.replace("\\r", "");
			jsonStringified = jsonStringified.replace("\\n", "");
			log.info("RETURN jsonStringified: {}", jsonStringified);
			return jsonStringified;
		} catch (Exception e) {
			return "{}";
		}
	}

	private void checkIfGroupedInput() {
		 
		if (isGrouped) {
			AdditionalQuestionField annotation = field.getAnnotation(AdditionalQuestionField.class);
			inputGroupname = annotation != null ? annotation.value() : AdditionalQuestionField.DEFAULT_GROUP_NAME;
		}
	}

	public boolean build() throws Exception {
		boolean result = doBuild();
		setEntityProperty(null);
		return result;
	}

	private boolean doBuild() throws Exception {

		boolean formFieldIsNullOrSkip = (formField == null || skipBaseField);
		if (formFieldIsNullOrSkip) {
			return false;
		}

		String lableName = formField.lableName().equals("") ? field.getName() : formField.lableName();
		FieldType determinedFieldType = determineFieldType();

		try {

			checkFieldType(determinedFieldType);
			boolean hasJoinColumn = field.getAnnotation(JoinColumn.class) != null;

			if (hasJoinColumn) {
				processJoinColumn(determinedFieldType);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			throw e1;
		}

		checkDetailField();
		setId(field.getName());
		setIdentity(idField);
		setLableName(StringUtil.extractCamelCase(lableName));
		setRequired(formField.required());
		setType(determinedFieldType.value);
		setMultiple(formField.multiple());
		setClassName(field.getType().getCanonicalName());
		setShowDetail(formField.showDetail());
		return true;
	}

	private void checkDetailField() {

		if (formField.detailFields().length > 0) {
			setDetailFields(String.join("~", formField.detailFields()));
		}
		if (formField.showDetail()) {
			setOptionItemName(formField.optionItemName());
			setDetailField(true);
		}
	}

	private void checkFieldType(FieldType fieldType) throws Exception {

		if (fieldType.equals(FieldType.FIELD_TYPE_IMAGE)) {
			processImageType();

		} else if (fieldType.equals(FieldType.FIELD_TYPE_CURRENCY)) {
			processCurrencyType();

		} else if (fieldType.equals(FieldType.FIELD_TYPE_DATE)) {
			processDateType();

		} else if (fieldType.equals(FieldType.FIELD_TYPE_PLAIN_LIST)) {
			processPlainListType();

		}

	}

	private void processCurrencyType() {
		entityProperty.getCurrencyElements().add(field.getName());
	}

	private void processImageType() {
		entityProperty.getImageElements().add(field.getName());
	}

	private void processDateType() {
		entityProperty.getDateElements().add(field.getName());
	}

	private void processPlainListType() throws Exception {

		String[] availableValues = formField.availableValues();

		if (availableValues.length > 0) {
			setPlainListValues(Arrays.asList(availableValues));

		} else if (field.getType().isEnum()) {
			Object[] enumConstants = field.getType().getEnumConstants();
			setPlainListValues(Arrays.asList(enumConstants));

		} else {
			log.error("Ivalid element: {}", field.getName());
			throw new Exception("Invalid Element");
		}
	}

	private FieldType determineFieldType() {

		FieldType fieldType;

		if (EntityUtil.isNumericField(field)) {
			fieldType = FieldType.FIELD_TYPE_NUMBER;

		} else if (field.getType().equals(Date.class) && field.getAnnotation(JsonFormat.class) == null) {
			fieldType = FieldType.FIELD_TYPE_DATE;

		} else if (idField) {
			fieldType = FieldType.FIELD_TYPE_HIDDEN;
		} else {
			fieldType = formField.type();
		}
		return fieldType;
	}

	private void processJoinColumn(FieldType fieldType) throws Exception {
		log.info("field {} of {} is join column, type: {}", field.getName(), field.getDeclaringClass(), fieldType);

		Class<?> referenceEntityClass = field.getType();
		Field referenceEntityIdField = EntityUtil.getIdFieldOfAnObject(referenceEntityClass);

		if (referenceEntityIdField == null) {
			throw new Exception("ID Field Not Found");
		}

		if (fieldType.equals(FieldType.FIELD_TYPE_FIXED_LIST) && additionalMap != null) {

			List<BaseEntity> referenceEntityList = (List<BaseEntity>) additionalMap.get(field.getName());
			if (null == referenceEntityList || referenceEntityList.size() == 0) {
				throw new RuntimeException(
						"Invalid object list provided for key: " + field.getName() + " in EntityElement.AdditionalMap");
			}
			log.info("Additional map with key: {} . Length: {}", field.getName(), referenceEntityList.size());
			if (referenceEntityList != null) {
				setOptions(referenceEntityList);
				setJsonList(MyJsonUtil.listToJson(referenceEntityList));
			}

		} else if (fieldType.equals(FieldType.FIELD_TYPE_DYNAMIC_LIST)) {

			setEntityReferenceClass(referenceEntityClass.getSimpleName());
		}

		setOptionValueName(referenceEntityIdField.getName());
		setOptionItemName(formField.optionItemName());
	}

}
