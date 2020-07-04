package com.fajar.entitymanagement.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fajar.entitymanagement.annotation.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Filter implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -5151185528546046666L;
	@Builder.Default
	private Integer limit = 0;
	@Builder.Default
	private Integer page = 0;
	private String orderType;
	private String orderBy;
	@Builder.Default
	private boolean contains = true;
	@Builder.Default
	private boolean beginsWith = true;
	@Builder.Default
	private boolean exacts = false;
	@Builder.Default
	private Integer day = 0;
	@Builder.Default
	private Integer year = 0;
	@Builder.Default
	private Integer month = 0;
	@Builder.Default
	private String module = "IN";
	@Builder.Default
	private Map<String, Object> fieldsFilter = new HashMap<>();
	
	private Integer monthTo;
	private Integer yearTo; 

}
