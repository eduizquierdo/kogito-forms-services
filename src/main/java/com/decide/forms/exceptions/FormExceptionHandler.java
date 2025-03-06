package com.decide.forms.exceptions;

import java.util.logging.Level;


import com.decide.forms.model.ExecutionStatus;
import com.decide.forms.util.LoggerHelper;

public class FormExceptionHandler  {

	private String formCode = null;
	
	public FormExceptionHandler() {
		super();
	}
	
	public void handleActionException(Exception e) throws Exception {

		if(e instanceof FormException ) {
			FormException fe = (FormException)e;
			LoggerHelper.log(fe.getLevel(), 
					         String.format("%s in form %s: %s", 
					                       e.getClass().getSimpleName(), 
					                       (this.formCode!=null) ? this.formCode: "??",
										   e.getMessage()));
			ExecutionStatus status = ExecutionStatus.getExecutionStatus();
			if(status!=null) {
				status.addExecutionMessage(fe.getLevel(), e.getMessage(), fe.getClave());
			}
		} else {
			// el resto de excepciones no se manajan
			LoggerHelper.log(Level.SEVERE, "Error executing rules: "+e.getMessage());
			throw e;
	    }
	}

	public void handleConditionException(Exception e) throws Exception {
		if(e instanceof FormException ) {
			FormException fe = (FormException)e;			
			LoggerHelper.log(fe.getLevel(), 
			         String.format("%s en formulario %s: %s", 
			                       e.getClass().getSimpleName(), 
			                       (this.formCode!=null) ? this.formCode: "??", 
			                        e.getMessage()));
			ExecutionStatus status = ExecutionStatus.getExecutionStatus();
			if(status!=null) {
				status.addExecutionMessage(fe.getLevel(), e.getMessage(), fe.getClave());
			}
			
		} else {
			// el resto de excepciones no se manejan
			LoggerHelper.log(Level.SEVERE, "Error executing rules: "+e.getMessage());
			throw e;
	    }

	}

}
