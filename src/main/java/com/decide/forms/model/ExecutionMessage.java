package com.decide.forms.model;

import java.util.logging.Level;

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

	/*--------------------------------------------------------------*
	 * Utilities
	 *--------------------------------------------------------------*/
	public static final String FORM_MESSAGE_DELIMITER = "[****]";
	public static ExecutionMessage createFromExceptionMessage(String message) {
		if(message==null) return null;

		ExecutionMessage result = null;
		int from  = message.indexOf(FORM_MESSAGE_DELIMITER);
        int to = -1;
        if(from!=-1) {
            from=from+FORM_MESSAGE_DELIMITER.length();
            to = message.indexOf(FORM_MESSAGE_DELIMITER,from);
            if(to!=-1) {
					result = new ExecutionMessage(message.substring(from,to),
					                              message.substring(to+FORM_MESSAGE_DELIMITER.length()));
				}

        }
		return result;
	}
}


	
