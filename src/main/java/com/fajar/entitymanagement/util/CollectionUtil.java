package com.fajar.entitymanagement.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CollectionUtil {
	public static <T> List<T> arrayToList(T[] array) {
		List<T> list = new ArrayList<T>();
		for (T t : array) {
			list.add(t);
		}
		return list;

	}

	public static void main(String[] args) {

	}

	public static <K, T> List<T> mapToList(Map<K, T> map) {
		List<T> list = new ArrayList<T>();
		for (K key : map.keySet()) {
			list.add(map.get(key));
		}

		return list;
	}

	public static <K, T> List<T> mapOfListToList(Map<K, List<T>> map) {
		List<T> list = new ArrayList<T>();
		for (K key : map.keySet()) {
			List<T> mapValue = map.get(key);
			if (null == mapValue)
				continue;
			
			list.addAll(mapValue);
		}

		return list;
	}
	 

	public static <T> void printArray(T[] array) {
		if (null == array) {
			return;
		}

		String[] arrayString = toArrayOfString(array);
		log.info("Print Array: [{}]", String.join(", ", arrayString));

	}

	public static <T> List<T> listOf(T o) {

		List<T> list = new ArrayList<T>();
		list.add(o);
		return list;
	}

	public static <T> List<T> convertList(List<?> list) {
		List<T> newList = new ArrayList<T>();
		for (Object object : list) {
			try {
				newList.add(EntityUtil.castObject(object));
			} catch (Exception e) {

			}
		}
		return newList;
	}

	public static String[] toArrayOfString(List<?> validUrls) {
		if (validUrls == null) {
			return new String[] {};
		}
		String[] array = new String[validUrls.size()];
		for (int i = 0; i < validUrls.size(); i++) {
			array[i] = validUrls.get(i).toString();
		}
		return array;
	}

	public static <T> String[] toArrayOfString(T[] arrays) {
		if (arrays == null) {
			return new String[] {};
		}
		String[] array = new String[arrays.length];
		for (int i = 0; i < arrays.length; i++) {
			if (null == arrays[i]) {
				continue;
			}
			array[i] = arrays[i].toString();
		}
		return array;
	}

	public static <T> boolean emptyArray(T[] arr) {
		return arr == null || arr.length == 0;
	}

	public static <T> Object[] toObjectArray(T[] rawArray) {
		 
		Object[]  resultArray = new Object[rawArray.length];
		
		for (int i = 0; i < rawArray.length; i++) {
			resultArray[i] = rawArray[i];
		}
		return resultArray;
	}

	public static Object[] objectElementsToArray(final String fieldName, Object...array) {
		try {
			Object sampleObject = array[0];
			Object[] result = new Object[array.length];
			Field field = EntityUtil.getDeclaredField(sampleObject.getClass(), fieldName);
			for (int i = 0; i < array.length; i++) {
				Object object = array[i];
				Object fieldValue = field.get(object);
				result[i] = fieldValue;
			}
			
			return result;
		}catch (Exception e) {
			return new Object[] {"EMPTY"};
		}
	}

}
