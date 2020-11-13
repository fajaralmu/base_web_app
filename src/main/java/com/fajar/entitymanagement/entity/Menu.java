package com.fajar.entitymanagement.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.apache.commons.lang3.StringEscapeUtils;

import com.fajar.entitymanagement.annotation.Dto;
import com.fajar.entitymanagement.annotation.FormField;
import com.fajar.entitymanagement.dto.FieldType;
import com.fajar.entitymanagement.service.entity.EntityUpdateInterceptor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Dto(ignoreBaseField = false)
@Entity
@Table(name = "menu")
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Menu extends BaseEntity {
	/**
	* 
	*/
	private static final long serialVersionUID = -6895624969478733293L;

	@FormField
	@Column
	private String code;
	@FormField
	@Column
	private String name;
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
	@Column
	private String description;
	@FormField
	@Column
	private String url;

	@FormField(lableName = "Path Variables Separated by ','", required = false)
	@Column
	private String pathVariables;
	// TODO: remove
//	@FormField
//	@Column
//	private String page;
	@JoinColumn(name = "page_id", nullable = false)
	@ManyToOne
	@FormField(lableName = "Parent Page", type = FieldType.FIELD_TYPE_FIXED_LIST, optionItemName = "name")
	private Page menuPage;
	@FormField(type = FieldType.FIELD_TYPE_IMAGE, required = false, defaultValue = "DefaultIcon.BMP")
	@Column(name = "icon_url")
	private String iconUrl;
	
	 
	public String pathVariablesString() {
		if(pathVariables == null || pathVariables.isEmpty()) {
			return "";
		}
		return pathVariables;
	}
	
	public static void main(String[] args) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		String JSON = objectMapper.writeValueAsString("FAJAR,ALMU".split(","));
		String JSON_2 = objectMapper.writeValueAsString(JSON);
		String escaped = (StringEscapeUtils.escapeHtml4(JSON_2));
		escaped = escaped.replaceAll("\\&quot;", "\\\\&quot;");
		System.out.println(escaped);
	}
	
	@PrePersist
	public void pre() {
		if(getColor() == null) {
			setColor("#ccc");
		}
		if(getFontColor() == null) {
			setFontColor("#000000");
		}
	}
	
	@Override
	public EntityUpdateInterceptor modelUpdateInterceptor() {

		return new EntityUpdateInterceptor<Menu>() {

			@Override
			public Menu preUpdate(Menu menu) {
				if (menu.getUrl().startsWith("/") == false) {
					menu.setUrl("/" + menu.getUrl());
				}
				return menu;

			}
		};
	}
}
