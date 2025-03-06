package com.decide.forms.util;

public class StringHelper {
	
	private StringHelper() {}
	
	public static String generateStringFormatPattern(Integer num) {
		StringBuilder sb = new StringBuilder();
		if(num != null && num > 0) {
			for(int i = 0; i < num; i++) {
				sb.append("%s");
			}
		}
		return sb.toString();
	}
	
	public static boolean isNotNull(String obj) {
		return obj != null;
	}
	
	public static boolean isNull(String obj) {
		return obj == null;
	}
	
	public static boolean isNotNullAndNotEmpty(String obj) {
		return isNotNull(obj) && !obj.isEmpty();
	}
	
	public static boolean emptyOrNull(String str) {
		return str==null || str.isEmpty();
	}
}
