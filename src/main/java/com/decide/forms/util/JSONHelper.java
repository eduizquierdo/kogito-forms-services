package com.decide.forms.util;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;

import com.decide.forms.commands.FormCommand;
import com.decide.forms.layout.FormLayout;
import com.decide.forms.model.ExecutionStatus;
import com.decide.forms.model.Form;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONHelper {
	
	private JSONHelper() {}

	/*--------------------------------------------------------------*
	 * Java 2 JSON Facilities for Form Data
	 *--------------------------------------------------------------*/    
	
	public static String convertFormCommandsToJSONString(FormCommand[] commands) {
		ObjectMapper mapper = createMapper();
    	try {
    		return mapper.writeValueAsString(commands);
		} catch (Exception e) {
			LoggerHelper.log(Level.SEVERE, "Exception while converting Form Commands to JSON String: " + e.getMessage());
	    	return null;
		} 
	}

	public static String convertExecutionStatusToJSONString(ExecutionStatus status) {
		ObjectMapper mapper = createMapper();
    	try {
    		return mapper.writeValueAsString(status);
		} catch (Exception e) {
			LoggerHelper.log(Level.SEVERE, "Exception while converting Execution Status to JSON String: " + e.getMessage());
	    	return null;
		} 
	}

	public static String convertFormToJSONString(Form form) {
		ObjectMapper mapper = createMapper();
    	try {
    		return mapper.writeValueAsString(form);
		} catch (Exception e) {
			LoggerHelper.log(Level.SEVERE, "Exception while converting Form to JSON String: " + e.getMessage());
	    	return null;
		} 
	}

	public static String convertFormLayoutToJSONString(FormLayout formLayout) {
		ObjectMapper mapper = createMapper();
    	try {
    		return mapper.writeValueAsString(formLayout);
		} catch (Exception e) {
			LoggerHelper.log(Level.SEVERE, "Exception while converting Form Layout to JSON String: " + e.getMessage());
	    	return null;
		} 
	}

	public static String convertFormDataToJSONString(Map<String,Object> formData) {
		ObjectMapper mapper = createMapper();
    	try {
    		return mapper.writeValueAsString(formData);
		} catch (Exception e) {
			LoggerHelper.log(Level.SEVERE, "Exception while converting Form Data to JSON String: " + e.getMessage());
	    	return null;
		} 
	}

	public static FormLayout loadFormLayoutFromJSONString(String json) {
    	ObjectMapper mapper = createMapper();
    	try {
    		return mapper.readValue(json, FormLayout.class);
    	} catch (Exception e) {
			LoggerHelper.log(Level.SEVERE, "Cannot load Form Layout from JSON string: "+e.getMessage());
		} 
    	return null;
	}

	public static FormLayout loadFormLayoutFromJSONFile(String fileName) {
    	try {
        	String json = new String(Files.readAllBytes(Paths.get(fileName)));
        	return loadFormLayoutFromJSONString(json);
    	} catch (Exception e) {
			LoggerHelper.log(Level.SEVERE,"Cannot read file "+fileName+". "+e.getMessage());
		} 
    	return null;
	}

	public static Map<String,Object> loadFormDataFromJSONFile(String fileName) {
    	try {
        	String json = new String(Files.readAllBytes(Paths.get(fileName)));
        	return loadFormDataFromJSONString(json);
    	} catch (Exception e) {
			LoggerHelper.log(Level.SEVERE,"Cannot read file "+fileName+". "+e.getMessage());
		} 
    	return null;
	}
	
	//SONAR: Return an empty map instead of null.
	@SuppressWarnings("unchecked")
	public static Map<String,Object> loadFormDataFromJSONString(String json) {
    	ObjectMapper mapper = createMapper();
    	try {
    		return mapper.readValue(json, Map.class);
    	} catch (Exception e) {
			LoggerHelper.log(Level.SEVERE,"Cannot load Form Data from JSON string: "+e.getMessage());
		} 
    	return null;
	}
	
	private static ObjectMapper createMapper() {
		ObjectMapper mapper = new ObjectMapper();
    	mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
    	return mapper;
	}

	public static FormLayout leerFormularioJSONDeString(String json) {
    	ObjectMapper mapper = createMapper();
    	try {
    		return mapper.readValue(json, FormLayout.class);
		} catch (Exception e) {
			LoggerHelper.log(Level.SEVERE,"No se puede leer el JSON del formulario: "+e.getMessage());
		} 
    	 
    	return null;
    }




}


