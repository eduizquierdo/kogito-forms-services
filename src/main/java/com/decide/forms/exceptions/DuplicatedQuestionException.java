package com.decide.forms.exceptions;

import java.util.logging.Level;

public class DuplicatedQuestionException extends FormException {

	private static final long serialVersionUID = -7710426809432573175L;
	public DuplicatedQuestionException(String mensaje, Level severity, String clave) {
		super("DuplicatedQuestionException: "+mensaje,severity,clave);
	}

}
