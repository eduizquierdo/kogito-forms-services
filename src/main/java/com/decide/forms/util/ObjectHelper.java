package com.decide.forms.util;

public class ObjectHelper {

	private ObjectHelper() {}
	
	public static boolean isNotNull(Object obj) {
		return obj != null;
	}
	
	public static boolean isNull(Object obj) {
		return obj == null;
	}

}
