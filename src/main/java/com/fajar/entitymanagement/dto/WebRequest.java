package com.fajar.entitymanagement.dto;

import java.io.Serializable;
import java.util.List;

import com.fajar.entitymanagement.annotation.Dto;
import com.fajar.entitymanagement.entity.BaseEntity;
import com.fajar.entitymanagement.entity.Customer;
import com.fajar.entitymanagement.entity.Menu;
import com.fajar.entitymanagement.entity.Page;
import com.fajar.entitymanagement.entity.Product;
import com.fajar.entitymanagement.entity.ProductCategory;
import com.fajar.entitymanagement.entity.ProductUnit;
import com.fajar.entitymanagement.entity.Profile;
import com.fajar.entitymanagement.entity.RegisteredRequest;
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
	private  Profile profile;
	private Menu menu;
	private ProductUnit productunit;
	private Product product;
	private UserRole userrole;
	private ProductCategory productcategory;
	private RegisteredRequest registeredRequest;  
	private Page page;

	/**
	 * ==========end entity============
	 */

	private Filter filter;  
	
	private BaseEntity entityObject;
	
	private String destination;
	private String username;
	private String value;
	
	private List<Page> pages; 
	private String imageData;
	private String partnerId;
	private String originId;

}
