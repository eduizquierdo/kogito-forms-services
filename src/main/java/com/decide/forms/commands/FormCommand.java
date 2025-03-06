package com.decide.forms.commands;

import java.util.logging.Level;

import com.decide.forms.exceptions.NonExistingFormItemException;
import com.decide.forms.model.Form;
import com.decide.forms.util.LoggerHelper;
import com.decide.forms.util.StringBuilderHelper;
import com.decide.forms.util.StringHelper;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FormCommand {

	/*--------------------------------------------------------------*
	 * Static data
	 *--------------------------------------------------------------*/
	public static final String REFERENCE_SEPARATOR = ".";
	public static final String REFERENCE_OPTION_SEPARATOR = ".";
	
	private static final String MSG_LOG_CHANGING_FEATURE_QUESTION = "changing feature %s of question %s in form %s to value %s";
	private static final String MSG_LOG_CHANGING_FEATURE_BUTTON = "changing feature %s of button %s in form %s to value %s in %s";

	/*--------------------------------------------------------------*
	 * Bean data
	 *--------------------------------------------------------------*/
	@JsonProperty("reference")
	private String reference;
	@JsonProperty("reference")
	public String getReference() {
		return reference;
	}
	@JsonProperty("reference")
	public void setReference(String reference) {
		this.reference = reference;
	}

	@JsonProperty("feature")
	private FormCommandFeature feature;
	@JsonProperty("feature")
	public FormCommandFeature getFeature() {
		return feature;
	}
	@JsonProperty("feature")
	public void setFeature(FormCommandFeature feature) {
		this.feature = feature;
	}

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

	@JsonProperty("message")
	private String message;
	@JsonProperty("message")
	public String getMessage() {
		return message;
	}
	@JsonProperty("message")
	public void setMessage(String message) {
		this.message = message;
	}

	/*--------------------------------------------------------------*
	 * Constructors
	 *--------------------------------------------------------------*/
	public FormCommand() {
		super();
		this.reference = null;
		this.feature = null;
		this.value = false;
		this.message = null;

	}
	protected FormCommand(String reference, FormCommandFeature feature) {
		super();
		this.reference = reference;
		this.feature = feature;
		this.value = null;
		this.message = null;
	}
	
	public FormCommand(String reference, FormCommandFeature feature, Object value) {
		super();
		this.reference = reference;
		this.feature = feature;
		this.value = value;
		this.message = null;
	}

	/*--------------------------------------------------------------*
	 * Static methods to support b2x actions in the BOM
	 *--------------------------------------------------------------*/

			/*-------------------------------------*
			 * With section
			 *-------------------------------------*/
	public static FormCommand cmdChangeFeatureSection(Form form, String pageCode, String sectionCode, String questionCode, String optionCode, FormCommandFeature feature, Object value, String ruleName) throws NonExistingFormItemException {
		LoggerHelper.log(Level.FINE, () -> 
			String.format("changing feature %s of question %s.%s.%s in form %s to value %s",feature, pageCode, sectionCode, questionCode, form.getCode(), value.toString()));
		
		String reference = getSectionReference(form, pageCode, sectionCode, questionCode, optionCode, ruleName); 
		
		if(feature==FormCommandFeature.RESPONSE) {
			form.setSectionQuestion(pageCode, sectionCode, questionCode,value, ruleName);
		} else if(feature==FormCommandFeature.CLEAN && questionCode!=null) {
			form.cleanSectionQuestion(pageCode, sectionCode, questionCode,ruleName);
		} else if(feature==FormCommandFeature.CLEAN) {
			form.cleanSection(pageCode, sectionCode, ruleName);
		}
		
		form.addCurrentQuestions(reference);
		
		if (form.getFormLayout()!=null) {
			form.changeFeatureFormLayout(pageCode, sectionCode, questionCode, optionCode, null, feature, value);
			return null;
		} else
			return new FormCommand(reference, feature, value);
	}
	
	public static FormCommand cmdChangeFeatureSectionWithMessage(Form form, String pageCode, String sectionCode, String questionCode, String optionCode, FormCommandFeature feature, Object value, String message, String ruleName) throws NonExistingFormItemException {
		LoggerHelper.log(Level.FINE, () -> 
			String.format(MSG_LOG_CHANGING_FEATURE_QUESTION, 
					      feature, questionCode, form.getCode(), value.toString()));
		FormCommand command =  cmdChangeFeatureSection(form, pageCode, sectionCode, questionCode, optionCode, feature, value, ruleName);
		if(command != null && message!=null) {
			command.setMessage(message);
		}
		return command;
	}
			/*-------------------------------------*
			 * Without section
			 *-------------------------------------*/
		
	public static FormCommand cmdChangeFeature(Form form, String pageCode, String questionCode, String optionCode, String buttonCode, FormCommandFeature feature, Object value, String ruleName) throws NonExistingFormItemException {
		LoggerHelper.log(Level.FINE, () -> 
			String.format(MSG_LOG_CHANGING_FEATURE_QUESTION, feature, questionCode, form.getCode(), value.toString()));

		String reference = getReference(form, pageCode, questionCode, optionCode, buttonCode, ruleName);

		if(feature==FormCommandFeature.RESPONSE) {
			form.setQuestion(pageCode, questionCode,value, ruleName);
		} else if(feature==FormCommandFeature.CLEAN && questionCode!=null)  {
			form.cleanPageQuestion(pageCode, questionCode,ruleName);			
		} 
		
		form.addCurrentQuestions(reference);
		
		if (form.getFormLayout()!=null) {
			form.changeFeatureFormLayout(pageCode, null, questionCode, optionCode, buttonCode, feature, value);
			return null;
		} else
			return new FormCommand(reference, feature, value);
	}
	
	public static FormCommand cmdChangeFeatureWithMessage(Form form, String pageCode, String questionCode, String optionCode, String buttonCode, FormCommandFeature feature, Object value, String message, String ruleName) throws NonExistingFormItemException {
		LoggerHelper.log(Level.FINE, () -> 
			String.format(MSG_LOG_CHANGING_FEATURE_QUESTION, feature, questionCode, form.getCode(), value.toString()));

		FormCommand command =  cmdChangeFeature(form, pageCode, questionCode, optionCode, buttonCode, feature, value, ruleName);
		if(command != null && message!=null) {
			command.setMessage(message);
		}
		return command;
	}

	public static FormCommand cmdChangeFeaturePageButton(Form form, String pageCode, String buttonCode, FormCommandFeature feature, Object value, String ruleName) throws NonExistingFormItemException {
		LoggerHelper.log(Level.FINE, () -> 
			String.format(MSG_LOG_CHANGING_FEATURE_BUTTON, feature, buttonCode, form.getCode(), value.toString(),ruleName ));

		String reference = getRefPageButton(pageCode, buttonCode);
		
		if (form.getFormLayout()!=null) {
			form.changeFeatureFormLayout(pageCode, null, null, null, buttonCode, feature, value);
			return null;
		} else
			return new FormCommand(reference, feature, value);
	}
	

	public static FormCommand cmdChangeFeaturePageQuestionButton(Form form, String pageCode, String questionCode, String buttonCode, FormCommandFeature feature, Object value, String ruleName) throws NonExistingFormItemException {
		LoggerHelper.log(Level.FINE, () -> 
			String.format(MSG_LOG_CHANGING_FEATURE_BUTTON, feature, buttonCode, form.getCode(), value.toString(),ruleName));

		String reference = getRefPageButton(pageCode, buttonCode);
		
		if (form.getFormLayout()!=null) {
			form.changeFeatureFormLayout(pageCode, null, questionCode, null, buttonCode, feature, value);
			return null;
		} else
			return new FormCommand(reference, feature, value);
	}
	
	/*--------------------------------------------------------------*
	 * Internal Utilities
	 *--------------------------------------------------------------*/
	protected static String getReference(Form form, String pageCode, String questionCode, String optionCode, String buttonCode, String ruleName) throws NonExistingFormItemException {
		String reference = "";
		
		if(optionCode != null) {
			reference = String.format(StringHelper.generateStringFormatPattern(5), pageCode,REFERENCE_SEPARATOR,questionCode,REFERENCE_OPTION_SEPARATOR,optionCode);
		} else if(buttonCode != null) {
			reference = String.format(StringHelper.generateStringFormatPattern(5),pageCode,REFERENCE_SEPARATOR,questionCode,REFERENCE_SEPARATOR,buttonCode);
		} else {
			reference = String.format(StringHelper.generateStringFormatPattern(3), pageCode,REFERENCE_SEPARATOR,questionCode);
		}

		if(!form.existPageQuestion(pageCode, questionCode, ruleName)) {
			throw new NonExistingFormItemException(logNonExistingFormItemException(ruleName, form, reference),Level.WARNING, reference);
		}
		
		return reference;
	}

	protected static String getSectionReference(Form form, String pageCode, String sectionCode, String questionCode, String optionCode, String ruleName) throws NonExistingFormItemException {
		String reference = "";
		
		if(optionCode != null) {
			reference = String.format(StringHelper.generateStringFormatPattern(7), pageCode,REFERENCE_SEPARATOR,sectionCode,REFERENCE_SEPARATOR,questionCode,REFERENCE_OPTION_SEPARATOR,optionCode);
			if(!form.existSectionQuestion(pageCode, sectionCode, questionCode, ruleName)) {
				throw new NonExistingFormItemException(logNonExistingFormItemException(ruleName, form, reference),Level.WARNING,reference);
			}
		} else if(questionCode != null) {
			reference = String.format(StringHelper.generateStringFormatPattern(5), pageCode,REFERENCE_SEPARATOR,sectionCode,REFERENCE_SEPARATOR,questionCode);
			if(!form.existSectionQuestion(pageCode, sectionCode, questionCode, ruleName)) {
				throw new NonExistingFormItemException(logNonExistingFormItemException(ruleName, form, reference),Level.WARNING,reference);
			}
		} else {
			reference = String.format(StringHelper.generateStringFormatPattern(3), pageCode,REFERENCE_SEPARATOR, sectionCode);
			if(!form.existSection(pageCode, sectionCode, ruleName)) {
				throw new NonExistingFormItemException(logNonExistingFormItemException(ruleName, form, reference),Level.WARNING,reference);
			}
		}			
		
		return reference;
	}
	
	protected static String getReferenceAll(Form form, String pageCode, String sectionCode, String questionCode, String optionCode, String buttonCode, String ruleName) throws NonExistingFormItemException {
		
		StringBuilder reference = new StringBuilder();
		StringBuilderHelper.addIfNotNullAndSeparator(reference,pageCode,REFERENCE_SEPARATOR);
		StringBuilderHelper.addIfNotNullAndSeparator(reference,sectionCode,REFERENCE_SEPARATOR);
		StringBuilderHelper.addIfNotNullAndSeparator(reference,questionCode,REFERENCE_SEPARATOR);
		StringBuilderHelper.addIfNotNullAndSeparator(reference,optionCode,REFERENCE_SEPARATOR);
		StringBuilderHelper.addIfNotNullAndSeparator(reference,buttonCode,REFERENCE_SEPARATOR);
		reference.deleteCharAt(reference.length() - 1);
		if(questionCode != null) {
			if(sectionCode != null) {
				if(!form.existSectionQuestion(pageCode, sectionCode, questionCode, ruleName)) {
					throw new NonExistingFormItemException(logNonExistingFormItemException(ruleName, form, reference.toString()),Level.WARNING,reference.toString());
				}	
			}else{
				if(!form.existPageQuestion(pageCode, questionCode, ruleName)) {
					throw new NonExistingFormItemException(logNonExistingFormItemException(ruleName, form, reference.toString()),Level.WARNING, reference.toString());
				}
			}
		}
		return reference.toString();
	}
	
	private static String logNonExistingFormItemException(String ruleName, Form form, String reference) {
		StringBuilder str = new StringBuilder();
		str.append("reference ").append(reference).append(" not found in form ").append(form.getCode());
		if(ruleName!=null && !ruleName.isEmpty()) {
			str.append(". Accessed in rule ").append(ruleName);
		}
		return str.toString();
	}
	
	protected static String getRefPageButton(String pageCode, String buttonCode) {
		return String.format(StringHelper.generateStringFormatPattern(3), pageCode,REFERENCE_SEPARATOR,buttonCode);
	}
	
	public static FormCommand cmdChangeCallBackQuestion(Form form, String pageCode, String sectionCode, String questionCode, String value) throws NonExistingFormItemException {
		String reference = getReferenceAll(form, pageCode, sectionCode, questionCode, null, null, null); 
		
		LoggerHelper.log(Level.FINE, () -> 
		String.format("changing callback of question %s in form %s to value %s", reference, form.getCode(), value));
		

		if (form.getFormLayout()!=null) {
			form.changeFeatureFormLayout(pageCode, sectionCode, questionCode, null, null, FormCommandFeature.CHANGE_CALLBACK, value);
			return null;
		} else
			return new FormCommand(reference, FormCommandFeature.CHANGE_CALLBACK, value);
	}
	
	public static FormCommand cmdChangeHelperTextQuestion(Form form, String pageCode, String sectionCode, String questionCode, String value) throws NonExistingFormItemException {
		String reference = getReferenceAll(form, pageCode, sectionCode, questionCode, null, null, null); 
		
		LoggerHelper.log(Level.FINE, () -> 
		String.format("changing Helper text of question %s in form %s to value %s", reference, form.getCode(), value));
		
		if (form.getFormLayout()!=null) {
			form.changeFeatureFormLayout(pageCode, sectionCode, questionCode, null, null, FormCommandFeature.CHANGE_HELPER_TEXT, value);
			return null;
		} else
			return new FormCommand(reference, FormCommandFeature.CHANGE_HELPER_TEXT, value);
	}
	
	public static FormCommand cmdChangeTooltipQuestion(Form form, String pageCode, String sectionCode, String questionCode, String value) throws NonExistingFormItemException {
		String reference = getReferenceAll(form, pageCode, sectionCode, questionCode, null, null, null); 
		
		LoggerHelper.log(Level.FINE, () -> 
		String.format("changing tooltip of question %s in form %s to value %s", reference, form.getCode(), value));

		if (form.getFormLayout()!=null) {
			form.changeFeatureFormLayout(pageCode, sectionCode, questionCode, null, null, FormCommandFeature.CHANGE_TOOLTIP, value);
			return null;
		} else
			return new FormCommand(reference, FormCommandFeature.CHANGE_TOOLTIP, value);
	}
	
	public static FormCommand cmdChangeActionButtonPage(Form form, String pageCode, String buttonCode, String value) throws NonExistingFormItemException {
		String reference = getReferenceAll(form, pageCode, null, null, null, buttonCode, null); 
		
		LoggerHelper.log(Level.FINE, () -> 
		String.format("changing action of button %s in form %s to value %s", reference, form.getCode(), value));
		if (form.getFormLayout()!=null) {
			form.changeFeatureFormLayout(pageCode, null, null, null, buttonCode, FormCommandFeature.CHANGE_ACTION, value);
			return null;
		} else
			return new FormCommand(reference, FormCommandFeature.CHANGE_ACTION, value);
	}
	public static FormCommand cmdChangeLabelButtonPage(Form form, String pageCode, String buttonCode, String value) throws NonExistingFormItemException {
		String reference = getReferenceAll(form, pageCode, null, null, null, buttonCode, null); 
		
		LoggerHelper.log(Level.FINE, () -> 
		String.format("changing label of button %s in form %s to value %s", reference, form.getCode(), value));
		if (form.getFormLayout()!=null) {
			form.changeFeatureFormLayout(pageCode, null, null, null, buttonCode, FormCommandFeature.CHANGE_LABEL, value);
			return null;
		} else
			return new FormCommand(reference, FormCommandFeature.CHANGE_LABEL, value);
	}

}
