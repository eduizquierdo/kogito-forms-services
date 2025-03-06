package com.decide.forms.util;

import java.util.Map;

public class MapUtil {
	
	private MapUtil() {}

	public static boolean isMap(Object obj) {
		boolean isMap = false;
		if (obj instanceof Map<?, ?>) isMap = true;
		return isMap;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> toMapStringObject(Object obj){
		Map<String, Object> map = null;
		if(isMap(obj)) {
			map = (Map<String, Object>) obj;
		}
		return map;
	}
	
}
