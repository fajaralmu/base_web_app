package com.fajar.entitymanagement.querybuilder;

import static com.fajar.entitymanagement.util.EntityUtil.getClassAnnotation;
import static com.fajar.entitymanagement.util.EntityUtil.getDeclaredField;
import static com.fajar.entitymanagement.util.EntityUtil.getIdFieldOfAnObject;
import static com.fajar.entitymanagement.util.StringUtil.buildString;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import com.fajar.entitymanagement.annotation.CustomEntity;
import com.fajar.entitymanagement.annotation.FormField;
import com.fajar.entitymanagement.dto.Filter;
import com.fajar.entitymanagement.dto.KeyPair;
import com.fajar.entitymanagement.entity.BaseEntity;
import com.fajar.entitymanagement.util.EntityUtil;
import com.fajar.entitymanagement.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueryUtil {
	
	
	public static final String DAY_SUFFIX = "-day";
	public static final String MONTH_SUFFIX = "-month";
	public static final String YEAR_SUFFIX = "-year";
	public static final String FILTER_DATE_DAY = "DAY";
	public static final String FILTER_DATE_MON1TH = "MONTH";
	public static final String FILTER_DATE_YEAR = "YEAR";
	
	//placeholders
	public static final String SQL_RAW_DATE_FILTER = " ${MODE}(`${TABLE_NAME}`.`${COLUMN_NAME}`) = ${VALUE} ";
	public static final String PLACEHOLDER_SQL_FOREIGN_ID = "${FOREIGN_ID}";
	public static final String PLACEHOLDER_SQL_JOIN_TABLE = "${JOIN_TABLE}";
	public static final String PLACEHOLDER_SQL_ENTITY_TABLE = "${ENTITY_TABLE}";
	public static final String PLACEHOLDER_SQL_JOIN_ID = "${JOIN_ID}";
	public static final String PLACEHOLDER_SQL_RAW_JOIN_STATEMENT = " LEFT JOIN `${JOIN_TABLE}` ON  `${JOIN_TABLE}`.`${JOIN_ID}` = `${ENTITY_TABLE}`.`${FOREIGN_ID}` ";
	public static final String PLACEHOLDER_SQL_TABLE_NAME = "${TABLE_NAME}";
	public static final String PLACEHOLDER_SQL_MODE = "${MODE}";
	public static final String PLACEHOLDER_SQL_COLUMN_NAME = "${COLUMN_NAME}";
	public static final String PLACEHOLDER_SQL_VALUE = "${VALUE}";
	
	public static final String SQL_KEYWORDSET_SELECT_COUNT = " SELECT COUNT(*) from  ";
	public static final String SQL_KEYWORD_SELECT = " SELECT "; 
	public static final String SQL_KEYWORD_LIMIT = " LIMIT ";
	public static final String SQL_KEYWORD_OFFSET = " OFFSET ";
	public static final String SQL_KEYWORD_ORDERBY = " ORDER BY ";
	public static final String SQL_KEYWORD_AND = " AND ";
	public static final String SQL_KEYWORD_WHERE = " WHERE ";
	public static final String SQL_KEYWORD_FROM = " from ";
	public static final String TABLE_NAME = "table_name_key";
	
	public static Field getFieldByName(String name, List<Field> fields) {
		return EntityUtil.getObjectFromListByFieldName("name", name, fields);
	}

	public static String getColumnName(Field field) {
		log.info("get column Name " + field.getDeclaringClass() + " from " + field.getName());

		if (field.getAnnotation(Column.class) == null)
			return field.getName();
		String columnName = ((Column) field.getAnnotation(Column.class)).name();
		if (columnName == null || columnName.equals("")) {
			columnName = field.getName();
		}
		return columnName;
	}

	
	/**
	 * create LEFT JOIN Statement for one field only
	 * @param entityClass
	 * @param field
	 * @return
	 */
	public static String createLeftJoinQueryByField(Class<?> entityClass, Field field) { 
		log.info("Create item sql left join: " + entityClass + ", field: " + field);

		JoinColumn joinColumn = EntityUtil.getFieldAnnotation(field, JoinColumn.class);

		if (null == joinColumn) {
			return "";
		}
 
		Class<?> fieldClass 	= field.getType();
		Field idForeignField 	= getIdFieldOfAnObject(fieldClass);

		String joinTableName 	= getTableName(fieldClass);
		String tableName 		= getTableName(entityClass);
		String foreignID 		= joinColumn.name();


		String sqlItem = PLACEHOLDER_SQL_RAW_JOIN_STATEMENT.
				replace(PLACEHOLDER_SQL_FOREIGN_ID, foreignID).
				replace(PLACEHOLDER_SQL_JOIN_TABLE, joinTableName).
				replace(PLACEHOLDER_SQL_ENTITY_TABLE, tableName).
				replace(PLACEHOLDER_SQL_JOIN_ID, getColumnName(idForeignField));

		return sqlItem;

	}

	/**
	 * create LEFT JOIN statement full object
	 * @param entityClass
	 * @return
	 */
	public static String createLeftJoinQueryFullObject(Class<? extends BaseEntity> entityClass) {

		StringBuilder sql = new StringBuilder("");

		CustomEntity customModel = getClassAnnotation(entityClass, CustomEntity.class);

		List<Field> fields = EntityUtil.getDeclaredFields(entityClass);
		for (Field field : fields) {

			if (customModel != null ) {
				//&& EntityUtil.existInList(field.getName(), Arrays.asList(customModel.rootFilter()))) {
				continue;
			}

			JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
			if (joinColumn != null) {

				String sqlItem = createLeftJoinQueryByField(entityClass, field); 
				sql = sql.append(sqlItem); 
			}
		}

		if (customModel != null) {//&& customModel.rootFilter().length > 0) {
			sql = sql.append(validateRootFilter(entityClass, new String[] {}));// customModel.rootFilter()));
		}

		return sql.toString();
	}

	/**
	 * add join clause if class has root filter
	 * @param entityClass
	 * @param rootFilter
	 * @return
	 */
	public static String validateRootFilter(Class<? extends BaseEntity> entityClass, String[] rootFilter) {

		StringBuilder stringBuilder = new StringBuilder("");

		Class<?> currentType = entityClass;
		Field currentField = null;

		for (String string : rootFilter) {

			try {
				currentField = currentType.getDeclaredField(string);

				String sqlJoinItem = createLeftJoinQueryByField(currentType, currentField);
				stringBuilder = stringBuilder.append(sqlJoinItem);

				currentType = currentField.getType();

			} catch ( Exception e) {
				e.printStackTrace();
			}

		}

		return stringBuilder.toString();
	}

	public static String createWhereClause(Class<?> entityClass, Map<String, Object> filter,  
		 final	boolean allItemExactSearch ) {

		String tableName 						= getTableName(entityClass);
		List<QueryFilterItem> sqlFilters 		= new ArrayList<QueryFilterItem>();
		List<Field> entityDeclaredFields 		= EntityUtil.getDeclaredFields(entityClass);

		log.info("=======FILTER: {}", filter);
		
		filter.put(TABLE_NAME, tableName);

		for (final String rawKey : filter.keySet()) {
			log.info("................." + rawKey + ":" + filter.get(rawKey));
			
			if (filter.get(rawKey) == null)
				continue;

			String currentKey = rawKey;
			boolean itemExacts = allItemExactSearch; 
			String filterTableName = tableName; 
			String finalNameAfterExactChecking = currentItemExact(rawKey);
			
			if(null != finalNameAfterExactChecking) {
				currentKey = finalNameAfterExactChecking;
				itemExacts = true;
			}
			
			log.info("Raw key: {} Now KEY: {}", rawKey, currentKey); 
			
			QueryFilterItem queryItem = new QueryFilterItem();
			queryItem.setExacts(itemExacts);

			// check if date
			QueryFilterItem dateFilterSql = getDateFilter(rawKey, currentKey, entityDeclaredFields, filter);

			if(null != dateFilterSql) {
				sqlFilters.add(dateFilterSql);
				continue;
			}
			
			Field field = getFieldByName(currentKey, entityDeclaredFields);

			if (field == null) {
				log.warn("Field Not Found :" + currentKey + " !");
				continue; 
			}
			
			String filterColumnName = getColumnName(field); 
			KeyPair joinColumnResult = checkIfJoinColumn(currentKey, field, false);
			
			if(null != joinColumnResult) {
				if(joinColumnResult.isValid()) {
					filterTableName = joinColumnResult.getKey().toString();
					filterColumnName = joinColumnResult.getValue().toString();
				} else {
					continue;
				}
			}
			
			queryItem.setTableName(filterTableName);
			queryItem.setColumnName(filterColumnName);
			queryItem.setValue(filter.get(rawKey));
			sqlFilters.add(queryItem ); 
		} 
 
		return completeWhereClause(sqlFilters);
		 
	}
	
	/**
	 * return keyValue of tableName and columnName of the referenced entity
	 * @param currentKey
	 * @param field
	 * @param actualColumnName
	 * @return
	 */
	public static KeyPair checkIfJoinColumn(String currentKey, Field field, boolean actualColumnName) {
		
		String multiKeyColumnName = getMultiKeyColumnName(currentKey);
		KeyPair keyValue = new KeyPair();
		boolean isMultiKey 	= null != multiKeyColumnName; 
		boolean validColumn = false;
		
		if (field.getAnnotation(JoinColumn.class) != null || isMultiKey) { 

			try {
				Class<?> fieldClass		= field.getType();
				String joinTableName 	= getTableName(fieldClass); 
				String referenceFieldName = "";

				if (isMultiKey) {
					referenceFieldName = multiKeyColumnName;
				}else {
					referenceFieldName = getOptionItemName(field);
				}

				Field 	referenceEntityField 	= getDeclaredField(fieldClass, referenceFieldName);
				String 	fieldColumnName 		= actualColumnName ? getColumnName(referenceEntityField)  : referenceFieldName;

				if (fieldColumnName == null || fieldColumnName.equals("")) {
					validColumn = false;
				}else {
				  
					keyValue.setKey(joinTableName);
					keyValue.setValue(fieldColumnName);
					validColumn = true;
				}
				
			} catch ( Exception e) {
				
				log.warn(e.getClass() + " " + e.getMessage() + " " + field.getType());
				e.printStackTrace(); 
				validColumn = false;
			}

		} else { 
			return null;
		} 
		
		keyValue.setValid(validColumn);
		log.info("keyValue: {}", keyValue);
		return keyValue;
	}

	private static String getMultiKeyColumnName(String currentKey) {
		String[] multiKey 	= currentKey.split(",");
		boolean isMultiKey 	= multiKey.length == 2;
		if (isMultiKey) {
			log.info("Multi Key: {}", currentKey);
			log.info("key name: {}", multiKey[1]);
			return  multiKey[1]; 
		} 
		else {
			return null;
		}
	}

	private static String completeWhereClause(List<QueryFilterItem> sqlFilters) {
		String whereClause = "";

		if (sqlFilters.size() > 0) {
			whereClause = generateQueryFilterString( sqlFilters);
		} else {
			return "";
		} 

		String result = SQL_KEYWORD_WHERE.concat(whereClause); 
		return result;
	}

	private static String currentItemExact(String rawKey) { 
		if (rawKey.endsWith("[EXACTS]")) { 
			String finalKey = rawKey.split("\\[EXACTS\\]")[0];
			log.info("{} exact search",finalKey);
			return finalKey;
		}
		return null;
	}

	private static String getOptionItemName(Field field) {
		FormField formField 	= field.getAnnotation(FormField.class);
		String referenceFieldName = formField.optionItemName();
		return referenceFieldName;
	}

	public static String generateQueryFilterString(List<QueryFilterItem> queryFilterItems) {
		List<String> listOfSqlFilter = new ArrayList<String>();
		for (int i = 0; i < queryFilterItems.size(); i++) {
			String sqlString = queryFilterItems.get(i).generateSqlString();
			
			listOfSqlFilter.add(sqlString); 
		}
		return String.join(SQL_KEYWORD_AND, listOfSqlFilter);
	}

	/**
	 * generate date filter sql
	 * @param rawKey
	 * @param key
	 * @param fields
	 * @param filter
	 * @return
	 */
	private static QueryFilterItem getDateFilter(String rawKey, String key, List<Field > fields, Map filter) {
		boolean dayFilter 	= rawKey.endsWith(DAY_SUFFIX);
		boolean monthFilter = rawKey.endsWith(MONTH_SUFFIX);
		boolean yearFilter 	= rawKey.endsWith(YEAR_SUFFIX);
		

		if (dayFilter || monthFilter || yearFilter) {

			String fieldName	= key;
			String mode 		= FILTER_DATE_DAY; 
			
			if (dayFilter) {
				fieldName 	= key.replace(DAY_SUFFIX, "");
				mode 		= FILTER_DATE_DAY;

			} else if (monthFilter) {
				fieldName 	= key.replace(MONTH_SUFFIX, "");
				mode 		= FILTER_DATE_MON1TH;

			} else if (yearFilter) {
				fieldName	= key.replace(YEAR_SUFFIX, "");
				mode 		= FILTER_DATE_YEAR;

			}

			Field field = getFieldByName(fieldName, fields);

			if (field == null) {
				log.warn("FIELD NOT FOUND: " + fieldName + " !");
				return null;

			}

			String columnName = getColumnName(field);
			String tableName = filter.get(TABLE_NAME).toString(); 
			
			QueryFilterItem queryItem = new QueryFilterItem();
			queryItem.setTableName(tableName);
			queryItem.setColumnName(columnName);
			queryItem.setDateMode(mode);
			queryItem.setValue( filter.get(key).toString());
			queryItem.setExacts(true); 
			
			return queryItem;
		}
		return null;
	} 
  
	public static String orderSQL(Class<?> entityClass, String orderType, String orderBy) {

		/**
		 * order by field
		 */
		Field orderByField = getDeclaredField(entityClass, orderBy);

		if (orderByField == null) {
			log.error("Order by field of {} not found: {}",orderBy, entityClass.getName() );
			return null;
		}
		 
		String orderColumnName;
		String tableName;

		if (orderByField.getAnnotation(JoinColumn.class) != null) {
			
			Class<?> fieldType 	= orderByField.getType();
			FormField formField = orderByField.getAnnotation(FormField.class);
			tableName 			= getTableName(fieldType); 
			String joinFieldName = formField.optionItemName();

			try {
				Field fieldField = fieldType.getDeclaredField(joinFieldName);
				orderColumnName = getColumnName(fieldField);

			} catch ( Exception e) {
				e.printStackTrace();
				return null;
			}
		} else {
			tableName = getTableName(entityClass);
			orderColumnName = getColumnName(orderByField);
		}

		String orderField = doubleQuoteMysql(tableName).concat(".").concat(doubleQuoteMysql(orderColumnName));

		return buildString(SQL_KEYWORD_ORDERBY, orderField, orderType);
	}

	public static String getTableName(Class<?> entityClass) {
		log.info("getTableName From entity class: " + entityClass.getCanonicalName());
		
		Table table = getClassAnnotation(entityClass, Table.class);

		if (table != null) {
			boolean tableNameExist = table.name() != null && !table.name().equals("");
			if (tableNameExist) {
				return table.name();
			}
		}
		return entityClass.getSimpleName().toLowerCase();
	}
	
	 
	/**
	 * generate sql Select * From and sql Select Count (*)
	 * @param filter
	 * @param entityClass
	 * @return
	 */
	public static CRUDQueryHolder generateSqlByFilter(Filter filter, Class<? extends BaseEntity> entityClass ) {

		log.info("CRITERIA-FILTER: {}", filter);
		log.info("entity class: {}", entityClass);
 
		CRUDQueryHolder queryHolder = new CRUDQueryHolder(entityClass, filter);
		queryHolder.buildSqlSelectAndSingleResult();
		
		return queryHolder ;
	}

	static String doubleQuoteMysql(Object str) {
		return StringUtil.doubleQuoteMysql(str.toString());
	}  
	
	/**
	 * 
	 * @param <T>
	 * @param _class
	 * @return fields having BaseEntity superClass type and @JoinColumn annotation
	 */
	public static  <T extends BaseEntity> List<Field> getJoinColumnFields(Class<T> _class){
		List<Field> joinColumns = new ArrayList<>();
		
		List<Field> fields = EntityUtil.getDeclaredFields(_class);
		for (int i = 0; i < fields.size(); i++) {
			final Field field = fields.get(i);
			
			JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
			if(null != joinColumn && field.getType().getSuperclass().equals(BaseEntity.class)) {
				field.setAccessible(true);
				joinColumns.add(field);
			}
		}
		
		return joinColumns;
	}

}
