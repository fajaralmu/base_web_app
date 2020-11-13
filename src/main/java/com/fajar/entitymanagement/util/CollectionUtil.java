package com.fajar.entitymanagement.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.fajar.entitymanagement.dto.KeyValue;
import com.fajar.entitymanagement.entity.BaseEntity;

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
	
	public static <T> List<KeyValue<T, T>> listToKeyVal(List<T> list){
		
		List<KeyValue<T, T>> result = new ArrayList<KeyValue<T,T>>();
		for (T el : list) {
			result.add((KeyValue<T, T>) KeyValue.builder().key(el).value(el).build());
		}
		return result ;
	}

	public static void main(String[] args) {

	}

	public static <K, T> List<T> mapToList(Map<K, T> map) {
		List<T> list = new ArrayList<T>();
		if(null != map)
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

	public static <T> ArrayList<T> convertList(List<?> list) {
		ArrayList<T> newList = new ArrayList<T>();
		if(null == list) {
			return newList;
		}
		for (Object object : list) {
			try {
				newList.add((T)(object));
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

	public static boolean isCollection(Object o) {
		return Collection.class.isAssignableFrom(o.getClass());
	}

	public static boolean isCollection(Class<?> o) {
		return Collection.class.isAssignableFrom(o);
	}
	
	public static Type[] getGenericTypes(Field field) {
		ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
		Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
		return actualTypeArguments;
	}
	
	public static Field getIDFieldOfUnderlyingListType(Field field) {
		 
		Type[] actualTypeArguments = getGenericTypes(field);
		Class<?> cls = (Class<?>) actualTypeArguments[0];
		return EntityUtil.getIdFieldOfAnObject(cls);
	}
	
	public static boolean isCollectionOfBaseEntity(Field field) {

		if (Collection.class.isAssignableFrom(field.getType())) {
			try {
				ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
				Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
				boolean result = BaseEntity.class.isAssignableFrom((Class<?>) actualTypeArguments[0]);

				return result;
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return false;

	}

	public static <T> boolean emptyArray(T[] arr) {
		return arr == null || arr.length == 0;
	}

	public static <T> Object[] toObjectArray(T[] rawArray) {

		Object[] resultArray = new Object[rawArray.length];

		for (int i = 0; i < rawArray.length; i++) {
			resultArray[i] = rawArray[i];
		}
		return resultArray;
	}

	public static Object[] objectElementsToArray(final String fieldName, Object... array) {
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
		} catch (Exception e) {
			return new Object[] { "EMPTY" };
		}
	}

	static final List empty = new ArrayList<>();
	
	public static <T> List<T> emptyList() {
		 
		return empty;
	}
	


	public static <T> List<T> reverse(List<T> arrayList) {
		List<T> reversedArrayList = new ArrayList<T>();
		for (int i = arrayList.size() - 1; i >= 0; i--) {

			// Append the elements in reverse order
			reversedArrayList.add(arrayList.get(i));
		}

		// Return the reversed arraylist
		return reversedArrayList;
	} 

	 

}
