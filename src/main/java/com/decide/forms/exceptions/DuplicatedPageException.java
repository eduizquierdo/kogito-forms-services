package com.decide.forms.exceptions;

import java.util.logging.Level;

public class DuplicatedPageException extends FormException {

	private static final long serialVersionUID = 3691729746796444874L;
	public DuplicatedPageException(String mensaje, Level severity, String clave) {
		super("DuplicatedPageException: "+mensaje,severity,clave);
	}


}
