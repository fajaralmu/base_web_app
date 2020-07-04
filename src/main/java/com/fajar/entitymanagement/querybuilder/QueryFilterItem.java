package com.fajar.entitymanagement.querybuilder;

import java.io.Serializable;

import com.fajar.entitymanagement.util.StringUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class QueryFilterItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7410226524071983871L;
	private Object value;
	private boolean exacts;
	private String tableName;
	private String columnName;
	private String dateMode;

	private String getDoubledQuotedColumn() {
		String fullColumnName = "";
		boolean tableNameExist = tableName != null && !tableName.isEmpty();
		
		if (tableNameExist) {

			fullColumnName = StringUtil.buildTableColumnDoubleQuoted(tableName, columnName);
		} else {

			fullColumnName = StringUtil.doubleQuoteMysql(columnName);
		}
		if (dateMode != null && !dateMode.isEmpty()) {
			fullColumnName = dateMode + "(" + fullColumnName + ")";
		}

		return fullColumnName;
	}

	public String generateSqlString() {
		String key = getDoubledQuotedColumn();
		StringBuilder sqlItem = new StringBuilder(key);
		
		if (isExacts()) {
			sqlItem = sqlItem.append(" = '").append(value).append("' ");
		} else {
			sqlItem = sqlItem.append(" LIKE '%").append(value).append("%' ");

		}

		log.info("Generated sql item: {}", sqlItem);

		return sqlItem.toString();
	}
}