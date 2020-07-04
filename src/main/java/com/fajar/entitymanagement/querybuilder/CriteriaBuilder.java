package com.fajar.entitymanagement.querybuilder;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;

import com.fajar.entitymanagement.dto.Filter;
import com.fajar.entitymanagement.dto.KeyPair;
import com.fajar.entitymanagement.entity.BaseEntity;
import com.fajar.entitymanagement.entity.ProductCategory;
import com.fajar.entitymanagement.entity.Customer;
import com.fajar.entitymanagement.entity.Product;
import com.fajar.entitymanagement.entity.ProductUnit;
import com.fajar.entitymanagement.entity.User;
import com.fajar.entitymanagement.entity.UserRole;
import com.fajar.entitymanagement.util.EntityUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CriteriaBuilder {

	private final Session hibernateSession;

	private String currentAlias;
	private int joinIndex;
	private final Class<? extends BaseEntity> _class;

	public CriteriaBuilder(Session hibernateSession, Class<? extends BaseEntity> _class) {
		this.hibernateSession = hibernateSession;
		this._class = _class;
	}

	{

		org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();

		configuration.setProperties(additionalProperties());
		configuration.addAnnotatedClass(Product.class);
		configuration.addAnnotatedClass(ProductUnit.class);
		configuration.addAnnotatedClass(ProductCategory.class);
		configuration.addAnnotatedClass(Transaction.class);
		configuration.addAnnotatedClass(Supplier.class);
		configuration.addAnnotatedClass(Customer.class);
		configuration.addAnnotatedClass(User.class);
		configuration.addAnnotatedClass(UserRole.class);
//		addAnnotatedClass(configuration);

//		factory = configuration./* setInterceptor(new HibernateInterceptor()). */buildSessionFactory();
//		session = factory.openSession();
	}

	private Properties additionalProperties() {

		String dialect = "org.hibernate.dialect.MySQLDialect";
		String ddlAuto = "update";

		Properties properties = new Properties();
		properties.setProperty("hibernate.dialect", dialect);
		properties.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/goodshop");
		properties.setProperty("hibernate.connection.username", "root");
		properties.setProperty("hibernate.connection.password", "");

		properties.setProperty("hibernate.connection.driver_class", com.mysql.jdbc.Driver.class.getCanonicalName());
		properties.setProperty("hibernate.current_session_context_class", "thread");
		properties.setProperty("hibernate.show_sql", "true");
		properties.setProperty("hibernate.connection.pool_size", "1");
		properties.setProperty("hbm2ddl.auto", ddlAuto);

		return properties;
	}

	public Criterion restrictionEquals(Class<?> entityClass, String fieldName, Object fieldValue) {
		String entityName = entityClass.getSimpleName();
		String columnName;
		boolean multiKey = fieldName.contains(",");
		Field field;
		
		if (multiKey) {
			Field hisField = EntityUtil.getDeclaredField(entityClass, fieldName.split(",")[0]);
			field = EntityUtil.getDeclaredField(hisField.getType(), fieldName.split(",")[1]);
			 
			fieldName = fieldName.replace(",", ".");
			fieldName = fieldName.replace( fieldName.split("\\.")[0], this.currentAlias);
			columnName = fieldName;
			
			return Restrictions.sqlRestriction(columnName+"='"+fieldValue+"'");
		} else {
			field = EntityUtil.getDeclaredField(entityClass, fieldName);
			columnName = entityName + '.' + fieldName;
		}

		if (field.getType().equals(String.class) == false) {
			return nonStringEqualsExp(entityClass, fieldName, fieldValue);
		}

		Object validatedValue = validateFieldValue(field, fieldValue);
		return Restrictions.naturalId().set(columnName, validatedValue);
	}

	private Criterion nonStringEqualsExp(Class<?> entityClass, String fieldName, Object value) {

		Criterion sqlRestriction = Restrictions.sqlRestriction("{alias}." + fieldName + " = '" + value + "'");

		return sqlRestriction;
	}

	private Object validateFieldValue(Field field, Object fieldValue) {
		if (null == fieldValue) {
			return 0;
		}

		Class<?> fieldType = field.getType();
		if (EntityUtil.isNumericField(field)) {
			if (fieldType.equals(Long.class)) {
				long value = Long.parseLong(fieldValue.toString());

				return value;
			}
		}
		return fieldValue;
	}

	private void setCurrentAlias(String aliasName) {
		if (null == aliasName)
			return;

		if (aliasName.equals("this")) {
			this.currentAlias = "this_";
		} else {

			joinIndex = getJoinColumnIndex(aliasName) + 1;
			
			if (aliasName.length() > 10) {
				aliasName = aliasName.substring(0, 10);
			}

			this.currentAlias = aliasName.toLowerCase() + joinIndex + '_';
		}
	}
	
	private int getJoinColumnIndex(String fieldName) {
		List<Field> joinColumns = QueryUtil.getJoinColumnFields(_class);
		for (int i = 0; i < joinColumns.size(); i++) {
			if(joinColumns.get(i).getName().equals(fieldName)) {
				return i;
			}
		}
		return -1;
	}

	public Criteria createCriteria(Class<?> entityClass, Filter filter, final boolean _allItemExactSearch) {
		Map<String, Object> fieldsFilter = filter.getFieldsFilter();
		List<Field> entityDeclaredFields = EntityUtil.getDeclaredFields(entityClass);

		log.info("=======FILTER: {}", fieldsFilter);
		boolean allItemExactSearch = filter.isExacts();

		String entityName = entityClass.getSimpleName();
		Criteria criteria = hibernateSession.createCriteria(entityClass, entityName);
		setCurrentAlias("this");

		for (final String rawKey : fieldsFilter.keySet()) {
			setCurrentAlias("this");

			log.info("##" + rawKey + ":" + fieldsFilter.get(rawKey));

			if (fieldsFilter.get(rawKey) == null)
				continue;

			String currentKey = rawKey;
			boolean itemExacts = allItemExactSearch;
			String finalNameAfterExactChecking = currentItemExact(rawKey);

			if (null != finalNameAfterExactChecking) {
				
				currentKey = finalNameAfterExactChecking;
				itemExacts = true;
				boolean multiKey = currentKey.contains(",");
				
				if(multiKey) {
					setCurrentAlias(currentKey.split(",")[0]);
				}
				
				criteria.add(restrictionEquals(entityClass, currentKey, fieldsFilter.get(rawKey)));
				continue;
			}

			log.info("Raw key: {} Now KEY: {}", rawKey, currentKey);

			// check if date
			Criterion dateFilterSql = getDateFilter(rawKey, currentKey, entityDeclaredFields, fieldsFilter);

			if (null != dateFilterSql) {
				log.info(" {} is date ", rawKey);
				criteria.add(dateFilterSql);
				continue;
			}

			boolean multiKey = rawKey.contains(",");

			Field field;
			if (multiKey) {
				Field hisField = EntityUtil.getDeclaredField(entityClass, rawKey.split(",")[0]);
				field = EntityUtil.getDeclaredField(hisField.getType(), rawKey.split(",")[1]);
			} else {
				field = QueryUtil.getFieldByName(currentKey, entityDeclaredFields);
			}

			if (field == null) {
				log.warn("Field Not Found :" + currentKey + " !");
				continue;
			}

			String fieldName = field.getName();
			KeyPair joinColumnResult = QueryUtil.checkIfJoinColumn(currentKey, field, false);

			if (null != joinColumnResult) {
				if (joinColumnResult.isValid()) {

					setCurrentAlias(fieldName);
					criteria.createAlias(entityName + "." + fieldName, fieldName);
					criteria.add(restrictionLike(fieldName + "." + joinColumnResult.getValue(), field.getType(),
							fieldsFilter.get(rawKey)));
				} else {
					continue;
				}
			} else {
				if (itemExacts) {
					criteria.add(restrictionEquals(entityClass, currentKey, fieldsFilter.get(rawKey)));
				} else {
					criteria.add(restrictionLike(entityName + "." + currentKey, entityClass, fieldsFilter.get(rawKey)));
				}
			}

		}

		try {
			addOrderOffsetLimit(criteria, filter);
		} catch (Exception e) {
			log.error("Error adding order/offset/limit");
			e.printStackTrace();
		}

		return criteria;

	}

	public Criteria createRowCountCriteria(Class<?> _class, final Filter rawFilter) {
		Filter filter = EntityUtil.cloneSerializable(rawFilter);

		filter.setLimit(0);
		filter.setPage(0);
		filter.setOrderBy(null);

		Criteria criteria = createCriteria(_class, filter, false);
		criteria.setProjection(Projections.rowCount());
		return criteria;
	}

	private void addOrderOffsetLimit(Criteria criteria, Filter filter) {
		if (filter.getLimit() > 0) {
			criteria.setMaxResults(filter.getLimit());
			if (filter.getPage() > 0) {
				criteria.setFirstResult(filter.getPage() * filter.getLimit());
			}
		}

		if (null != filter.getOrderBy()) {
			Order order;

			if (filter.getOrderType().toLowerCase().equals("desc")) {
				order = Order.desc(filter.getOrderBy());
			} else {
				order = Order.asc(filter.getOrderBy());
			}

			criteria.addOrder(order);
		}

	}

	Criterion restrictionLike(final String fieldName, Class<?> _class, Object value) {
		String extractedFieldName = fieldName;
		if (fieldName.contains(".") && fieldName.split("\\.").length == 2) {
			extractedFieldName = fieldName.split("\\.")[1];
		}
		Field field = EntityUtil.getDeclaredField(_class, extractedFieldName);
		boolean stringTypeField = field.getType().equals(String.class);
		Object validatedValue = validateFieldValue(field, value);

		if (!stringTypeField) {

			return nonStringLikeExp(field, _class, validatedValue);
		}

		SimpleExpression likeExp = Restrictions.like(fieldName, String.valueOf(validatedValue), MatchMode.ANYWHERE);

		return likeExp;

	}

	private Criterion nonStringLikeExp(Field field, Class<?> _class, Object value) {

		String columnName = field.getName();// QueryUtil.getColumnName(field);
		String tableName = _class.getName();// QueryUtil.getTableName(_class); NOW USING ALIAS

		Criterion sqlRestriction = Restrictions
				.sqlRestriction(currentAlias + "." + columnName + " LIKE '%" + value + "%'");

		return sqlRestriction;
	}

	public static void main(String[] args) {
		String name = "kk.ll";
		log.info("contains: {}", name.contains("."));
		String str = "capitaltype"; // 10 digits
		System.out.println(str.substring(0, 10));
		System.out.println(str.replace("e", "."));

//		Map<String, Object> filter = new HashMap<String, Object>() {
//			{
//				 
//				put("transactionDate-month", 2);
//			}
//		};
//		Criteria criteria = createWhereClause(Transaction.class, filter, false);
//		criteria.setMaxResults(2);
//		System.out.println("CRITERIA BUILT");
//		List list = criteria.list();
//		CollectionUtil.printArray(list.toArray());
//		System.out.println(criteria.getClass());
	}

	private Criterion getDateFilter(String rawKey, String key, List<Field> entityDeclaredFields,
			Map<String, Object> filter) {
		boolean dayFilter = rawKey.endsWith(QueryUtil.DAY_SUFFIX);
		boolean monthFilter = rawKey.endsWith(QueryUtil.MONTH_SUFFIX);
		boolean yearFilter = rawKey.endsWith(QueryUtil.YEAR_SUFFIX);

		if (dayFilter || monthFilter || yearFilter) {

			String fieldName = key;
			String mode = QueryUtil.FILTER_DATE_DAY;

			if (dayFilter) {
				fieldName = key.replace(QueryUtil.DAY_SUFFIX, "");
				mode = QueryUtil.FILTER_DATE_DAY;

			} else if (monthFilter) {
				fieldName = key.replace(QueryUtil.MONTH_SUFFIX, "");
				mode = QueryUtil.FILTER_DATE_MON1TH;

			} else if (yearFilter) {
				fieldName = key.replace(QueryUtil.YEAR_SUFFIX, "");
				mode = QueryUtil.FILTER_DATE_YEAR;

			}
			Field field = EntityUtil.getObjectFromListByFieldName("name", fieldName, entityDeclaredFields);
			Object value = filter.get(key);
			String columnName = QueryUtil.getColumnName(field);
			log.info("mode: {}. value: {}", mode, value);
			Criterion restriction = Restrictions.sqlRestriction(mode + "(" + columnName + ")=" + value);

			return restriction;
		}

		return null;
	}

	private String currentItemExact(String rawKey) {
		if (rawKey.endsWith("[EXACTS]")) {
			String finalKey = rawKey.split("\\[EXACTS\\]")[0];
			log.info("{} exact search", finalKey);
			return finalKey;
		}
		return null;
	}

}
