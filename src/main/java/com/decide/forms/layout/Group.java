package com.decide.forms.layout;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.decide.forms.util.LoggerHelper;
import com.decide.forms.util.StringHelper;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class Group {
	/*--------------------------------------------------------------*
	 * Bean
	 *--------------------------------------------------------------*/    
	/*-----------------------*/
	@JsonProperty("code") 
    private String code;
    public String getCode() {
        return code;
    }
    public void setCode(String codigo) {
        this.code = codigo.trim();
    }
	/*-----------------------*/
    @JsonProperty("label") 
    private String label;
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
	/*-----------------------*/
    @JsonProperty("visible")
    private Boolean visible;
    public Boolean getVisible() {
        return visible;
    }
    public void setVisible(Boolean visible) {
        this.visible = visible;
    }
	/*-----------------------*/
    @JsonProperty("tooltip")
    private String tooltip;
    public String getTooltip() {
        return tooltip;
    }
    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }
	/*-----------------------*/
    @JsonProperty("url")
    private String url;
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
	/*-----------------------*/
    @JsonProperty("render")
    private GroupRenderType render;
    public GroupRenderType getRender() {
		return render;
	}
	public void setRender(GroupRenderType render) {
		this.render = render;
	}
	/*-----------------------*/
	@JsonProperty("minSize")
    private Integer minSize;
	public Integer getMinSize() {
		return minSize;
	}
	public void setMinSize(Integer minSize) {
		this.minSize = minSize;
	}
	/*-----------------------*/
	@JsonProperty("maxSize")
    private Integer maxSize;
	public Integer getMaxSize() {
		return maxSize;
	}
	public void setMaxSize(Integer maxSize) {
		this.maxSize = maxSize;
	}
	/*-----------------------*/
	@JsonProperty("order")
    private Integer order;
	@JsonProperty("order")
    public Integer getOrder() {
		return order;
	}
    @JsonProperty("order")
	public void setOrder(Integer order) {
		this.order = order;
	}
	/*-----------------------*/
    @JsonProperty("helperText")
    private String helperText;

    public String getHelperText() {
		return helperText;
	}
	public void setHelperText(String helperText) {
		this.helperText = helperText;
	}
	/*-----------------------*/
	@JsonProperty("questions")
    private List<Question> questions;

    @JsonProperty("questions")
    public List<Question> getQuestions() {
        return questions;
    }
    @JsonProperty("questions")
    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

	/*--------------------------------------------------------------*
	 * DefaultConfiguration
	 *--------------------------------------------------------------*/    
    protected void setDefaultConfiguration() {
		this.code = null;
		this.label = null;
		this.visible = Boolean.valueOf(true);
		this.tooltip = null;
		this.url = null;
		this.render = GroupRenderType.TABLE;
		this.minSize = null;
		this.maxSize = null;
		this.order = null;
		this.helperText = null;
		this.questions = new ArrayList<>();
    }

	/*--------------------------------------------------------------*
	 * Constructors
	 *--------------------------------------------------------------*/    
	public Group() {
		super();
		this.setDefaultConfiguration();
	}

	public Group(String codigo, String label, GroupRenderType render) {
		super();
		this.setDefaultConfiguration();
		this.code = codigo.trim();
		this.label = label.trim();
		this.render=render;
	}

	public Group(Group group) {
		super();
		this.setDefaultConfiguration();
		this.code = group.getCode();
		this.label = group.getLabel();
		this.visible = group.getVisible();
		this.tooltip = group.getTooltip();
		this.url = group.getUrl();
		this.render = group.getRender();
		this.minSize = group.getMinSize();
		this.maxSize = group.getMaxSize();
		this.order = group.getOrder();
		this.helperText = group.getHelperText();
	}

	/*--------------------------------------------------------------*
	 * Utilities
	 *--------------------------------------------------------------*/    
	//SONAR: This line will not be executed conditionally; only the first line of this 2-line block will be. The rest will execute unconditionally.
	public Question addQuestion(Question catalogQuestion) {
		if(catalogQuestion==null) return null;
    	if(getQuestion(catalogQuestion.getCode())!=null) {
			LoggerHelper.log(Level.WARNING,  String.format("Duplicated section %s. Ignored", catalogQuestion.getCode()));
    	}
    	Question result = new Question(catalogQuestion);
    	this.questions.add(result);
    	return result;
    }
    
    public Question getQuestion(String questionCode) {
    	if(StringHelper.emptyOrNull(questionCode)) return null;	
    	for(Question q: this.getQuestions()) {
    		if(questionCode.equals(q.getCode())) return q;
    	}
    	return null;
    }
}
