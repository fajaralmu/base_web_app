package com.fajar.entitymanagement.dto;

import java.io.Serializable;
import java.util.Map.Entry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

 
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KeyValue<K, V> implements Serializable, Entry<K, V>{/**
	 * 
	 */
	private static final long serialVersionUID = -1668484384625090190L;

	private K key;
	private V value;
	@Builder.Default
	private boolean valid = true;
	
	@Override
	public K getKey() {
		return key;
	}
	@Override
	public V getValue() {
		return value;
	}
	@Override
	public V setValue(V value) {
		this.value = value;
		return value;
	}
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	public void setKey(K key) {
		this.key = key;
	}
	
	
	
}

