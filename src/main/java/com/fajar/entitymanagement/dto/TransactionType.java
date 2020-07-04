package com.fajar.entitymanagement.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TransactionType {

	IN, OUT;

	@JsonCreator
	public static TransactionType forValue(String value) {
		TransactionType[] enumKeys = TransactionType.values();
		for (int i = 0; i < enumKeys.length; i++) {
			if (enumKeys[i].toString().equals(value)) {
				return enumKeys[i];
			}
		}

		return null;
	}

	@JsonValue
	public String toValue() {
		TransactionType[] enumKeys = TransactionType.values();
		for (int i = 0; i < enumKeys.length; i++) {
			if (enumKeys[i].equals(this)) {
				return enumKeys[i].toString();
			}
		}

		return null; // or fail
	}
}
