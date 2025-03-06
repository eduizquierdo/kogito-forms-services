package com.decide.forms.layout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import com.decide.forms.util.LoggerHelper;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class Page {
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
    private List<Question> questions = new ArrayList<>();
    public List<Question> getQuestions() {
        return questions;
    }
    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
	/*-----------------------*/
    @JsonProperty("sections")
    private List<Section> sections = new ArrayList<>();
    public List<Section> getSections() {
        return sections;
    }
    public void setSections(List<Section> sections) {
        this.sections = sections;
    }
	/*-----------------------*/
    @JsonProperty("groups")
    private List<Group> groups = new ArrayList<>();
    public List<Group> getGroups() {
		return groups;
	}
	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
	/*-----------------------*/
    @JsonProperty("buttons")
    private List<Button> buttons = new ArrayList<>();
	public List<Button> getButtons() {
        return buttons;
    }
    public void setButtons(List<Button> buttons) {
        this.buttons = buttons;
    }
	/*-----------------------*/
    @JsonProperty("helperText ")
    private String helperText ;
    public String getHelperText() {
		return helperText;
	}
	public void setHelperText(String helperText) {
		this.helperText = helperText;
	}

	/*--------------------------------------------------------------*
	 * Constructors
	 *--------------------------------------------------------------*/    
	public Page() {
		super();
	}

	public Page(String code, String label) {
		super();
		this.code = code.trim();
		this.label=label;
		this.visible = true;
		this.tooltip = null;
		this.url = null;
		this.questions = null;
	}

	public Page(Page page) {
		super();
		this.code = page.code.trim();
		this.label = page.label;
		this.visible = page.visible;
		this.tooltip = page.tooltip;
		this.url = page.url;
		this.helperText = page.helperText;
	}

	/*--------------------------------------------------------------*
	 * Utilities
	 *--------------------------------------------------------------*/    
	public Section getSection(String sectionCode) {
		
		for (Iterator<Section> iterator = sections.iterator(); iterator.hasNext();) {
			Section section= iterator.next();
			if(section.getCode().equals(sectionCode)) return section;
		}
		return null;
	}

	public Group getGroup(String groupCode) {
		
		for (Iterator<Group> iterator = groups.iterator(); iterator.hasNext();) {
			Group group= iterator.next();
			if(group.getCode().equals(groupCode)) return group;
		}
		return null;
	}

	public Question getOrCreatequestion(Question catalogQuestion) {
		Question pageQuestion = getQuestion(catalogQuestion.getCode());
		if(pageQuestion==null) {
			pageQuestion = new Question(catalogQuestion);
			this.questions.add(pageQuestion);
		}
		return pageQuestion;
	}

	public Section getOrCreateSection(Section catalogSection) {
		Section pageSection = getSection(catalogSection.getCode());
		if(pageSection==null) {
			pageSection = new Section(catalogSection);
			this.sections.add(pageSection);
		}
		return pageSection;
	}

	public Group getOrCreateGroup(Group catalogGroup) {
		if(catalogGroup==null) return null;
		Group pageGroup = getGroup(catalogGroup.getCode());
		if(pageGroup==null) {
			pageGroup= new Group(catalogGroup);
			this.groups.add(pageGroup);
		}
		return pageGroup;
	}

	public void addSection(Section catalogSection) {
		if(getSection(catalogSection.getCode())!=null) {
			LoggerHelper.log(Level.WARNING, String.format("Duplicated section %s in page %s. Ignored", catalogSection.getCode(),this.code));
			return;
		}
		this.sections.add(new Section(catalogSection));
	}

	public Section addCatalogSection(Section catalogSection) {
		if(getSection(catalogSection.getCode())!=null) {
			LoggerHelper.log(Level.WARNING, String.format("Duplicated section %s in page %s. Ignored", catalogSection.getCode(),this.code));
			return null;
		}
		Section section = new Section(catalogSection);
		this.sections.add(section);
		return section;
	}

	public Question getQuestion(String questionCode) {
		for (Iterator<Question> iterator = questions.iterator(); iterator.hasNext();) {
			Question question= iterator.next();
			if(question.getCode().equals(questionCode)) return question;
		}
		return null;
	}

	public void addQuestion(Question question) {
		if(getQuestion(question.getCode())!=null) {
			LoggerHelper.log(Level.WARNING, String.format("Duplicated question %s in page %s. Ignored", question.getCode(), this.code));
			return;
		}
		this.questions.add(new Question(question));
	}

	public Button getButton(String buttonCode) {
		for (Iterator<Button> iterator = buttons.iterator(); iterator.hasNext();) {
			Button button= iterator.next();
			if(button.getCode().equals(buttonCode)) return button;
		}
		return null;
	}


	public void addButton(Button button) {
		if(getButton(button.getCode())!=null) {
			LoggerHelper.log(Level.WARNING, String.format("Duplicated button %s in page %s. Ignored", button.getCode(), this.code));
			return;
		}
		this.buttons.add(new Button(button));
	}

    
}
