package com.decide.forms.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BusinessData {
	
	@JsonProperty("code")
	private String code;
	@JsonProperty("value")
	private Object value;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	
	public BusinessData(String code, Object value) {
		this.code = code;
		this.value = value;
	}
	public BusinessData() {

	}
	
	
}
