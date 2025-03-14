package com.decide.forms.layout;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class QuestionOption {

	/*--------------------------------------------------------------*
	 * Bean
	 *--------------------------------------------------------------*/
    @JsonProperty("code")
    private String code;
    @JsonProperty("label")
    private String label;
    @JsonProperty("enabled")
    private Boolean enabled;
    @JsonProperty("visible")
    private Boolean visible;
    @JsonProperty("value")
    private Object value;

    @JsonProperty("value")
    public Object getValue() {
        return value;
    }
    @JsonProperty("value")
    public void setValue(Object value) {
        this.value = value;
    }

    @JsonProperty("code")
    public String getCode() {
        return code;
    }
    @JsonProperty("code")
    public void setCode(String codigo) {
        this.code = codigo.trim();
    }

    @JsonProperty("label")
    public String getLabel() {
        return label;
    }
    @JsonProperty("label")
    public void setLabel(String label) {
        this.label = label;
    }

    @JsonProperty("enabled")
    public Boolean getEnabled() {
        return enabled;
    }
    @JsonProperty("enabled")
    public void setEnabled(Boolean habilitada) {
        this.enabled = habilitada;
    }

    @JsonProperty("visible")
    public Boolean getVisible() {
        return visible;
    }
    @JsonProperty("visible")
    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    /*--------------------------------------------------------------*
	 * Contructors
	 *--------------------------------------------------------------*/
	public QuestionOption() {
		super();
		this.code = null;
		this.label = null;
		this.enabled = false;
		this.visible = false;
		this.value=null;
	}

    public QuestionOption(String code) {
		super();
		this.code = code.trim();
		this.label = null;
		this.enabled = true;
		this.visible = true;
		this.value=null;
	}

	public QuestionOption(String code, String label, Boolean enabled, Boolean visible) {
		super();
		this.code = code.trim();
		this.label = label;
		this.enabled = enabled;
		this.visible = visible;
		this.value=null;
	}

	public QuestionOption(String code, String label) {
		super();
		this.code = code.trim();
		this.label = label;
		this.enabled = true;
		this.visible = true;
		this.value=null;
	}

	public QuestionOption(QuestionOption qo) {
		super();
		this.code = qo.code.trim();
		this.label = qo.label;
		this.enabled = qo.enabled;
		this.visible = qo.visible;
		this.value = qo.value;
	}



}
