package com.fajar.entitymanagement.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fajar.entitymanagement.annotation.Dto;
import com.fajar.entitymanagement.annotation.FormField;
import com.fajar.entitymanagement.dto.FieldType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto(commonManagementPage = false, updateService = "userUpdateService")
@Entity
@Table(name = "user")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity   {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3896877759244837620L;
	@Column(unique = true)
	@FormField
	private String username;
	@Column(name = "display_name")
	@FormField
	private String displayName;
	@Column
	@FormField
//	@JsonIgnore
	private String password;
	@JoinColumn(name = "role_id")
	@ManyToOne
	@FormField(type = FieldType.FIELD_TYPE_FIXED_LIST, optionItemName = "name")
	private UserRole role;
	@FormField(type = FieldType.FIELD_TYPE_IMAGE, required = false, defaultValue = "DefaultIcon.BMP")
	@Column(name = "profile_image")
	private String profileImage;

	@javax.persistence.Transient
	@JsonIgnore
	private String loginKey;
	@Transient
	@JsonIgnore
	private String requestId;
	@Transient
	@JsonIgnore
	@Builder.Default
	private boolean enabled = true;
	@Transient
	@JsonIgnore
	@Builder.Default
	private boolean credentialsNonExpired = true;
	@Transient
	@JsonIgnore
	@Builder.Default
	private boolean accountNonLocked = true;
	@Transient
	@JsonIgnore
	@Builder.Default
	private boolean accountNonExpired = true; 
	
	@Transient
	@JsonIgnore
	private Date processingDate; //for transaction
	
	public void setLoginKeyAndPasswordNull(String loginKey) {
		this.loginKey = loginKey;
		this.password = null;
	}

//	@Override
//	@JsonIgnore
//	public Collection<? extends GrantedAuthority> getAuthorities() {
//		 
//		return new ArrayList<>();
//	}

//	@JsonIgnore
//	@Override
//	public boolean isAccountNonExpired() {
//		 
//		return true;
//	}

//	@JsonIgnore
//	@Override
//	public boolean isAccountNonLocked() {
//		 
//		return true;
//	}

//	@JsonIgnore
//	@Override
//	public boolean isCredentialsNonExpired() {
//		 
//		return true;
//	}

	 

}
