package com.decide.forms.layout;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.decide.forms.util.LoggerHelper;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class Section {
	/*--------------------------------------------------------------*
	 * Bean
	 *--------------------------------------------------------------*/    
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
    @JsonProperty("questions")
    private List<Question> questions;
    public List<Question> getQuestions() {
        return questions;
    }
    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
	/*-----------------------*/
    @JsonProperty("groups")
    private List<Group> groups;
	public List<Group> getGroups() {
		return groups;
	}
	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
	/*-----------------------*/
    @JsonProperty("order")
    private Integer order;
    public Integer getOrder() {
		return order;
	}
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

	/*--------------------------------------------------------------*
	 * DefaultConfiguration
	 *--------------------------------------------------------------*/    
    protected void setDefaultConfiguration() {
		this.code = null;
		this.label = null;
		this.visible = Boolean.valueOf(true);
		this.tooltip = null;
		this.url = null;
		this.questions = new ArrayList<>();
		this.groups = new ArrayList<>();
		this.order = null;
		this.helperText = null;

    }

	/*--------------------------------------------------------------*
	 * Constructors
	 *--------------------------------------------------------------*/    
	public Section() {
		super();
		setDefaultConfiguration();
	}

	public Section(String codigo, String label) {
		super();
		setDefaultConfiguration();
		this.code = codigo.trim();
		this.label = label.trim();
	}

	public Section(Section section) {
		super();
		setDefaultConfiguration();
		this.code = section.code.trim();
		this.label = section.label;
		this.visible = section.visible;
		this.tooltip = section.tooltip;
		this.url = section.url;
		this.order = section.order;
		this.helperText = section.helperText;
	}

	/*--------------------------------------------------------------*
	 * Utilities
	 *--------------------------------------------------------------*/    
	protected boolean emptyString(String str) {
		return str==null || str.isEmpty();
	}
	//SONAR: This line will not be executed conditionally; only the first line of this 2-line block will be. The rest will execute unconditionally.
	public void addQuestion(Question catalogQuestion) {
		if(catalogQuestion==null) return;
    	if(getQuestion(catalogQuestion.getCode())!=null) {
			LoggerHelper.log(Level.WARNING,  String.format("Duplicated section %s. Ignored", catalogQuestion.getCode()));
    	}
    	this.questions.add(new Question(catalogQuestion));
    }
	//SONAR: This line will not be executed conditionally; only the first line of this 2-line block will be. The rest will execute unconditionally.
	public void addGroup(Group catalogGroup) {
		if(catalogGroup==null) return;
    	if(getGroup(catalogGroup.getCode())!=null) {
			LoggerHelper.log(Level.WARNING,  String.format("Duplicated group %s. Ignored", catalogGroup.getCode()));
    	}
    	this.groups.add(new Group(catalogGroup));
    }
    
    public Question getQuestion(String questionCode) {
    	if(emptyString(questionCode)) return null;	
    	for(Question q: this.getQuestions()) {
    		if(questionCode.equals(q.getCode())) return q;
    	}
    	return null;
    }

    public Group getGroup(String groupCode) {
    	if(emptyString(groupCode)) return null;	
    	for(Group g: this.getGroups()) {
    		if(groupCode.equals(g.getCode())) return g;
    	}
    	return null;
    }
    
	public Group getOrCreateGroup(Group catalogGroup) {
		Group sectionGroup = getGroup(catalogGroup.getCode());
		if(sectionGroup==null) {
			sectionGroup= new Group(catalogGroup);
			this.groups.add(sectionGroup);
		}
		return sectionGroup;
	}


}
