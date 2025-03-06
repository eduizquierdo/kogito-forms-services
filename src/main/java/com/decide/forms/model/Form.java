package com.decide.forms.model;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.decide.forms.commands.FormCommand;
import com.decide.forms.commands.FormCommandFeature;
import com.decide.forms.exceptions.DateFormatException;
import com.decide.forms.exceptions.NonAnsweredQuestionException;
import com.decide.forms.exceptions.NonExistingFormItemException;
import com.decide.forms.exceptions.TypeMismatchException;
import com.decide.forms.layout.Button;
import com.decide.forms.layout.FormLayout;
import com.decide.forms.layout.Page;
import com.decide.forms.layout.Question;
import com.decide.forms.layout.QuestionOption;
import com.decide.forms.layout.Section;
import com.decide.forms.util.Constant;
import com.decide.forms.util.LoggerHelper;
import com.decide.forms.util.ObjectHelper;
import com.decide.forms.util.StringHelper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Form {
	/*--------------------------------------------------------------*
	 * Static data
	 *--------------------------------------------------------------*/
	public    static final String  DEFAULT_DATE_FORMAT_PATTERN = "dd/MM/yyyy";
	protected static final Integer DEFAULT_DECIMAL_PRECISION = 2;
	protected static final Integer MAX_DECIMAL_PRECISION = 5;
	protected static final boolean LAZY_DECIMAL_ROUNDING = false;
	public    static final String  GROUP_INDEX_KEY="__GroupIndex__";
	public    static final boolean ADD_NON_EXISTING_MERGE_DATA=true;
	public    static final String ATTACHMENTS_PROPERTY_NAME="attachments";
	public    static final String IN="in";
	
	/*--------------------------------------------------------------*
	 * Bean
	 *--------------------------------------------------------------*/
	@JsonProperty("decimalPrecision")
	private Integer decimalPrecision;
	public Integer getDecimalPrecision() {
		return decimalPrecision;
	}
	public void setDecimalPrecision(Integer decimalPrecision) {
		// check positive decimal precision
		if(decimalPrecision<0) decimalPrecision=0;		
		// check max allowed precision
		this.decimalPrecision = (decimalPrecision>MAX_DECIMAL_PRECISION) ? MAX_DECIMAL_PRECISION : decimalPrecision;
	}

	@JsonProperty("dateFormat")
	private String dateFormat;
	public String getDateFormat() {
		return dateFormat;
	}
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	@JsonProperty("code")
	private String code;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	@JsonProperty("currentStatus")
	private FormStatus currentStatus;
	public  FormStatus getCurrentStatus() {
		return currentStatus;
	}
	public void setCurrentStatus( FormStatus currentStatus) {
		this.currentStatus = currentStatus;
	}

	@JsonProperty("formLayout")
	private FormLayout formLayout;
	public FormLayout getFormLayout() {
		return formLayout;
	}
	public void setFormLayout(FormLayout formLayout) {
		this.formLayout = formLayout;
	}

	@JsonProperty("formData")
	@JsonInclude(JsonInclude.Include.ALWAYS)
	private Map<String,Object> formData;
	public Map<String,Object> getFormData() {
		return formData;
	}
	public void setFormData(Map<String,Object> formData) {
		this.formData = formData;
	}
	
	//Datos para archivos atachados
	@JsonProperty("title")
	private String title;
	@JsonProperty("allowAttach")
	private Boolean allowAttach;
	@JsonProperty("instructions")
	private String instructions;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Boolean getAllowAttach() {
		return allowAttach;
	}
	public void setAllowAttach(Boolean allowAttach) {
		this.allowAttach = allowAttach;
	}
	public String getInstructions() {
		return instructions;
	}
	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}
	
	@JsonIgnore
	private List<Map<String,Object>> attachments = new ArrayList<>();

	/*------------------------------------------------------------------------------------*
	 * Internal execution status to report Errors
	 *------------------------------------------------------------------------------------*/
	@JsonIgnore
	private ExecutionStatus executionStatus = null;
    public ExecutionStatus getExecutionStatus() {
        return executionStatus;
    }
    public void setExecutionStatus(ExecutionStatus executionStatus) {
        this.executionStatus = executionStatus;
    }
	

	/*-----------------------------------------------------*
	 * Contructors
	 *-----------------------------------------------------*/
	public Form() {
		super();
		this.code=null;
		this.formData=null;
		this.decimalPrecision=DEFAULT_DECIMAL_PRECISION;
		this.dateFormat=DEFAULT_DATE_FORMAT_PATTERN;
	}

	public Form(String code,FormLayout layout) {
		super();
		this.code=code;
		this.formLayout=layout;
		this.formData = null;
		this.decimalPrecision=DEFAULT_DECIMAL_PRECISION;
		this.dateFormat=DEFAULT_DATE_FORMAT_PATTERN;
	}


	public Form(String code, Map<String,Object> formData) {
		super();
		this.code=code;
		this.formLayout=null;
		this.formData = formData;
		this.decimalPrecision=DEFAULT_DECIMAL_PRECISION;
		this.dateFormat=DEFAULT_DATE_FORMAT_PATTERN;
	}

	/*--------------------------------------------------------------*
	 * Business Data Access [FORM DATA]
	 *--------------------------------------------------------------*/
	
			/*-------------------------------------*
			 * Checks
			 *-------------------------------------*/
	
	public void checkPageQuestion(String pageCode, String questionCode, String ruleName) throws NonExistingFormItemException {
		Map<String, Object> page = getPage(pageCode, ruleName);
		if(!page.containsKey(questionCode)) 
			throw new NonExistingFormItemException(String.format("Question %s on page %s not found.", questionCode,pageCode), Level.WARNING, getIdReferencePageElement(pageCode, questionCode));
	}
	
	public void checkSectionQuestion(String pageCode, String sectionCode, String questionCode, String ruleName) throws NonExistingFormItemException {
		Map<String, Object> page = getPage(pageCode, ruleName);
		Map<String, Object> section = getSection(page, pageCode, sectionCode, ruleName);
		
		
		if(!section.containsKey(questionCode)) 
			throw new NonExistingFormItemException(String.format("Question %s on page-section %s-%s not found.", questionCode,pageCode,sectionCode), Level.WARNING, getIdReferencePageSectionQuestion(pageCode, sectionCode, questionCode));
	}
	
			/*-------------------------------------*
			 * Getters
			 *-------------------------------------*/
	
	public String getPageQuestionAsText(String pageCode, String questionCode, String ruleName ) throws NonExistingFormItemException, TypeMismatchException, NonAnsweredQuestionException {
		LoggerHelper.log(Level.FINER, () -> String.format("getting response of question %s in form %s as TEXT", questionCode, this.code));

		Map<String, Object> page = getPage(pageCode, ruleName);
		return getTypedQuestion(page, pageCode, questionCode, ruleName, String.class);
	}

	public Integer getPageQuestionAsInteger(String pageCode, String questionCode, String ruleName ) throws NonExistingFormItemException, TypeMismatchException, NonAnsweredQuestionException {
		LoggerHelper.log(Level.FINER, () -> String.format("getting response of question %s in form %s as INTEGER", questionCode, this.code));

		Map<String, Object> page = getPage(pageCode, ruleName);
		return getTypedQuestion(page, pageCode, questionCode, ruleName, Integer.class);
	}

	public Double getPageQuestionAsDecimal(String pageCode, String questionCode, String ruleName ) throws NonExistingFormItemException, TypeMismatchException, NonAnsweredQuestionException {
		LoggerHelper.log(Level.FINER, () -> String.format("getting response of question %s in form %s as DECIMAL", questionCode, this.code));

		Map<String, Object> page = getPage(pageCode, ruleName);
		return getTypedQuestion(page, pageCode, questionCode, ruleName, Double.class);
	}
	
	public Boolean getPageQuestionAsBoolean(String pageCode, String questionCode, String ruleName ) throws NonExistingFormItemException, TypeMismatchException, NonAnsweredQuestionException {
		LoggerHelper.log(Level.FINER, () -> String.format("getting response of question %s in form %s as BOOLEAN", questionCode, this.code));

		Map<String, Object> page = getPage(pageCode, ruleName);
		return getTypedQuestion(page, pageCode, questionCode, ruleName, Boolean.class);
	}

	public Date getPageQuestionAsFormatDate(String pageCode, String questionCode, String dateFormatPattern, String ruleName ) throws NonExistingFormItemException, TypeMismatchException, DateFormatException, NonAnsweredQuestionException {
		LoggerHelper.log(Level.FINER, () -> String.format("getting response of question %s in form %s as DATE", questionCode, this.code));

		Map<String, Object> page = getPage(pageCode, ruleName);

		String strdate = getTypedQuestion(page, pageCode, questionCode, ruleName, String.class);
		Date result = null;
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormatPattern); 
			result = simpleDateFormat.parse(strdate);
		} catch (Exception e) {
			StringBuilder str = new StringBuilder();
			str.append("wrong date format in question ").append(questionCode).append(" in ").append(pageCode)
		       .append(Constant.BLANK).append(Constant.IN_FORM).append(Constant.BLANK).append(this.code).append(". Expected format: ").append(dateFormatPattern);
			if(ruleName!=null && !ruleName.isEmpty()) {
				str.append(Constant.DOT_BLANK).append(Constant.ACCESSED_IN_RULE).append(Constant.BLANK).append(ruleName);
			}
			throw new DateFormatException(str.toString(),Level.WARNING, getIdReferencePageElement(pageCode, questionCode));
		}		
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<String> getPageQuestionAsStringList(String pageCode, String questionCode, String ruleName ) throws NonExistingFormItemException, TypeMismatchException, NonAnsweredQuestionException {
		LoggerHelper.log(Level.FINER, () -> String.format("getting response of question %s in form %s as LIST", questionCode, this.code));

		Map<String, Object> page = getPage(pageCode, ruleName);
		return  getTypedQuestion(page, pageCode, questionCode, ruleName, List.class);
	}

	public String getSectionQuestionAsText(String pageCode, String sectionCode, String questionCode, String ruleName ) throws NonExistingFormItemException, TypeMismatchException, NonAnsweredQuestionException {
		Map<String, Object> page = getPage(pageCode, ruleName);
		Map<String, Object> section = getSection(page, pageCode, sectionCode, ruleName);
		String response = getTypedQuestion(section, sectionCode, questionCode, ruleName, String.class); 
		LoggerHelper.log(Level.FINER, () -> String.format("getting response of question %s in form %s as TEXT: %s", questionCode, this.code,response));
		return response;
	}

	public Integer getSectionQuestionAsInteger(String pageCode, String sectionCode, String questionCode, String ruleName ) throws NonExistingFormItemException, TypeMismatchException, NonAnsweredQuestionException {
		LoggerHelper.log(Level.FINER, () -> String.format("getting response of question %s in form %s as INTEGER", questionCode, this.code));

		Map<String, Object> page = getPage(pageCode, ruleName);
		Map<String, Object> section = getSection(page, pageCode, sectionCode, ruleName);
		return getTypedQuestion(section, sectionCode, questionCode, ruleName, Integer.class);
	}
	
	public Double getSectionQuestionAsDecimal(String pageCode, String sectionCode, String questionCode, String ruleName ) throws NonExistingFormItemException, TypeMismatchException, NonAnsweredQuestionException {
		LoggerHelper.log(Level.FINER, () -> String.format("getting response of question %s in form %s as DECIMAL", questionCode, this.code));

		Map<String, Object> page = getPage(pageCode, ruleName);
		Map<String, Object> section = getSection(page, pageCode, sectionCode, ruleName);
		return getTypedQuestion(section, sectionCode, questionCode, ruleName, Double.class);
	}
	
	public Boolean getSectionQuestionAsBoolean(String pageCode, String sectionCode, String questionCode, String ruleName ) throws NonExistingFormItemException, TypeMismatchException, NonAnsweredQuestionException {
		LoggerHelper.log(Level.FINER, () -> String.format("getting response of question %s in form %s as BOOLEAN", questionCode, this.code));

		Map<String, Object> page = getPage(pageCode, ruleName);
		Map<String, Object> section = getSection(page, pageCode, sectionCode, ruleName);
		return getTypedQuestion(section, sectionCode, questionCode, ruleName, Boolean.class);
	}

	public Date getSectionQuestionAsFormatDate(String pageCode, String sectionCode, String questionCode, String dateFormatPattern, String ruleName ) throws NonExistingFormItemException, TypeMismatchException, DateFormatException, NonAnsweredQuestionException {
		LoggerHelper.log(Level.FINER, () -> String.format("getting response of question %s in form %s as DATE", questionCode, this.code));

		Map<String, Object> page = getPage(pageCode, ruleName);
		Map<String, Object> section = getSection(page, pageCode, sectionCode, ruleName);
		
		String strdate = getTypedQuestion(section, sectionCode,questionCode, ruleName, String.class);
		Date result = null;
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormatPattern); 
			result = simpleDateFormat.parse(strdate);
		} catch (Exception e) {
			StringBuilder str = new StringBuilder();
			str.append("wrong date format in question ").append(questionCode).append(" in ").append(pageCode)
		       .append(Constant.BLANK).append(Constant.IN_FORM).append(Constant.BLANK).append(this.code).append(". Expected format: ").append(dateFormatPattern);
			if(ruleName!=null && !ruleName.isEmpty()) {
				str.append(Constant.DOT_BLANK).append(Constant.ACCESSED_IN_RULE).append(Constant.BLANK).append(ruleName);
			}
			throw new DateFormatException(str.toString(),Level.WARNING, getIdReferencePageSectionQuestion(pageCode, sectionCode, questionCode));
		}		
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<String> getSectionQuestionAsStringList(String pageCode, String sectionCode, String questionCode, String ruleName ) throws NonExistingFormItemException, TypeMismatchException, NonAnsweredQuestionException {
		LoggerHelper.log(Level.FINER, () -> String.format("getting response of question %s in form %s as LIST", questionCode, this.code));

		Map<String, Object> page = getPage(pageCode, ruleName);
		Map<String, Object> section = getSection(page, pageCode, sectionCode, ruleName);
		return  getTypedQuestion(section, sectionCode, questionCode, ruleName, List.class);
	}
	
			/*-------------------------------------*
			 * Exist
			 *-------------------------------------*/

	public boolean existPageQuestion(String pageCode, String questionCode, String ruleName) throws NonExistingFormItemException {
		Map<String, Object> page = getPage(pageCode, ruleName);
		return page.containsKey(questionCode);
	}
	
	public boolean existSection(String pageCode, String sectionCode, String ruleName) throws NonExistingFormItemException {
		Map<String, Object> page = getPage(pageCode, ruleName);
		Map<String, Object> section = getSection(page, pageCode, sectionCode, ruleName);
		return section!=null;
	}
	
	public boolean existSectionQuestion(String pageCode, String sectionCode, String questionCode, String ruleName) throws NonExistingFormItemException {
		Map<String, Object> page = getPage(pageCode, ruleName);
		Map<String, Object> section = getSection(page, pageCode, sectionCode, ruleName);
		return section.containsKey(questionCode);
	}

			/*-------------------------------------*
			 * Clean
			 *-------------------------------------*/
	public void cleanSection(String pageCode, String sectionCode, String ruleName) throws NonExistingFormItemException {
		LoggerHelper.log(Level.FINE, () -> String.format("cleaing section %s of page %s in form %s", sectionCode, pageCode, this.code));

		Map<String, Object> page = getPage(pageCode, ruleName);
		Map<String, Object> section = getSection(page, pageCode, sectionCode, ruleName);
		for(String key: section.keySet()) {
			section.put(key, null);
		}
	}

	public void cleanPageQuestion(String pageCode, String questionCode, String ruleName) throws NonExistingFormItemException {
		LoggerHelper.log(Level.FINE, () -> String.format("cleaing question %s of page %s in form %s", questionCode, pageCode, this.code));

		Map<String, Object> page = getPage(pageCode, ruleName);
		if(!page.containsKey(questionCode)) {
			StringBuilder str = new StringBuilder();
			str.append(Constant.QUESTION).append(Constant.BLANK).append(questionCode).append(" not found in ").append(pageCode).append(Constant.BLANK).append(Constant.IN_FORM).append(Constant.BLANK).append(this.code);
			if(ruleName!=null && !ruleName.isEmpty()) {
				str.append(Constant.DOT_BLANK).append(Constant.ACCESSED_IN_RULE).append(Constant.BLANK).append(ruleName);
			}
			throw new NonExistingFormItemException(str.toString(),Level.WARNING, getIdReferencePageElement(pageCode, questionCode));
		} else {
			page.put(questionCode, null);
		}
	}

	public void cleanSectionQuestion(String pageCode, String sectionCode, String questionCode, String ruleName) throws NonExistingFormItemException {
		LoggerHelper.log(Level.FINE, () -> String.format("cleaing question %s in section %s of page %s in form %s", questionCode, sectionCode, pageCode, this.code));

		Map<String, Object> page = getPage(pageCode, ruleName);
		Map<String, Object> section = getSection(page, pageCode, sectionCode, ruleName);
		if(!section.containsKey(questionCode)) {
			StringBuilder str = new StringBuilder();
			str.append(Constant.QUESTION).append(Constant.BLANK).append(questionCode).append(" not found in section ").append(sectionCode)
			   .append(" of page ").append(pageCode).append(Constant.BLANK).append(Constant.IN_FORM).append(Constant.BLANK).append(this.code);
			if(ruleName!=null && !ruleName.isEmpty()) {
				str.append(Constant.DOT_BLANK).append(Constant.ACCESSED_IN_RULE).append(Constant.BLANK).append(ruleName);
			}
			throw new NonExistingFormItemException(str.toString(),Level.WARNING, getIdReferencePageSectionQuestion(pageCode, sectionCode, questionCode));
		} else {
			section.put(questionCode, null);
		}

	}
	
	public void cleanQuestionGroup(String pageCode, String sectionCode, String groupCode, Integer occurrenceIndex,  String questionCode, String ruleName) throws NonExistingFormItemException {
		setQuestionGroup(pageCode, sectionCode, groupCode, occurrenceIndex, questionCode, null, ruleName);
	}
	
			/*-------------------------------------*
			 * Is answered
			 *-------------------------------------*/

	public boolean isPageQuestionAnswered(String pageCode, String questionCode, String ruleName) throws NonExistingFormItemException {
		Map<String, Object> page = getPage(pageCode, ruleName);
		if(!page.containsKey(questionCode)) 
			throw new NonExistingFormItemException(String.format("Question %s on page %s not found.", questionCode,pageCode), Level.WARNING, getIdReferencePageElement(pageCode, questionCode));
		return page.get(questionCode)!=null;
	}

	public boolean isSectionQuestionAnswered(String pageCode, String sectionCode, String questionCode, String ruleName) throws NonExistingFormItemException {
		Map<String, Object> page = getPage(pageCode, ruleName);
		Map<String, Object> section = getSection(page, pageCode, sectionCode, ruleName);
		if(!section.containsKey(questionCode)) 
			throw new NonExistingFormItemException(String.format("Question %s on section %s on page %s not found.", questionCode,sectionCode,pageCode), Level.WARNING, getIdReferencePageSectionQuestion(pageCode, sectionCode, questionCode));
		return section.get(questionCode)!=null;
	}

			/*-------------------------------------*
			 * Setters
			 *-------------------------------------*/
	
	public void setSectionQuestion(String pageCode, String sectionCode, String questionCode, Object value, String ruleName ) throws NonExistingFormItemException {
		LoggerHelper.log(Level.FINE, () -> String.format("changing response of question %s in section %s of page %s in form %s", questionCode, sectionCode, pageCode, this.code));
		Map<String, Object> page = getPage(pageCode, ruleName);
		Map<String, Object> section = getSection(page, pageCode, sectionCode, ruleName);
		if(value!=null && !LAZY_DECIMAL_ROUNDING && value instanceof Double) {
			section.put(questionCode,roundDecimalValue(Double.class.cast(value)));
		} else {
			section.put(questionCode, value);
		}
	}
	
	public void setQuestion(String pageCode, String questionCode, Object value, String ruleName ) throws NonExistingFormItemException {
		LoggerHelper.log(Level.FINE, () -> String.format("changing response of question %s of page %s in form %s", questionCode, pageCode, this.code));
		Map<String, Object> page = getPage(pageCode, ruleName);
		if(value!=null && !LAZY_DECIMAL_ROUNDING && value instanceof Double) {
			page.put(questionCode,roundDecimalValue(Double.class.cast(value)));
		} else {
			page.put(questionCode, value);
		}
	}
	
	public void setQuestionGroup(String pageCode, String sectionCode, String groupCode, Integer occurrenceIndex,  String questionCode, Object value, String ruleName) throws NonExistingFormItemException {
		//LoggerHelper.log(Level.FINE, () -> String.format("cleaing question %s in section %s of page %s in form %s", questionCode, sectionCode, pageCode, this.code));
		Map<String, Object> element ;
		Map<String, Object> page = getPage(pageCode, ruleName);
		
		Map<String, Object> section = sectionCode != null ? getSection(page, pageCode, sectionCode, ruleName) : null;

		
		List<Map<String, Object>> group = getGroup(page, section, pageCode, sectionCode, groupCode, ruleName);
		
		Map<String, Object> occurrence = group != null && occurrenceIndex != null && occurrenceIndex != -1 ? getOccurrenceGroup(group, pageCode, sectionCode, occurrenceIndex, ruleName) : null;
		
		element = occurrence != null ? occurrence : section;

		setValueQuestion(element, pageCode,sectionCode,groupCode,occurrenceIndex, questionCode, value, ruleName);

	}
	private void setValueQuestion(Map<String, Object> element, String pageCode,String sectionCode,String groupCode, Integer occurrenceIndex, String questionCode, Object value, String ruleName) throws NonExistingFormItemException{
		if(ObjectHelper.isNotNull(element) && !element.containsKey(questionCode)) {
			generateNonExistingFormItemException(pageCode, sectionCode, groupCode, occurrenceIndex, questionCode, null, ruleName);
		} else {
			if(element == null) element = new HashMap<>(); 
			element.put(questionCode, value);
		}
	}
	private List<Map<String, Object>> getGroup(Map<String, Object> page, Map<String, Object> section, String pageCode,String sectionCode,String groupCode, String ruleName) throws NonExistingFormItemException{
		List<Map<String, Object>> group = null;
		if(ObjectHelper.isNotNull(groupCode)) {
			if(section != null) {
				group = getGroupMap(section, pageCode, sectionCode, groupCode, ruleName);
			}else {
				group = getGroupMap(page, pageCode, sectionCode, groupCode, ruleName);
			}
			if(ObjectHelper.isNull(group)) generateNonExistingFormItemException(pageCode, sectionCode, groupCode, null, null, null, ruleName);
		}
		return group;
	}

	/*--------------------------------------------------------------*
	 * Business Data Access [FORM LAYOUT]
	 *--------------------------------------------------------------*/

	public void changeFeatureFormLayout(String pageCode, String sectionCode, String questionCode, String optionCode, String buttonCode, FormCommandFeature feature, Object value) throws NonExistingFormItemException {
		Page page = formLayout.getPage(pageCode);
			if(buttonCode!=null) { //Cambiar button
				Button button = null;

				if(questionCode!=null) {
					Question question = sectionCode!=null ? formLayout.getQuestion(questionCode, formLayout.getSection(sectionCode, page)) : formLayout.getQuestion(questionCode, page);
					button = formLayout.getButton(buttonCode, question);
				} else {
					button = formLayout.getButton(buttonCode, page);
				}
				if(button == null) generateButtonNonExistingFormItemException(buttonCode, null);
					
				switch (feature) {
				case ENABLED:
					button.setEnabled((Boolean)value);
					break;
				case VISIBLE:
					button.setVisible((Boolean)value);
					break;
				case CHANGE_LABEL:
					button.setLabel((String)value);
					break;
				case CHANGE_ACTION:
					button.setAction((String)value);
					break;
				default:
					LoggerHelper.log(Level.WARNING, String.format("Feature %s has been tried to change on button %s", feature, buttonCode ));
					break;
				}
				
			} else if(optionCode!=null) { //Cambiar option
				Question question = sectionCode!=null ? formLayout.getQuestion(questionCode, formLayout.getSection(sectionCode, page)) : formLayout.getQuestion(questionCode, page);
				QuestionOption option= formLayout.getOption(optionCode, question);

				if(option == null) generateNonExistingFormItemException(pageCode, sectionCode, null, null, questionCode, optionCode, null);

					
				switch (feature) {
				case ENABLED:
					option.setEnabled((Boolean)value);
					break;
				case VISIBLE:
					option.setVisible((Boolean)value);
					break;
				default:
					LoggerHelper.log(Level.WARNING, String.format("Feature %s has been tried to change on question option %s-%s", feature, questionCode, optionCode ));
					break;
				}
				
			} else if(questionCode!=null) { //Cambiar question
				Question question = sectionCode!=null ? formLayout.getQuestion(questionCode, formLayout.getSection(sectionCode, page)) : formLayout.getQuestion(questionCode, page);
				
				if(question == null) generateNonExistingFormItemException(pageCode, sectionCode, null, null, questionCode, optionCode, null);
				
				switch (feature) {
				case EDITABLE:
					question.setEditable((Boolean)value);
					break;
				case MANDATORY:
					question.setMandatory((Boolean)value);
					break;
				case VALIDATED:
					question.setValidated((Boolean)value);
					break;
				case VISIBLE:
					question.setVisible((Boolean)value);
					break;
				case CHANGE_HELPER_TEXT:
					question.setHelperText((String)value);
					break;	
				case CHANGE_TOOLTIP:
					question.setTooltip((String)value);
					break;
				case CHANGE_CALLBACK:
					question.setCallback((String)value);
					break;
				default:
					LoggerHelper.log(Level.WARNING, String.format("Feature %s has been tried to change on question %s. Ignored", feature, questionCode ));
					break;
				}
				
			} else if(sectionCode!=null) { //Cambiar section
				Section section = formLayout.getSection(sectionCode, page);
				if(section == null) generateNonExistingFormItemException(pageCode, sectionCode, null, null, questionCode, optionCode, null);
				
				if(feature == FormCommandFeature.VISIBLE) section.setVisible((Boolean)value);
				else LoggerHelper.log(Level.WARNING, String.format("Fature %s has been tried to change on section %s", feature, sectionCode ));
				
			} else { //Cambiar page
				if(feature == FormCommandFeature.VISIBLE) page.setVisible((Boolean)value);
				else LoggerHelper.log(Level.WARNING, String.format("Feature %s has been tried to change on page %s", feature, pageCode ));
			}
	}
	
	public void changeFeatureFormLayout(String pageCode, String sectionCode, String groupCode, String questionCode, String optionCode, String buttonCode, FormCommandFeature feature, Object value) throws NonExistingFormItemException {
		Page page = formLayout.getPage(pageCode);
		if(buttonCode!=null) { //Cambiar button
			Button button = null;
			if(questionCode!=null) {
				Question question = sectionCode!=null ? formLayout.getQuestion(questionCode, formLayout.getSection(sectionCode, page)) : formLayout.getQuestion(questionCode, page);
				button = formLayout.getButton(buttonCode, question);
			} else {
				button = formLayout.getButton(buttonCode, page);
			}
			
			if(button == null) generateButtonNonExistingFormItemException(buttonCode, null);
			
			switch (feature) {
			case ENABLED:
				button.setEnabled((Boolean)value);
				break;
			case VISIBLE:
				button.setVisible((Boolean)value);
				break;
			default:
				LoggerHelper.log(Level.WARNING, String.format("Feature %s has been tried to change on button %s", feature, buttonCode ));
				break;
			}
			
		} else if(optionCode!=null) { //Cambiar option
			Question question = sectionCode!=null ? formLayout.getQuestion(questionCode, formLayout.getSection(sectionCode, page)) : formLayout.getQuestion(questionCode, page);
			QuestionOption option= formLayout.getOption(optionCode, question);
			
			if(option == null) generateNonExistingFormItemException(pageCode, sectionCode, groupCode, null, questionCode, optionCode, null);

			switch (feature) {
			case ENABLED:
				option.setEnabled((Boolean)value);
				break;
			case VISIBLE:
				option.setVisible((Boolean)value);
				break;
			default:
				LoggerHelper.log(Level.WARNING, String.format("Feature %s has been tried to change on question option %s-%s", feature, questionCode, optionCode ));
				break;
			}
			
		} else if(questionCode!=null) { //Cambiar question
			Question question = sectionCode!=null ? formLayout.getQuestion(questionCode, formLayout.getSection(sectionCode, page)) : formLayout.getQuestion(questionCode, page);
			
			if(question == null) generateNonExistingFormItemException(pageCode, sectionCode, null, null, questionCode, optionCode, null);
			
			switch (feature) {
			case EDITABLE:
				question.setEditable((Boolean)value);
				break;
			case MANDATORY:
				question.setMandatory((Boolean)value);
				break;
			case VALIDATED:
				question.setValidated((Boolean)value);
				break;
			case VISIBLE:
				question.setVisible((Boolean)value);
				break;
			default:
				LoggerHelper.log(Level.WARNING, String.format("Feature %s has been tried to change on question %s. Ignored", feature, questionCode ));
				break;
			}
			
		} else if(sectionCode!=null) { //Cambiar section
			Section section = formLayout.getSection(sectionCode, page);
			
			if(section == null) generateNonExistingFormItemException(pageCode, sectionCode, groupCode, null, questionCode, optionCode, null);
			
			switch (feature) {
			case VISIBLE:
				section.setVisible((Boolean)value);
				break;
			default:
				LoggerHelper.log(Level.WARNING, String.format("Fature %s has been tried to change on section %s", feature, sectionCode ));
				break;
			}
			
		} else { //Cambiar page
			switch (feature) {
			case VISIBLE:
				page.setVisible((Boolean)value);
				break;
			default:
				LoggerHelper.log(Level.WARNING, String.format("Feature %s has been tried to change on page %s", feature, pageCode ));
				break;
			}
		}			
}

	/*----------------------------------------------------------------------------------------*
	 * Merge de los datos de Formulario después del layout con los datos guardados
	 *   en ela BD y enviados por un RELOAD o un VIEW
	 * El contexto en el que se llama a esta función es el siguiente:
	 *   1. se recibe formData en un VIEW o un RELOAD
	 *   2. se ejecuta el flujo "Form Layout"
	 *   3. se instancia formData con los valores por defecto resultantes de FormLayout
	 *   3. a continuación se hace un merge con la siguiente lógica
	 *      3.1 si el dato existe en el formData enviado por el VIEW/RELOAD, se toma este dato
	 *      3.2 si el dato no existe en el formData enviado por el VIEW/RELOAD, se deja el 
	 *          valor por defecto calculador en el formData del Layout
	 *      
	 *  Detalle del algoritmo:
	 *   1. en un primer nivel todo tienen que ser mapas    
	 *      
	 *----------------------------------------------------------------------------------------*/
	@SuppressWarnings("unchecked")
	public void mergeFormData(Map<String, Object> mergeData) {
		LoggerHelper.log(Level.FINE, "Merging data of form "+this.code);
		// iterar las paginas del formulario		
		for (Map.Entry<String, Object> entryPage: mergeData.entrySet()) {
			String pageCode = entryPage.getKey();
			Object obj = entryPage.getValue();
			LoggerHelper.log(Level.FINE, Constant.ELLIPSIS+Constant.BLANK+Constant.MERGING+Constant.BLANK +pageCode+Constant.BLANK+Constant.TYPE+Constant.BLANK+((obj==null)?"null":obj.getClass().getName()));
			if(obj instanceof Map<?, ?>) {
				// es una página, se procesa
				Map<String, Object> mergePage = (Map<String,Object>) obj;
				
				// la misma página debe existir en formData
				Map<String, Object> formPage = getFormContainer(this.formData,pageCode);
				if(formPage==null) {
					if(ADD_NON_EXISTING_MERGE_DATA) {
						this.formData.put(pageCode, obj);	
					} else {
						LoggerHelper.log(Level.SEVERE, 
								String.format("page %s was not found in structure while merging reloaded data form data of %s", pageCode, this.code ));
					}
				} else {
					this.formData.put(pageCode,mergePage(formPage,mergePage));
				}
			} else {
				// si no es una página sólo pueden ser attachment de documentos: se pone el valor de mergeData
				this.formData.put(entryPage.getKey(), obj);				
			}
			
		}
	}

	protected Map<String, Object> mergePage(Map<String, Object> formPage, Map<String, Object> mergePage) {
		LoggerHelper.log(Level.FINE, "...merging page");

		for (Map.Entry<String, Object> entry: mergePage.entrySet()) {
			String code = entry.getKey();
			Object obj = entry.getValue();
			LoggerHelper.log(Level.FINE, "... merging "+code+Constant.BLANK+Constant.TYPE+Constant.BLANK+((obj==null)?"null":obj.getClass().getName()));
			if(obj instanceof Map<?, ?>) {
				// es un fichero, se procesa
				if(File.isFile(obj)) {
					formPage.put(code,obj);
				}
				// es una seccion, se procesa 
				@SuppressWarnings("unchecked")
				Map<String, Object> mergeSection = (Map<String,Object>) obj;
				
				// la misma seccion debe existir en formPage
				Map<String, Object> formSection = getFormContainer(formPage,code);
				if(formSection==null) {
					if(ADD_NON_EXISTING_MERGE_DATA) {
						this.formData.put(code, obj);	
					} else  {
						LoggerHelper.log(Level.SEVERE, 
							String.format("section %s was not found in structure while merging reloaded data form data of %s", code, this.code ));
					}
				} else {
					formPage.put(code,mergeSection(formSection,mergeSection));
				}
				
			} else if(obj instanceof ArrayList<?>) {
				@SuppressWarnings("unchecked")
				ArrayList<Object> mergeArray = (ArrayList<Object>) obj;
				@SuppressWarnings("unchecked")
				ArrayList<Object> formArray = (ArrayList<Object>) formPage.get(code);
				if(formArray==null) {
					if(ADD_NON_EXISTING_MERGE_DATA) {
						formPage.put(code, obj);	
					} else {						
						LoggerHelper.log(Level.SEVERE, 
								String.format("group or array %s was not found in structure while merging reloaded data form data of %s", code, this.code ));
					}
				} else {
					// comprobamos que son ocurrencias de grupo: los elementos tienen que ser mapas
					if(checkGroupOcurrences(formArray)) {					
						formPage.put(code,mergeGroup(formArray,mergeArray));
					} else {
						formPage.put(code, obj);
					}
				}
			} else {
				// la única opción restante es que sea Double, String ... o Array de tipos básicos: se pone el valor de mergeData
				Object item = formPage.get(code);
				// si existe en la estructura se actualiza y si no existe pero hay que añadir datos no existentes se añade
				if(item!=null || ADD_NON_EXISTING_MERGE_DATA) {
					formPage.put(code, obj);
				}
			}
		}
		// devolvemos el mapa modificado
		return formPage;
	}

	protected Map<String, Object> mergeSection(Map<String, Object> formSection, Map<String, Object> mergeSection) {
		LoggerHelper.log(Level.FINE, "... merging section");
		for (Map.Entry<String, Object> entry: mergeSection.entrySet()) {
			String code = entry.getKey();
			Object obj = entry.getValue();
			LoggerHelper.log(Level.FINE, "... merging "+code+Constant.BLANK+Constant.TYPE+Constant.BLANK+((obj==null)?"null":obj.getClass().getName()));
			if(obj instanceof Map<?, ?>) {
				// es un fichero, se procesa
				if(File.isFile(obj)) {
					formSection.put(code,obj);
				}else {
					LoggerHelper.log(Level.SEVERE, 
							String.format("wrong data structure while merging reloaded data form data of %s. Section inside section? code: ", this.code, code ));
				}
			} else if(obj instanceof ArrayList<?>) {
				@SuppressWarnings("unchecked")
				ArrayList<Object> mergeArray = (ArrayList<Object>) obj;
				// el mismo array debe existir en formSection
				@SuppressWarnings("unchecked")
				ArrayList<Object> formArray = (ArrayList<Object>) formSection.get(code);
				if(formArray==null) {
					if(ADD_NON_EXISTING_MERGE_DATA) {
						formSection.put(code, obj);
					} else {
							LoggerHelper.log(Level.SEVERE, 
								String.format("group or array %s was not found in structure while merging reloaded data form data of %s", code, this.code ));
					} 
				} else {
					if(checkGroupOcurrences(formArray)) {
						formSection.put(code,mergeGroup(formArray,mergeArray));
					} else {
						formSection.put(code,obj);					
					}
				}				
			} else {
				// la única opción restante es que sea Double, String ... o Array de tipos básicos: se pone el valor de mergeData
				Object item = formSection.get(code);
				// si existe en la estructura se actualiza y si no existe pero hay que añadir datos no existentes se añade
				if(item!=null || ADD_NON_EXISTING_MERGE_DATA) {
					formSection.put(code, obj);
				}
			}
		}
		// devolvemos el mapa modificado
		return formSection;
	}

	protected ArrayList<Object> mergeGroup(ArrayList<Object> formGroup, ArrayList<Object> mergeGroup) {
		LoggerHelper.log(Level.FINE, "... merging group");
		// nos quedamos con el mapa-plantilla de la estructura del formulario
		@SuppressWarnings("unchecked")
		Map<String,Object> ocurrenceTemplate = (Map<String,Object>)formGroup.get(0);
		formGroup.remove(0);
		boolean existPlantilla = false;
		for(int i=0; i<mergeGroup.size(); i++) {			
			// para cada ocurrencia de los datos del RELOAD en merge Group ....
			@SuppressWarnings("unchecked")
			Map<String,Object> mergeOcurrence = (Map<String,Object>)mergeGroup.get(i);
			// duplicamos la plantilla
			Map<String,Object> formOcurrence = new LinkedHashMap<>();
			formOcurrence.putAll(ocurrenceTemplate);
			//hacemos el merge	
			formOcurrence.putAll(mergeOcurrence);
			//añadimos al grupo resultante
			formGroup.add(i,formOcurrence);
			if(mergeOcurrence.containsKey(GROUP_INDEX_KEY) && mergeOcurrence.get(GROUP_INDEX_KEY) instanceof Integer){
				Integer index = (Integer) mergeOcurrence.get(Form.GROUP_INDEX_KEY);
				if (index == -1) {
					existPlantilla = true;
				}
			}
		}
		if(!existPlantilla) {
			formGroup.add(0, ocurrenceTemplate);
		}
		return formGroup;
	}

	protected boolean checkGroupOcurrences(ArrayList<Object> array) {
		if(array==null || array.isEmpty()) return false;
		Object firstElement = array.get(0);
		if(firstElement instanceof Map<?,?>) {
			return true;
		} else {
			return false;
		}
	}


	
	/*--------------------------------------------------------------*
	 * Redondeo final para evitar excesivos decimales
	 *--------------------------------------------------------------*/
	protected Double roundDecimalValue(Double value) {
		Double decimalPositionsPower = Math.pow(10.0,this.decimalPrecision);
		return Double.valueOf(Math.round(value*decimalPositionsPower)/decimalPositionsPower);
	}

	public void roundAllDecimals() {
		// rounding is computed with every calculation, no needed this final processing.
		if (!LAZY_DECIMAL_ROUNDING) return;

		LoggerHelper.log(Level.INFO, () -> String.format("rouding decimals of form %s in lazy mode to %d decimal positions", this.code, this.decimalPrecision));

		if(formData==null) return;
		Double decimalPositionsPower = Math.pow(10.0,this.decimalPrecision);
		this.formData=recursiveRoundAllDecimalsInMap(formData, decimalPositionsPower);
	}

	protected Map<String,Object> recursiveRoundAllDecimalsInMap(Map<String,Object> map, Double decimalPositionsPower) {
			if(map==null) return null;
			Map<String,Object> result = new LinkedHashMap<>(map.size());
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				Object value = entry.getValue();
				String key = entry.getKey();
				if(value==null) {
					result.put(key,null);
				} else if(value instanceof Map<?,?>) {
					@SuppressWarnings("unchecked")
					Map<String,Object> mapValue = (Map<String,Object>) value;
					result.put(key,recursiveRoundAllDecimalsInMap(mapValue, decimalPositionsPower));
				} else {
					Double decimal = (Double.class.isInstance(value)) ? Double.class.cast(value) : null;
					if(decimal!=null) {
						result.put(key, roundDecimal(decimal, decimalPositionsPower));
					} else {
						result.put(key, value);						
					}
				}
			}
			return result;
		}

	static public Double roundDecimal(Double decimal, Double decimalPositionsPower) {
		return Double.valueOf(Math.round(decimal*decimalPositionsPower)/decimalPositionsPower);
	}
	
	/*--------------------------------------------------------------*
	 * Internal Data Access
	 *--------------------------------------------------------------*/
	protected static Object getFormObject(Map<String, Object> data, String code) {
		if(data==null || code==null || code.isEmpty()) return null;
		return data.get(code);
	}

	@SuppressWarnings("unchecked")
	protected Map<String, Object> getFormContainer(Map<String, Object> data, String code) {
		if(data==null || code==null || code.isEmpty()) return null;
		Object obj = data.get(code);
		if(obj==null) {
			return null;
		}
		if(obj instanceof Map<?, ?>) {
			return (Map<String, Object>) obj;
		} else {
			return null;
		}
	}
	
	protected Map<String, Object> getPage(String pageCode, String ruleName) throws NonExistingFormItemException {
		Map<String, Object> page = getFormContainer(this.formData,pageCode);
		if(page==null) generateNonExistingFormItemException(pageCode, null, null, null, null, null, ruleName);
		return page;
	}

	protected Map<String, Object> getSection(Map<String, Object> page, String pageCode, String sectionCode, String ruleName) throws NonExistingFormItemException {
		Map<String, Object> section = getFormContainer(page,sectionCode);
		if(section==null) generateNonExistingFormItemException(pageCode, sectionCode, null, null, null, null, ruleName);
		return section;
	}
	
	protected Object getQuestion(Map<String, Object> container, String containerCode, String questionCode, String ruleName) throws NonExistingFormItemException, NonAnsweredQuestionException {
		if(!container.containsKey(questionCode)) {
			StringBuilder str = new StringBuilder();
			str.append(Constant.QUESTION).append(Constant.BLANK).append(questionCode).append(" not found in ").append(containerCode).append(Constant.BLANK).append(Constant.IN_FORM).append(Constant.BLANK).append(this.code);
			if(ruleName!=null && !ruleName.isEmpty()) {
				str.append(Constant.DOT_BLANK).append(Constant.ACCESSED_IN_RULE).append(Constant.BLANK).append(ruleName);
			}
			throw new NonExistingFormItemException(str.toString(),Level.WARNING, getIdReferencePageElement(containerCode, questionCode));
		}
		Object question = getFormObject(container,questionCode);
		if(question==null) {
			StringBuilder str = new StringBuilder();
			str.append(Constant.QUESTION).append(Constant.BLANK).append(questionCode).append(" not answered ")
			   .append(" in ").append(containerCode).append(" of form ").append(this.code);
			if(ruleName!=null && !ruleName.isEmpty()) {
				str.append(Constant.DOT_BLANK).append(Constant.ACCESSED_IN_RULE).append(Constant.BLANK).append(ruleName);
			}
			throw new NonAnsweredQuestionException(str.toString(),Level.WARNING, getIdReferencePageElement(containerCode, questionCode));
		}
		return question;
	}
	
	@SuppressWarnings("unchecked")
	protected List<Map<String, Object>> getGroupMap(Map<String, Object> data, String pageCode, String sectionCode, String groupCode, String ruleName) throws NonExistingFormItemException {
		List<Map<String, Object>> group = new ArrayList<>();
		
		for (Map.Entry<String, Object> entryPage : data.entrySet()) {
			String code = entryPage.getKey();
			Object obj = entryPage.getValue();
			String reference = getIdReferenceElement(pageCode, sectionCode, groupCode, null,null,null);
			LoggerHelper.log(Level.FINE,
					"... get group in " + reference + Constant.BLANK+Constant.TYPE+Constant.BLANK + ((obj == null) ? "null" : obj.getClass().getName()));
			if(code.equals(groupCode) && obj instanceof List) {
				List<Object> occurrenceList = (List<Object>) obj;
				if (!occurrenceList.isEmpty() && occurrenceList.get(0) instanceof Map) {
					List<Map<String, Object>> occurrences = (List<Map<String, Object>>) obj;
					if (occurrences.get(0).containsKey(Form.GROUP_INDEX_KEY)) {
						group = (List<Map<String, Object>>) obj;
					}
				}
			}	
		}
		return group;
	}
	
	protected Map<String, Object> getOccurrenceGroup(List<Map<String, Object>> group, String pageCode, String groupCode, Integer occurrenceIndex,  String ruleName) throws NonExistingFormItemException {		
		for(Map<String, Object> o : group) {
			if(o.containsKey(Form.GROUP_INDEX_KEY)) {
				if(o.get(Form.GROUP_INDEX_KEY) instanceof Integer && occurrenceIndex.equals( (Integer) o.get(Form.GROUP_INDEX_KEY))){
					return o;
				}
			}
		}
		
		return null;
	}

	protected <T> T getTypedQuestion(Map<String, Object> container, String containerCode, String questionCode, String ruleName, Class<T> clazz) throws NonExistingFormItemException, NonAnsweredQuestionException, TypeMismatchException {
		Object objresult = getQuestion(container, containerCode, questionCode, ruleName);
		T result = safeCast(objresult,clazz);
		// If no possible down-casting to target type, throw a TypeMismatchException.
		if(result==null) {
			StringBuilder str = new StringBuilder();
			str.append("wrong type of question ").append(questionCode).append(" in ").append(containerCode)
			   .append(Constant.BLANK).append(Constant.IN_FORM).append(Constant.BLANK).append(this.code)
               .append(". Expected type is ").append(clazz.getName());
			if(ruleName!=null && !ruleName.isEmpty()) {
				str.append(Constant.DOT_BLANK).append(Constant.ACCESSED_IN_RULE).append(Constant.BLANK).append(ruleName);
			}
			throw new TypeMismatchException(str.toString(),Level.WARNING, getIdReferencePageElement(containerCode, questionCode));
		}

		// If down-casting to target type was possible write log (level FINER)
		LoggerHelper.log(Level.FINE, () -> String.format("getting response of question %s.%s as %s = %s", containerCode, questionCode, clazz.getCanonicalName(),result.toString()));
		
		// Esto es porque si el objeto es de tipo Double, FLoat o Integer, se convierte a BigDecimal
		// En este caso, si el puntero es diferente, lo actualizamos en el mapa para evitar nuevas conversiones
		// Ver método safeCast()
		// ANULADO
		if(result!=objresult) {
			//container.put(questionCode, result);
		}
		return result;
	}
	
	protected static <T> T safeCast(Object obj, Class<T> clazz) {
		if(clazz==null || obj==null) return null;
		
		Object result = obj;

		// si el tipo objetivo es Double y se recibe como Integer o Double o Float o BigDecimal, se transforma.
		if(clazz==Double.class) {
			Double decimalresult = null;
			if(Integer.class.isInstance(obj)) {
				Integer intobj = Integer.class.cast(obj);
				decimalresult = Double.valueOf(intobj.doubleValue());
			}  else if(Float.class.isInstance(obj)) {
				Float fobj = Float.class.cast(obj);
				decimalresult = Double.valueOf(fobj.doubleValue());
			}  else if(BigDecimal.class.isInstance(obj)) {
				BigDecimal bdobj = BigDecimal.class.cast(obj);
				decimalresult = bdobj.doubleValue();
			} else {
				decimalresult = Double.class.isInstance(obj) ? Double.class.cast(obj) : null;				
			}
			// cambiamos el número de decimales del BigDecimal creado
			if(decimalresult!=null) {
				result=decimalresult;
			}
		} 
		return clazz.isInstance(result) ? clazz.cast(result) : null;			
	}
	
	private String getIdReferencePageElement(String pageCode, String questionCode){
		return String.format("%s%s%s", pageCode,FormCommand.REFERENCE_SEPARATOR,questionCode);
	}
	private String getIdReferencePageSectionQuestion(String pageCode, String sectionCode, String questionCode){
		return String.format("%s%s%s%s%s", pageCode,FormCommand.REFERENCE_SEPARATOR,sectionCode,FormCommand.REFERENCE_SEPARATOR,questionCode);
	}
	
	private String getIdReferenceElement(String pageCode, String sectionCode, String groupCode, Integer occurrenceIndex, String questionCode, String optionCode){
		StringBuilder sb = new StringBuilder(pageCode);
		if(sectionCode != null) sb.append(FormCommand.REFERENCE_SEPARATOR).append(sectionCode);
		if(groupCode != null) sb.append(FormCommand.REFERENCE_SEPARATOR).append(groupCode);
		if(occurrenceIndex != null) sb.append(FormCommand.REFERENCE_SEPARATOR).append(occurrenceIndex);
		if(questionCode != null) sb.append(FormCommand.REFERENCE_SEPARATOR).append(questionCode);
		if(optionCode != null) sb.append(FormCommand.REFERENCE_SEPARATOR).append(optionCode);
		
		return sb.toString();
	}
	
	// Extrae el número de ocurrencia de la pregunta actual. En caso negativo devuelve null
	public static Integer extraerNumeroOcurrenciaPreguntaActual(String preguntaActual) {
		Integer num = null;
		if(preguntaActual != null && !preguntaActual.isEmpty()) {
			String patron = "\\.(\\d+)\\.";
			Pattern pattern = Pattern.compile(patron);
			Matcher matcher = pattern.matcher(preguntaActual);
			if(matcher.find()) {
					num = Integer.parseInt(matcher.group(1));
			}
		}		
		return num;
	}

	/*--------------------------------------------------------------*
	 * Debug utilities
	 *--------------------------------------------------------------*/
	static protected void imprimirMapa(String message, Map<String, Object> mapa) {
	    System.out.println("******* MAPA: "+message+" ****");
		for (Map.Entry<String, Object> entry : mapa.entrySet()) {
		    System.out.println("* clave=" + entry.getKey() + ", valor=" + entry.getValue());
		}
		
	}
	/*--------------------------------------------------------------*
	 * Attachments
	 *--------------------------------------------------------------*/
	@SuppressWarnings("unchecked")
	public void createAttachments() {
		if(!this.formData.containsKey(ATTACHMENTS_PROPERTY_NAME)) {
			return;
		}
		Object obj = this.formData.get(ATTACHMENTS_PROPERTY_NAME);
		if(obj instanceof ArrayList<?>) {
			this.attachments = (ArrayList<Map<String,Object>>) obj;
		}
	}
	
	@JsonIgnore
	public int getAttachmentsSize() {
		int count = 0;
		if(this.attachments != null) count = this.attachments.size();
		return count;
	}
	
	/*--------------------------------------------------------------*
	 * CurrentQuestion
	 *--------------------------------------------------------------*/
	@JsonIgnore
	private Set<String> currentQuestions = new HashSet<>();
	
	@JsonIgnore
	private Set<String> getCurrentQuestions() {
		return currentQuestions;
	}
	
	public void addCurrentQuestions(String currentQuestion) {
		if(!currentQuestions.contains(currentQuestion)) {
			currentQuestions.add(currentQuestion);
		}
	}

	public boolean checkCurrentQuestions(String currentQuestion) {
		if(currentQuestion==null) {
			return false; 
		} else {
			return currentQuestions.contains(currentQuestion);
		}
	}
	
	/*--------------------------------------------------------------*
	 * Exceptions
	 *--------------------------------------------------------------*/
	
	public void generateButtonNonExistingFormItemException(String buttonCode, String ruleName) throws NonExistingFormItemException {
		StringBuilder str = new StringBuilder();
		str.append("Button ").append(buttonCode);
		str.append(" not found. In form ").append(this.code).append(". ");
	
		if(StringHelper.isNotNullAndNotEmpty(ruleName)) {
			str.append("Accessed in rule ").append(ruleName);
		}
		throw new NonExistingFormItemException(str.toString(),Level.WARNING, buttonCode);
	}
	
	public void generateNonExistingFormItemException(String pageCode, String sectionCode, String groupCode, Integer occurrenceIndex, String questionCode, String optionQuestion, String ruleName) throws NonExistingFormItemException {
		StringBuilder str = new StringBuilder();
		str.append(generateMsgReference(optionQuestion, questionCode, groupCode, sectionCode, pageCode));
		str.append("not found. In form ").append(this.code).append(". ");
	
		if(StringHelper.isNotNullAndNotEmpty(ruleName)) {
			str.append("Accessed in rule ").append(ruleName);
		}
		throw new NonExistingFormItemException(str.toString(),Level.WARNING, getIdReferenceElement(pageCode, sectionCode, groupCode, occurrenceIndex, questionCode, optionQuestion));
	}
	
	/**
	 * Generar referencia para el mensaje de log
	 * @param questionCode
	 * @param sectionCode
	 * @param pageCode
	 * @param ruleName
	 * @return
	 */
	private String generateMsgReference(String pageCode, String sectionCode, String groupCode, String questionCode, String optionCode) {
		StringBuilder str = new StringBuilder();		
		if(StringHelper.isNotNull(pageCode)) {
			if(StringHelper.isNotNull(questionCode)) {
				if(StringHelper.isNotNull(optionCode)) str.append("option ").append(optionCode).append(IN);
				str.append("question ").append(questionCode).append(IN);
			}
			if(StringHelper.isNotNull(sectionCode)) {
				str.append("section ").append(sectionCode).append(IN);
				str.append(msgGroup(groupCode));
			}else {
				str.append(msgGroup(groupCode));
			}
			str.append("page ").append(pageCode).append(Constant.BLANK);
		}
		return str.toString();
	}
	
	private String msgGroup(String groupCode) {
		StringBuilder str = new StringBuilder();		
		if(StringHelper.isNotNull(groupCode))str.append("group ").append(groupCode).append(IN);
		return str.toString();
	}

}
