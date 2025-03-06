package com.decide.forms.util;

import java.util.List;
import java.util.Map;

public class ListUtil {
	
	private ListUtil() {}
	
	public static boolean isList(Object obj) {
		boolean isList = false;
		if (obj instanceof List) isList = true;
		return isList;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Object> objectToList(Object obj){
		List<Object> listObject = null;
		if(isList(obj)) {
			listObject = (List<Object>) obj;
		}
		return listObject;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> objectToListMapStringObject(Object obj){
		List<Map<String, Object>> listMap = null;
		List<Object> listMapObject = objectToList(obj);
		if (listMapObject!= null && !listMapObject.isEmpty() && listMapObject.get(0) instanceof Map) {
			listMap = (List<Map<String, Object>>) obj;
		}
		return listMap;
	}

}
