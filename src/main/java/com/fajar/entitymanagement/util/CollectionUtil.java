package com.fajar.entitymanagement.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fajar.entitymanagement.dto.KeyPair;

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

	public static <T> String printArray(T[] array) {
		if (null == array) {
			return "";
		}

		String[] arrayString = toArrayOfString(array);
		String result =  String.join(", ", arrayString);
		log.info("Print Array: [{}]", result);
		return result;
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
				newList.add((T) object);
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

	public static List<KeyPair> yearArray(int min, int max){
		List<KeyPair> years = new ArrayList<>();
		for(int i = min; i <= max; i++) {
			years.add(new KeyPair(i, i, true));
		}
		return years;
	}
	
	public static <T> boolean emptyArray(T[] array) {
		if(null == array) return true;
		if(0 == array.length) return true;
		
		return false;
	}
}
