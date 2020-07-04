package com.fajar.entitymanagement.querybuilder;

import static com.fajar.entitymanagement.querybuilder.QueryUtil.SQL_KEYWORD_FROM;
import static com.fajar.entitymanagement.querybuilder.QueryUtil.SQL_KEYWORD_SELECT;
import static com.fajar.entitymanagement.querybuilder.QueryUtil.doubleQuoteMysql;
import static com.fajar.entitymanagement.util.StringUtil.buildString;

import java.io.Serializable;
import java.util.List;

import com.fajar.entitymanagement.dto.Filter;
import com.fajar.entitymanagement.entity.BaseEntity;
 
public abstract class QueryHolder implements Serializable{ 
	
	/**
	 * 
	 */
	protected static final long serialVersionUID = 772056003694375496L;
	protected static final String SQL_DOT_STAR = ".*";
	protected List<QueryFilterItem> queryFilters;
	protected final Class<? extends BaseEntity> entityClass;
	protected final String tableName;
	protected final Filter filter; 
	
	protected String whereClauseSql, joinSql; 
	
	protected int		offset;
	protected boolean withLimit;
	protected boolean withOrder ;
	protected boolean contains;
	protected boolean exacts;
	protected boolean withFilteredField;

	protected String orderType;
	protected String orderBy;
	
	public QueryHolder(Class<? extends BaseEntity> _entityClass, Filter filter) {
		this.entityClass = _entityClass;
		this.tableName = QueryUtil.getTableName(entityClass);
		this.filter = filter;
		init();
	}
	
	protected void init() {
		this.offset 			= filter.getPage() * filter.getLimit();
		this.withLimit 			= filter.getLimit() > 0;
		this.withOrder 			= filter.getOrderBy() != null && filter.getOrderType() != null
				&& !filter.getOrderBy().isEmpty() && !filter.getOrderType().isEmpty();
		this.contains 			= filter.isContains();
		this.exacts 			= filter.isExacts();
		this.withFilteredField 	= filter.getFieldsFilter() != null;

		this.orderType 			= filter.getOrderType();
		this.orderBy 			= filter.getOrderBy(); 
	}
	
	public abstract void buildSqlSelectAndSingleResult();
	
	public abstract String getSqlSelect();
	
	protected String selectFromTable() {

		return buildString(SQL_KEYWORD_SELECT, 
				doubleQuoteMysql(tableName), 
				SQL_DOT_STAR, 
				SQL_KEYWORD_FROM,
				doubleQuoteMysql(tableName));
	}

	public abstract String getSqlSingleResult();

}
