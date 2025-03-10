package com.decide.forms.exceptions;

import java.util.logging.Level;

public class FormException extends Exception {
	
	private static final long serialVersionUID = -5021692597219576912L;

	private final Level level;
	public Level getLevel() {
		return level;
	}
	
	private final String clave;
	public String getClave() {
		return clave;
	}
	
	public FormException(String mensaje, Level level, String clave) {
		super("[****]"+level+"[****]"+mensaje);
		this.level=level;
		this.clave=clave;
	}

	
}
