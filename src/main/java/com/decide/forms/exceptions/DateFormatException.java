package com.decide.forms.exceptions;

import java.util.logging.Level;

public class DateFormatException extends FormException {

	private static final long serialVersionUID = 7058026259909787616L;

	public DateFormatException(String mensaje, Level severity, String clave) {
		super("DateFormaException:"+mensaje,severity,clave);
	}


}
