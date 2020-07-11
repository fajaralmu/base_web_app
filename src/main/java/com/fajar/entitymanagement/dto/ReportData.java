package com.fajar.entitymanagement.dto;

import java.io.Serializable;
import java.util.List;

import com.fajar.entitymanagement.entity.BaseEntity;
import com.fajar.entitymanagement.entity.setting.EntityProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6723760671439954923L;
	 
	private Filter filter;
	private List<BaseEntity> entities;
	private EntityProperty entityProperty;
	
	private String requestId;
	private String reportName;

}
