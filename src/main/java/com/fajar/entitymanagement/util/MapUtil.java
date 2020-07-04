package com.fajar.entitymanagement.util;

import java.util.HashMap;
import java.util.Map;

public class MapUtil {

	public static boolean objectEquals(Object object, Object... objects) {

		for (Object object2 : objects) {
			if (object.equals(object2)) {
				return true;
			}
		}

		return false;
	}

	public static <K, V> Map<K, V> singleMap(K key, V value) {

		return new HashMap<K, V>() {
			private static final long serialVersionUID = 1150764585262310376L;
			{
				put(key, value);
			}

		};
	}
}
