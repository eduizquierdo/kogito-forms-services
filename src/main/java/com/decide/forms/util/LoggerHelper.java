package com.decide.forms.util;

import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerHelper {
	/*--------------------------------------------------------------*
	 * Logging (do not verbalize in BOM)
	 *--------------------------------------------------------------*/    
	public static final Logger LOGGER = Logger.getLogger("com.decide.forms.FormulariosOpen");	

	private LoggerHelper() {}

	/*--------------------------------------------------------------*
	 * Application Logging
	 *--------------------------------------------------------------*/    
	
	public static void log(Level level, String message) {
		LOGGER.log(level, message);
	}
	public static void log(Level level, Supplier<String> logMessageSupplier) {
		if(LOGGER.isLoggable(level)) {
			String message = logMessageSupplier.get();
			LOGGER.log(level,message);
		}
	}
	/*--------------------------------------------------------------*
	 * Logging in the Ruleset (to be used in b2x)
	 *--------------------------------------------------------------*/    
	public static void logINFO(String message) {
		if(message!=null && !message.isEmpty()) {
			log(Level.INFO,message);
		}
	}
}
