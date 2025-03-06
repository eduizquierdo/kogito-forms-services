package com.decide.forms.model;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.decide.forms.commands.FormCommand;
import com.decide.forms.commands.FormCommandFeature;
import com.decide.forms.exceptions.DateFormatException;
import com.decide.forms.exceptions.NonAnsweredQuestionException;
import com.decide.forms.exceptions.NonExistingFormItemException;
import com.decide.forms.exceptions.TypeMismatchException;
import com.decide.forms.util.Constant;
import com.decide.forms.util.LoggerHelper;

public class GroupOccurrence {
	
	/*--------------------------------------------------------------*
	 * Bean
	 *--------------------------------------------------------------*/    
    private Group group;
    private Integer index;
    private Map<String,Object> questions;
	public Group getGroup() {
		return group;
	}
	public void setGroup(Group group) {
		this.group = group;
	}
	public Integer getIndex() {
		return index;
	}
	public void setIndex(Integer index) {
		this.index = index;
	}
	public Map<String,Object> getQuestions() {
		return questions;
	}
	public void setQuestions(Map<String,Object> questions) {
		this.questions = questions;
	}

	/*--------------------------------------------------------------*
	 * Constructors
	 *--------------------------------------------------------------*/    
	public GroupOccurrence(Group group, Map<String,Object> questions) {
		this.group = group;
		this.index = (int) questions.get(Form.GROUP_INDEX_KEY);
		this.questions = questions;
	}
    
	/*--------------------------------------------------------------*
	 * BOM Methods
	 *--------------------------------------------------------------*/    

	public Boolean respondidaPregunta(String questionCode, String ruleName) throws NonExistingFormItemException {
		if( questionCode==null || questionCode.isEmpty())  {
			return false;
		}
		return getQuestionResponseWithoutCheck(questionCode, ruleName) != null;
	}

	public Boolean equalsReferencePageGroup(String pageCode, String groupCode) {
		if( pageCode==null || pageCode.isEmpty() ||
		    groupCode==null || groupCode.isEmpty()) {
			// no debería ocurrir nunca
			return false;
		}
			
		String ref = pageCode+FormCommand.REFERENCE_SEPARATOR+groupCode;
		return ref.equals(this.group.getReference());
	}
	
	public Boolean equalsReferenceSectionGroup(String pageCode, String sectionCode, String groupCode) {
		if( pageCode==null || pageCode.isEmpty() ||
		    sectionCode==null || sectionCode.isEmpty() ||
			groupCode==null || groupCode.isEmpty()) {
					// no debería ocurrir nunca
			return false;
		}
					
		String ref = pageCode+FormCommand.REFERENCE_SEPARATOR+sectionCode+FormCommand.REFERENCE_SEPARATOR+groupCode;
		return ref.equals(this.group.getReference());
	}

	public String getQuestionAsString(String questionCode, String ruleName) throws NonExistingFormItemException, NonAnsweredQuestionException, TypeMismatchException {
		return getTypedQuestion(questionCode, ruleName, String.class);
	}

	public Integer getQuestionAsInteger(String questionCode, String ruleName) throws NonExistingFormItemException, NonAnsweredQuestionException, TypeMismatchException {
		return getTypedQuestion(questionCode, ruleName, Integer.class);
	}

	public Double getQuestionAsDouble(String questionCode, String ruleName) throws NonExistingFormItemException, NonAnsweredQuestionException, TypeMismatchException {
		return getTypedQuestion(questionCode, ruleName, Double.class);
	}

	public Boolean getQuestionAsBoolean(String questionCode, String ruleName) throws NonExistingFormItemException, NonAnsweredQuestionException, TypeMismatchException {
		return getTypedQuestion(questionCode, ruleName, Boolean.class);
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getQuestionAsList(String questionCode, String ruleName) throws NonExistingFormItemException, TypeMismatchException, NonAnsweredQuestionException {
		return  getTypedQuestion(questionCode, ruleName, List.class);
	}
	
	//SONAR: Remove this unused method parameter "questionValue"
	//No elimino porque afecta a reglas
	public Date getQuestionAsDate(Object questionValue, String questionCode, String ruleName, String dateFormatPattern) throws NonExistingFormItemException, TypeMismatchException, NonAnsweredQuestionException, DateFormatException {
		String strdate = getTypedQuestion(questionCode, ruleName, String.class);
		
		Date result = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatPattern); 
			result = dateFormat.parse(strdate);
		} catch (Exception e) {
			throw new DateFormatException(getMessageError(questionCode, ruleName, dateFormatPattern),Level.WARNING, String.format("%s%s%s", getReference(), FormCommand.REFERENCE_SEPARATOR, questionCode));
		}		
		return result;
	}


	public FormCommand cmdChangeQuestionFeature(Form form, FormCommandFeature feature, String questionCode, Object value, String ruleName) throws NonExistingFormItemException {
		// check data
		if(questionCode==null || questionCode.isEmpty()) {
			// no puede ocurrir
			LoggerHelper.log(Level.WARNING, String.format("Null question reference while changing feature %s in %s. Ignored",feature,getReference()));
			return null;
		}
		
		LoggerHelper.log(Level.INFO, () -> 
							String.format("changing feature %s of question %s to %s",
									      feature,this.getQuestionReference(questionCode),value.toString()));

		if(feature==FormCommandFeature.RESPONSE) {
			// change the response in the occurrence
			setQuestionResponse(questionCode, value, ruleName);
		} else if(feature==FormCommandFeature.CLEAN) {
			// change the response in the occurrence to null
			Boolean accion = false;
			if(value instanceof Boolean) accion = (Boolean) value;
			else {
				LoggerHelper.log(Level.WARNING, String.format("Wrong value while changing feature %s in %s. Assuming false",feature,getQuestionReference(questionCode)));
				accion = false;
			}
			if(Boolean.TRUE.equals(accion)) {
				setQuestionResponse(questionCode, null, ruleName);
			}
		}else {
			//Añadir exception nueva "NoSupportedFeature"
		}
		String reference = getQuestionReference(questionCode);
		
		form.addCurrentQuestions(reference);
		
		// creamos el comando y lo devolvemos
		LoggerHelper.log(Level.FINE, () -> "Form Command created");
		return new FormCommand(reference, feature, value);
	}

	public FormCommand cmdChangeQuestionFeatureWithMessage(Form form, FormCommandFeature feature, String questionCode, Object value, String message,  String ruleName) throws NonExistingFormItemException {
	
		FormCommand command =  cmdChangeQuestionFeature(form, feature, questionCode, value, ruleName);
		if(command != null && message!=null) {
			command.setMessage(message);
		}
		return command;
	}

	/*--------------------------------------------------------------*
	 * Internal utilities
	 *--------------------------------------------------------------*/    
	// occurrence reference = page.group.n
	protected String getReference() {
		StringBuilder str = new StringBuilder();
		str.append(this.group.getReference()).append(FormCommand.REFERENCE_SEPARATOR).append(this.index);    
		return str.toString();
	}

	// question occurrence reference = page.group.n.question
	protected String getQuestionReference(String questionCode) {
		if(questionCode==null || questionCode.isEmpty()) return null;
		
		StringBuilder str = new StringBuilder();
		str.append(this.getReference()).append(FormCommand.REFERENCE_SEPARATOR).append(questionCode);    
		return str.toString();
		
	}
	
	protected void setQuestionResponse(String questionCode, Object value, String ruleName) throws NonExistingFormItemException {
		if(checkContainsQuestion(questionCode, ruleName)) {
			this.questions.put(questionCode, value);
		}
	}

	protected boolean checkContainsQuestion(String questionCode, String ruleName) throws NonExistingFormItemException {
		if(!questions.containsKey(questionCode)) {
			StringBuilder str = new StringBuilder();
			str.append(Constant.QUESTION).append(Constant.BLANK).append(getQuestionReference(questionCode)).append(" not found in group occurrence.");
			if(ruleName!=null && !ruleName.isEmpty()) {
				str.append(Constant.DOT_BLANK).append(Constant.ACCESSED_IN_RULE).append(Constant.BLANK).append(ruleName);
			}
			String msg = str.toString();
			LoggerHelper.log(Level.WARNING, msg);
			throw new NonExistingFormItemException(msg,Level.WARNING, getQuestionReference(questionCode));
		}
		return true;
	}
	
	protected <T> T getTypedQuestion(String questionCode, String ruleName, Class<T> clazz) throws NonExistingFormItemException, NonAnsweredQuestionException, TypeMismatchException {
		Object objresult = getQuestionResponse(questionCode, ruleName);
		T result = safeCast(objresult,clazz);
		String questionReference = getQuestionReference(questionCode);
		// If no possible down-casting to target type, throw a TypeMismatchException.
		if(result==null) {
			StringBuilder str = new StringBuilder();
			str.append("wrong type of question ").append(questionReference)
               .append(". Expected type is ").append(clazz.getName());
			if(ruleName!=null && !ruleName.isEmpty()) {
				
				str.append(Constant.DOT_BLANK).append(Constant.ACCESSED_IN_RULE).append(Constant.BLANK).append(ruleName);
			}
			throw new TypeMismatchException(str.toString(),Level.WARNING, questionReference);
		}

		// If down-casting to target type was possible write log (level FINER)
		LoggerHelper.log(Level.FINE, () -> String.format("getting response of group question %s as %s = %s", questionReference, clazz.getCanonicalName(),result.toString()));
		
		return result;
	}

	protected Object getQuestionResponseWithoutCheck(String questionCode, String ruleName) throws NonExistingFormItemException {
		String questionReference = getQuestionReference(questionCode);
		if(!this.questions.containsKey(questionCode)) {
			StringBuilder str = new StringBuilder();
			str.append(Constant.QUESTION).append(Constant.BLANK).append(questionReference).append(" not found in group ocurrence.");
			if(ruleName!=null && !ruleName.isEmpty()) {
				str.append(Constant.DOT_BLANK).append(Constant.ACCESSED_IN_RULE).append(Constant.BLANK).append(ruleName);
			}
			throw new NonExistingFormItemException(str.toString(),Level.WARNING, getQuestionReference(questionCode));
		}
		return this.questions.get(questionCode);
	}

	protected Object getQuestionResponse(String questionCode, String ruleName) throws NonExistingFormItemException, NonAnsweredQuestionException {
		String questionReference = getQuestionReference(questionCode);
		if(!this.questions.containsKey(questionCode)) {
			StringBuilder str = new StringBuilder();
			str.append(Constant.QUESTION).append(Constant.BLANK).append(questionReference).append(" not found in group ocurrence.");
			if(ruleName!=null && !ruleName.isEmpty()) {
				str.append(Constant.DOT_BLANK).append(Constant.ACCESSED_IN_RULE).append(Constant.BLANK).append(ruleName);
			}
			throw new NonExistingFormItemException(str.toString(),Level.WARNING, getQuestionReference(questionCode));
		}
		Object response = this.questions.get(questionCode);
		if(response==null) {
			StringBuilder str = new StringBuilder();
			str.append(Constant.QUESTION).append(Constant.BLANK).append(questionReference).append(" not answered in group ocurrence.");
			if(ruleName!=null && !ruleName.isEmpty()) {
				str.append(Constant.DOT_BLANK).append(Constant.ACCESSED_IN_RULE).append(Constant.BLANK).append(ruleName);
			}
			throw new NonAnsweredQuestionException(str.toString(),Level.WARNING, questionReference);
		}
		return response;
	}
	
	protected static <T> T safeCast(Object obj, Class<T> clazz) {
		if (clazz == null || obj == null)
			return null;

		Object result = obj;

		// si el tipo objetivo es Double y se recibe como Integer o Double o Float o
		// BigDecimal, se transforma.
		if (clazz == Double.class) {
			Double decimalresult = null;
			if (obj instanceof Integer) {
				Integer intobj = Integer.class.cast(obj);
				decimalresult = Double.valueOf(intobj.doubleValue());
			} else if (obj instanceof Float) {
				Float fobj = Float.class.cast(obj);
				decimalresult = Double.valueOf(fobj.doubleValue());
			} else if (obj  instanceof BigDecimal) {
				BigDecimal bdobj = BigDecimal.class.cast(obj);
				decimalresult = bdobj.doubleValue();
			} else {
				decimalresult = obj instanceof Double ? Double.class.cast(obj) : null;
			}
			// cambiamos el número de decimales del BigDecimal creado
			if (decimalresult != null) {
				result = decimalresult;
			}
		}
		return clazz.isInstance(result) ? clazz.cast(result) : null;
	}
	
	protected String getMessageError(String questionCode, String ruleName, String dateFormatPattern) {
		StringBuilder str = new StringBuilder();
		str.append("wrong date format in question ").append(questionCode).append(" in ").append(getReference());
		if(dateFormatPattern!=null && !dateFormatPattern.isEmpty()) {
			str.append(". Expected format: ").append(dateFormatPattern);
		}
		if(ruleName!=null && !ruleName.isEmpty()) {
			str.append(Constant.DOT_BLANK).append(Constant.ACCESSED_IN_RULE).append(Constant.BLANK).append(ruleName);
		}
		return str.toString();
	}
}
