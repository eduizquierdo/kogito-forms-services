package com.decide.forms.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.decide.forms.util.LoggerHelper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class ExecutionStatus {

	
	/*--------------------------------------------------------------------------*
	 * Execution status management
	 * - initializeExecutionStatus() should be executed before executing rules. 
	 *   Ruleset out param of type ExecutionStatus should be provided as input.
	 * - should be executed after rule execution finish 
	 *-------------------------------------------------------------------------*/
	private static final ThreadLocal<ExecutionStatus> EXECUTION_STATUS = new ThreadLocal<>();
	
	// this method is verbalized in BOM and should be called at the beginning of main ruleflow
	public static void initializeExecutionStatus(ExecutionStatus status) {
		LoggerHelper.log(Level.INFO,  "initializing execution status ...");		
		if(status==null) {
			LoggerHelper.log(Level.WARNING, "execution status out param not provided. No execution messages will be returned in the output.");
		} else {
			EXECUTION_STATUS.set(status);
		}
	}
	
	// this method is verbalized in BOM and should be called at the end of main ruleflow
	public static void finalizeExecutionStatus() {
		ExecutionStatus status = EXECUTION_STATUS.get();		
		LoggerHelper.log(Level.INFO,  "finalizing execution status with state..."+((status==null) ? "UNKNOWN" : status.getStatus()));		
		EXECUTION_STATUS.remove();
	}

	public static ExecutionStatus getExecutionStatus() {
		return EXECUTION_STATUS.get();
	}
	
	/*--------------------------------------------------------------*
	 * Static data
	 *--------------------------------------------------------------*/
	public static final String STATUS_SUCCESS = "SUCCESS";
	public static final String STATUS_WARNING = "WARNING";
	public static final String STATUS_ERROR = "ERROR";

	/*--------------------------------------------------------------*
	 * Bean
	 *--------------------------------------------------------------*/
	@JsonProperty("status")
	private String status;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	//SONAR: Return an empty collection instead of null.
	@JsonProperty("messages")
	public List<ExecutionMessage> getMessages() {
		if(this.messagesMap!=null && !this.messagesMap.isEmpty()) {
			return new ArrayList<>(this.messagesMap.values());
		} else 
			return null;
	}

	/*--------------------------------------------------------------*
	 * Private Map to de-duplicate error messages
	 *--------------------------------------------------------------*/
	@JsonIgnore
	private Map<String,ExecutionMessage> messagesMap;
	public Map<String, ExecutionMessage> getMessagesMap() {
		return messagesMap;
	}
	
	/*--------------------------------------------------------------*
	 * Constructor
	 *--------------------------------------------------------------*/
	public ExecutionStatus() {
		super();
		this.status = STATUS_SUCCESS;
		this.messagesMap = new HashMap<>();
	}

	/*--------------------------------------------------------------*
	 * Utilities
	 *--------------------------------------------------------------*/
	public void addExecutionMessage(Level level, String message, String key) {
		String severity = converLeveltoSeverity(level);
		this.messagesMap.put(key, new ExecutionMessage(severity,message));
		this.status=getMaxSeverity(this.status, severity);
	}
	
	private String getMaxSeverity(String currentSeverity, String newSeverity) {
		if(currentSeverity.equals(STATUS_ERROR)) {
			return currentSeverity;
		} else if(currentSeverity.equals(STATUS_WARNING) && newSeverity.equals(STATUS_ERROR)) {
			return newSeverity;
		} else if(currentSeverity.equals(STATUS_WARNING)) {
			return currentSeverity;
		} else {
			return newSeverity;
		}
	}

	private String converLeveltoSeverity(Level level) {
		if(level==Level.SEVERE) return STATUS_ERROR;
		else if(level==Level.WARNING) return STATUS_WARNING;
		else return STATUS_SUCCESS;
	}
}


	
