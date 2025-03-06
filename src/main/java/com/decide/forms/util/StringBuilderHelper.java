package com.decide.forms.util;

public class StringBuilderHelper {
	
	private StringBuilderHelper() {}

	public static void addIfNotNull(StringBuilder buffer, String text) {
		if(text != null) buffer.append(text);
	}
	public static void add(StringBuilder buffer, String separator ) {
		buffer.append(separator);
	}
	public static void addIfNotNullAndSeparator(StringBuilder buffer, String text, String separator ) {
		if(text != null) {
			buffer.append(text);
			buffer.append(separator);
		}	
	}

}
