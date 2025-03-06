package com.decide.forms.exceptions;

import java.util.logging.Level;

public class DuplicatedItemException extends FormException {

	private static final long serialVersionUID = 1622606564786180422L;

	public DuplicatedItemException(String mensaje, Level severity, String clave) {
		super(mensaje,severity,clave);
	}

}
