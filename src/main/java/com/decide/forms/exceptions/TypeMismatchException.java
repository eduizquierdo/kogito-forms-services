package com.decide.forms.exceptions;

import java.util.logging.Level;

public class TypeMismatchException extends FormException {

	private static final long serialVersionUID = 7058026259909787616L;

	public TypeMismatchException(String mensaje, Level severity, String clave) {
		super("TypeMismatchException: "+mensaje,severity,clave);
	}


}
