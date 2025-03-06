package com.decide.forms.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class ExecutionMessage {
	/*--------------------------------------------------------------*
	 * Bean
	 *--------------------------------------------------------------*/
	@JsonProperty("severity")
	private String severity;
	public String getSeverity() {
		return severity;
	}
	public void setSeverity(String severity) {
		this.severity = severity;
	}

	@JsonProperty("message")
	private String message;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	/*--------------------------------------------------------------*
	 * Constructors
	 *--------------------------------------------------------------*/
	public ExecutionMessage() {
		super();
		this.severity = null;
		this.message = null;
	}
	public ExecutionMessage(String severity, String message) {
		super();
		this.severity = severity;
		this.message = message;
	}
}


	
