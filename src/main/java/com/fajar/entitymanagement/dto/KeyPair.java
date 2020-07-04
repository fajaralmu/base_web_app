package com.fajar.entitymanagement.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KeyPair<K, V> implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = -1668484384625090190L;

	private K key;
	private V value;
	@Builder.Default
	private boolean valid = true;
	
}

