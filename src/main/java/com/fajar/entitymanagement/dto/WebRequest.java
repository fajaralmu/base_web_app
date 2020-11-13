package com.fajar.entitymanagement.dto;

import java.io.Serializable;
import java.util.List;

import com.fajar.entitymanagement.annotation.Dto;
import com.fajar.entitymanagement.entity.BaseEntity;
import com.fajar.entitymanagement.entity.Category;
import com.fajar.entitymanagement.entity.Customer;
import com.fajar.entitymanagement.entity.Menu;
import com.fajar.entitymanagement.entity.Page;
import com.fajar.entitymanagement.entity.Product;
import com.fajar.entitymanagement.entity.Profile;
import com.fajar.entitymanagement.entity.RegisteredRequest;
import com.fajar.entitymanagement.entity.Unit;
import com.fajar.entitymanagement.entity.User;
import com.fajar.entitymanagement.entity.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 110411933791444017L;


	
	/**
	 * ENTITY CRUD use lowerCase!!!
	 */

	private String entity;
	private User user; 
	private Customer customer;
	private Profile profile;
	private Menu menu;
	private Unit unit;
	private Product product;
	private UserRole userrole;
	private Category category;
	private RegisteredRequest registeredrequest;  
	private Page page;

	/**
	 * ==========end entity============
	 */

	private Filter filter;
	
	private BaseEntity entityObject;
	
	private String destination;
	private String username;
	private String value;
	
	private List<BaseEntity> orderedEntities;
	
	private boolean regularTransaction;
	
	private String imageData;
	private String partnerId;
	private String originId;

}
