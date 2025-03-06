package com.decide.forms.exceptions;

import java.util.logging.Level;

public class NonAnsweredQuestionException extends FormException {


	private static final long serialVersionUID = 4272735240409263504L;
	public NonAnsweredQuestionException(String mensaje, Level severity, String clave) {
		super(mensaje,severity,clave);
	}


}
