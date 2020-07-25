package com.fajar.entitymanagement.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fajar.entitymanagement.annotation.Dto;
import com.fajar.entitymanagement.annotation.FormField;
import com.fajar.entitymanagement.dto.FieldType;
import com.fajar.entitymanagement.service.entity.EntityUpdateInterceptor;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
	@Column(unique = true)
	private String code;
	@FormField
	@Column(unique = true)
	private String name;
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
	@Column
	private String description;
	@FormField
	@Column(unique = true)
	private String url;

	@JoinColumn(name = "page_id", nullable = false)
	@ManyToOne
	@FormField(lableName = "Page", type = FieldType.FIELD_TYPE_FIXED_LIST, optionItemName = "name")
	private Page menuPage;
	@FormField(type = FieldType.FIELD_TYPE_IMAGE, required = false, defaultValue = "DefaultIcon.BMP")
	@Column(name = "icon_url")
	private String iconUrl;

	@Override
	@JsonIgnore
	public EntityUpdateInterceptor<Menu> getUpdateInterceptor() {

		return new EntityUpdateInterceptor<Menu>() { 
			private static final long serialVersionUID = -5435893352707283150L;

			@Override
			public Menu preUpdate(Menu menu) {
				System.out.println("****** EntityUpdateInterceptor for Menu *******");
				
				if (menu.getUrl().startsWith("/") == false) {
					menu.setUrl("/" + menu.getUrl());
				}
				return menu;

			}
		};
	}

}
