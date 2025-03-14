package com.decide.forms.layout;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Button {

	/*--------------------------------------------------------------*
	 * Bean
	 *--------------------------------------------------------------*/    
	@JsonProperty("code")
	private String code;
	@JsonProperty("label")
	private String label;
	@JsonProperty("action")
	private String action;
	@JsonProperty("callback")
	private String callback;
    @JsonProperty("enabled")
    private Boolean enabled;
    @JsonProperty("style")
    private String style;
    @JsonProperty("visible")
    private Boolean visible;
    @JsonProperty("visible")
	public Boolean getVisible() {
		return visible;
	}
    @JsonProperty("visible")
	public void setVisible(Boolean visible) {
		this.visible = visible;
	}
	@JsonProperty("code")
	public String getCode() {
		return code;
	}
	@JsonProperty("code")
	public void setCode(String code) {
		this.code = code.trim();
	}
	
	@JsonProperty("label")
	public String getLabel() {
		return label;
	}
	@JsonProperty("label")
	public void setLabel(String label) {
		this.label = label;
	}
	
	@JsonProperty("action")
	public String getAction() {
		return action;
	}
	@JsonProperty("action")
	public void setAction(String action) {
		this.action = action;
	}
	
	@JsonProperty("callback")
	public String getCallback() {
		return callback;
	}
	@JsonProperty("callback")
	public void setCallback(String callback) {
		this.callback = callback;
	}

    @JsonProperty("enabled")
    public Boolean getEnabled() {
        return enabled;
    }
    @JsonProperty("enabled")
    public void setEnabled(Boolean habilitada) {
        this.enabled = habilitada;
    }
    @JsonProperty("style")
	public String getStyle() {
		return style;
	}
    @JsonProperty("style")
	public void setStyle(String style) {
		this.style = style;
	}
	/*--------------------------------------------------------------*
	 * Constructors
	 *--------------------------------------------------------------*/    
	public Button() {
		super();
		this.code=null;
		this.label = null;
		this.action = null;
		this.callback= null;
		this.enabled=true;
		this.visible=true;
	}
	public Button(String code) {
		super();
		this.code=code.trim();
		this.label = null;
		this.action = null;
		this.callback= null;
		this.enabled=true;
		this.visible=true;
	}
	public Button(String code, String label, String action) {
		super();
		this.code=code.trim();
		this.label = label;
		this.action = action;
		this.callback= null;
		this.enabled=true;
		this.visible=true;
	}
	public Button(Button button) {
		super();
		this.code=button.code.trim();
		this.label = button.label;
		this.action = button.action;
		this.callback= button.callback;
		this.enabled=button.enabled;
		this.style=button.style;
		this.visible=button.visible;
	}
	
}
