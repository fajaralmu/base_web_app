package com.fajar.entitymanagement.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fajar.entitymanagement.annotation.Dto;
import com.fajar.entitymanagement.annotation.FormField;
import com.fajar.entitymanagement.dto.FieldType;
import com.fajar.entitymanagement.dto.FormInputColumn;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Dto(formInputColumn = FormInputColumn.ONE_COLUMN, ignoreBaseField = false)
@Entity
@Table(name="page")
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Page extends BaseEntity  {/**
	 * 
	 */
	private static final long serialVersionUID = -4180675906997901285L;

	@FormField
	@Column(unique = true)
	private String code;
	@FormField
	@Column(unique = true)
	private String name;
	@FormField(lableName = "Authorized (yes:1 or no:0)",type = FieldType.FIELD_TYPE_PLAIN_LIST, availableValues = {"0", "1"})
	@Column(nullable = false)
	private int authorized; 
	@FormField(lableName = "Is Non-Menu Page (yes:1 or no:0)",type = FieldType.FIELD_TYPE_PLAIN_LIST, availableValues = {"0", "1"})
	@Column(name = "is_non_menu_page")
	private int nonMenuPage;
	@FormField(lableName = "Link for non menu page")
	@Column(unique = true)
	private String link;
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
	@Column
	private String description;
	@FormField(type = FieldType.FIELD_TYPE_IMAGE,  required = false, defaultValue = "DefaultIcon.BMP")
	@Column(name= "image_url")
	private String imageUrl;
	@Column()
	@FormField(type=FieldType.FIELD_TYPE_NUMBER,lableName = "Urutan Ke")
	private int sequence;
	

	
	@Transient
	private List<Menu> menus;
	public String isMenuPage() {
		return nonMenuPage == 0 ? "true":"false"; 
	}
}
