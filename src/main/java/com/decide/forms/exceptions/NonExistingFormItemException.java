package com.decide.forms.exceptions;

import java.util.logging.Level;

public class NonExistingFormItemException extends FormException {

	private static final long serialVersionUID = -200305826858233755L;
	public NonExistingFormItemException(String mensaje, Level severity, String clave) {
		super(mensaje,severity,clave);
	}


}
