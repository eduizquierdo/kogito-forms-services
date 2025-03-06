package com.decide.forms.layout;

import java.math.BigDecimal;
import java.text.ParseException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import com.decide.forms.exceptions.DateFormatException;
import com.decide.forms.exceptions.TypeMismatchException;
import com.decide.forms.model.Form;
import com.decide.forms.util.LoggerHelper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class Question {

	/*--------------------------------------------------------------*
	 * Bean
	 *--------------------------------------------------------------*/    
    @JsonProperty("code")
    private String code;
    @JsonProperty("label")
    private String label;
    @JsonProperty("visible")
    private Boolean visible;
    @JsonProperty("editable")
    private Boolean editable;
    @JsonProperty("dataType")
    private DataType dataType;
    @JsonProperty("render")
    private RenderType render;
    @JsonProperty("mandatory")
    private Boolean mandatory;
    @JsonProperty("tooltip")
    private String tooltip;
    @JsonProperty("refresh")
    private Boolean refresh;
    @JsonProperty("options")
    private List<QuestionOption> options;
    @JsonProperty("buttons")
    private List<Button> buttons;
    
    @JsonProperty("validated")
    private Boolean validated;
    @JsonProperty("order")
    private Integer order;
    @JsonProperty("newLine")
    private Boolean newLine;

    @JsonProperty("helperText")
    private String helperText;
    
	@JsonProperty("callback")
	private String callback;

	//Propiedades de tipos especificos
    @JsonProperty("maxLength")
    private Integer maxLength;
    @JsonProperty("minLength")
    private Integer minLength;
    @JsonProperty("minValue")
    private Number minValue;
    @JsonProperty("maxValue")
    private Number maxValue;
    @JsonProperty("numDecimals")
    private Integer numDecimals;
    @JsonProperty("formatDate")
    private String formatDate ;
    @JsonProperty("extensions")
    private List<String> extensions;
    @JsonProperty("valija")
    private boolean valija;

    public Boolean getNewLine() {
		return newLine;
	}
	public void setNewLine(Boolean newLine) {
		this.newLine = newLine;
	}
	
	public String getHelperText() {
		return helperText;
	}
	public void setHelperText(String helperText) {
		this.helperText = helperText;
	}
	@JsonProperty("order")
    public Integer getOrder() {
		return order;
	}
    @JsonProperty("order")
	public void setOrder(Integer order) {
		this.order = order;
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

    @JsonProperty("visible")
    public Boolean getVisible() {
        return visible;
    }
    @JsonProperty("visible")
    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    @JsonProperty("editable")
    public Boolean getEditable() {
        return editable;
    }
    @JsonProperty("editable")
    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    @JsonProperty("dataType")
    public DataType getDataType() {
        return dataType;
    }
    @JsonProperty("dataType")
    public void setDataType(DataType dataType) {
        this.dataType= dataType;
    }

    @JsonProperty("render")
    public RenderType getRender() {
        return render;
    }
    @JsonProperty("render")
    public void setRender(RenderType render) {
        this.render= render;
    }

    @JsonProperty("mandatory")
    public Boolean getMandatory() {
        return mandatory;
    }
    @JsonProperty("mandatory")
    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    @JsonProperty("tooltip")
    public String getTooltip() {
        return tooltip;
    }
    @JsonProperty("tooltip")
    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    @JsonProperty("refresh")
    public Boolean getRefresh() {
        return refresh;
    }
    @JsonProperty("refresh")
    public void setRefresh(Boolean refresh) {
        this.refresh = refresh;
    }

    @JsonProperty("options")
    public List<QuestionOption> getOptions() {
        return options;
    }
    @JsonProperty("options")
    public void setOptions(List<QuestionOption> options) {
        this.options = options;
    }

    @JsonProperty("buttons")
    public List<Button> getButtons() {
        return buttons;
    }
    @JsonProperty("buttons")
    public void setButtons(List<Button> buttons) {
        this.buttons = buttons;
    }
    
    @JsonProperty("callback")
	public String getCallback() {
		return callback;
	}
	@JsonProperty("callback")
	public void setCallback(String callback) {
		this.callback = callback;
	}
    
    public Integer getMaxLength() {
		return maxLength;
	}
	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}
	public Integer getMinLength() {
		return minLength;
	}
	public void setMinLength(Integer minLength) {
		this.minLength = minLength;
	}
	public Number getMinValue() {
		return minValue;
	}
	public void setMinValue(Number minValue) {
		this.minValue = minValue;
	}
	public Number getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(Number maxValue) {
		this.maxValue = maxValue;
	}
	public Integer getNumDecimals() {
		return numDecimals;
	}
	public void setNumDecimals(Integer numDecimals) {
		this.numDecimals = numDecimals;
	}
	@JsonProperty("validated")
    public Boolean getValidated() {
		return validated;
	}
    @JsonProperty("validated")
	public void setValidated(Boolean validated) {
		this.validated = validated;
	}
	public String getFormatDate() {
		return formatDate;
	}
	public void setFormatDate(String formatDate) {
		this.formatDate = formatDate;
	}
	public List<String> getExtensions() {
		return extensions;
	}
	public void setExtensions(List<String> extensions) {
		this.extensions = extensions;
	}
	public boolean isValija() {
		return valija;
	}
	public void setValija(boolean valija) {
		this.valija = valija;
	}
	
	/*--------------------------------------------------------------*
	 * Constructores
	 *--------------------------------------------------------------*/    
	public Question() {
		super();
	}
    
    public Question(String code, String label, DataType dataType, RenderType render) {
		super();
		this.code = code.trim();
		this.label = label;
		this.visible = true;
		this.editable = (render!=RenderType.LINK);
		this.dataType=dataType;
		this.render = render;
		this.mandatory = false;
		this.tooltip = null;
		this.refresh = false;
		this.options = new ArrayList<>();
		this.buttons = new ArrayList<>();
		this.validated = true;
		this.newLine = false;
		this.callback= null;
		this.extensions = new ArrayList<>();
		this.valija = false;
    }

    public Question(Question question) {
		super();
		this.code = question.code.trim();
		this.label = question.label;
		this.visible = question.visible;
		this.editable = question.editable;
		this.dataType = question.dataType;
		this.render = question.render;
		this.mandatory = question.mandatory;
		this.tooltip = question.tooltip;
		this.refresh = question.refresh;
		this.options = question.getOptions().stream().map(QuestionOption::new).collect(Collectors.toList());
		this.buttons = question.getButtons().stream().map(Button::new).collect(Collectors.toList());
		this.defaultValue = question.defaultValue;
		this.validated = question.validated;
		this.numDecimals = question.numDecimals;
		this.maxLength = question.maxLength;
		this.minLength = question.minLength;
		this.minValue = question.minValue;
		this.maxValue = question.maxValue;
		this.order = question.order;
		this.formatDate = question.formatDate;
		this.newLine = question.newLine;
		this.helperText = question.helperText;
		this.extensions = question.extensions;
		this.valija = question.valija;
		this.callback = question.callback;
    }

	/*--------------------------------------------------------------*
	 * Utilities
	 *--------------------------------------------------------------*/    
    public void addButton(Button catalogButton) {
    	if(catalogButton==null) return;
    	this.buttons.add(new Button(catalogButton));
    }
    
    public void addOption(QuestionOption option ) {
    	if(option==null || option.getCode()==null || option.getCode().isEmpty()) return;
    	Object value = computeOptionValue(option);
    	option.setValue(value);
    	this.options.add(option);
    }
    
	public Button getButton(String buttonCode) {
		for (Iterator<Button> iterator = buttons.iterator(); iterator.hasNext();) {
			Button button= iterator.next();
			if(button.getCode().equals(buttonCode)) return button;
		}
		return null;
	}
    
	public QuestionOption getQuestionOption(String questionOptionCode) {
		for (Iterator<QuestionOption> iterator = options.iterator(); iterator.hasNext();) {
			QuestionOption questionOption= iterator.next();
			if(questionOption.getCode().equals(questionOptionCode)) return questionOption;
		}
		return null;
	}
	
    protected Object computeOptionValue(QuestionOption option) {
    	// for the moment question options are only supported for INT and String
    	switch (this.dataType) {
		case TEXT:
			return option.getCode();

		case INTEGER:
			Integer value = null;
			try {
				value = Integer.parseInt(option.getCode());
			} catch(Exception e) {
				LoggerHelper.log(Level.WARNING, String.format("Type mismatch of option %s for question %s. Option is ignored", option.getCode(), this.code));
				value = null;
			}
			return value;

		default:
			LoggerHelper.log(Level.WARNING, String.format("Unrecognized or unsupported option type for question %s. Option %s is ignored", this.code,option.getCode()));
			break;
		}
    	
    	return null;
    }
    
    @JsonIgnore
    private Object defaultValue;
    
    public void setDefaultValue(String defaultValue) throws TypeMismatchException {
    	try {
	    	switch (dataType) {
				case BOOLEAN:
					this.defaultValue = Boolean.valueOf(defaultValue);
					break;
				case DATE:
					setDefaultValueDate(defaultValue);
					break;
				case DOUBLE:
					this.defaultValue = new BigDecimal(defaultValue);
					break;
				case INTEGER:
					this.defaultValue = Integer.valueOf(defaultValue);
					break;
				case TEXT:
					this.defaultValue = String.valueOf(defaultValue);
					break;
				default:
					this.defaultValue = String.valueOf(defaultValue);
					StringBuilder str = new StringBuilder();
					str.append("Wrong format for default value of question ").append(this.code);
					LoggerHelper.log(Level.WARNING, str.toString());
	    	}
    	}
	    catch (Exception e ) {
			StringBuilder str = new StringBuilder();
			str.append(this.dataType).append(" was expected for default value of question ").append(this.code).append(". Default value ignored.");
			LoggerHelper.log(Level.WARNING, str.toString());
			throw new TypeMismatchException(str.toString(), Level.WARNING, this.code);
	    }
    }
    
	public Object getDefaultValue() {
		return this.defaultValue;
	}
	
	public void setDefaultValueDate(String defaultValue) throws DateFormatException {
		SimpleDateFormat format = new SimpleDateFormat(Form.DEFAULT_DATE_FORMAT_PATTERN);
		try {
			this.defaultValue = format.parse(defaultValue);
		} catch (ParseException e) {
			StringBuilder str = new StringBuilder();
			str.append("Wrong date format for default value of question ").append(this.code).append(". Expected format: ").append(Form.DEFAULT_DATE_FORMAT_PATTERN);
			LoggerHelper.log(Level.WARNING, str.toString());
			throw new DateFormatException(str.toString(),Level.WARNING, this.code);
		}
	}

}
