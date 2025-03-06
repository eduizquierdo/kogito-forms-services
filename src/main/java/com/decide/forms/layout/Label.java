package com.decide.forms.layout;

public class Label {

	/*--------------------------------------------------------------*
	 * Bean
	 *--------------------------------------------------------------*/
    private String locale;
    private String text;

    public String getLocale() {
        return locale;
    }
    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

	/*--------------------------------------------------------------*
	 * Constructors
	 *--------------------------------------------------------------*/    
	public Label() {
		super();
	}
	public Label(String locale, String text) {
		super();
		this.locale = locale;
		this.text = text;
	}

}
