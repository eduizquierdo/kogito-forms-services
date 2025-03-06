package com.decide.forms.model;

import java.util.ArrayList;
import java.util.List;

import com.decide.forms.commands.FormCommand;
import com.decide.forms.exceptions.NonAnsweredQuestionException;
import com.decide.forms.exceptions.NonExistingFormItemException;
import com.decide.forms.exceptions.TypeMismatchException;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class Group {
	/*--------------------------------------------------------------*
	 * Bean
	 *--------------------------------------------------------------*/    
	private String reference;
    public String getReference() {
        return reference;
    }
    public void setReference(String reference) {
        this.reference = reference;
    }

    private List<GroupOccurrence> occurrences;
    public List<GroupOccurrence> getOccurrences() {
        return occurrences;
    }
    public void setOccurrences(List<GroupOccurrence> occurrences) {
        this.occurrences = occurrences;
    }

    /*--------------------------------------------------------------*
	 * Constructors
	 *--------------------------------------------------------------*/    
	public Group(String reference) {
		this.reference = reference;
		this.occurrences = new ArrayList<>();
	}

    /*--------------------------------------------------------------*
	 * BOM methods
	 *--------------------------------------------------------------*/    
	public Boolean equalsReferencePageGroup(String pageCode, String groupCode) {
		if( pageCode==null || pageCode.isEmpty() ||
		    groupCode==null || groupCode.isEmpty()) {
			// no debería ocurrir nunca
			return false;
		}
			
		String ref = pageCode+FormCommand.REFERENCE_SEPARATOR+groupCode;
		return ref.equals(this.reference);
	}
	
	public Boolean equalsReferenceSectionGroup(String pageCode, String sectionCode, String groupCode) {
		if( pageCode==null || pageCode.isEmpty() ||
		    sectionCode==null || sectionCode.isEmpty() ||
			groupCode==null || groupCode.isEmpty()) {
					// no debería ocurrir nunca
			return false;
		}
		String ref = pageCode+FormCommand.REFERENCE_SEPARATOR+sectionCode+FormCommand.REFERENCE_SEPARATOR+groupCode;
		return ref.equals(this.reference);
	}

	public Integer getNumberOfOccurrences() {
		if (this.occurrences==null) return 0;
		else return this.occurrences.size();
	}
	
	public Integer sumIntegerQuestionResponses(String questionCode, String ruleName) throws NonExistingFormItemException, TypeMismatchException {
		Integer sum = 0;
		for(GroupOccurrence occurrence: this.occurrences) {
			try {
				sum += occurrence.getQuestionAsInteger(questionCode, ruleName);
			} catch (NonExistingFormItemException|TypeMismatchException e) {
				throw e;
			} catch (NonAnsweredQuestionException e) {
				// do nothing, question response is not added
			}
		}
		
		return sum;
	}
	
	public Integer maxIntegerQuestionResponses(String questionCode, String ruleName) throws NonExistingFormItemException, TypeMismatchException {
		Integer max= Integer.MIN_VALUE;
		for(GroupOccurrence occurrence: this.occurrences) {
			try {
				Integer current = occurrence.getQuestionAsInteger(questionCode, ruleName);
				if(current > max) max = current;
			} catch (NonExistingFormItemException|TypeMismatchException e) {
				throw e;
			} catch (NonAnsweredQuestionException e) {
				// do nothing, question response is not taken into account for max calculation
			}
		}
		
		return max;
	}

	public Integer minIntegerQuestionResponses(String questionCode, String ruleName) throws NonExistingFormItemException, TypeMismatchException {
		Integer min= Integer.MAX_VALUE;
		for(GroupOccurrence occurrence: this.occurrences) {
			try {
				Integer current = occurrence.getQuestionAsInteger(questionCode, ruleName);
				if(current < min) min = current;
			} catch (NonExistingFormItemException|TypeMismatchException e) {
				throw e;
			} catch (NonAnsweredQuestionException e) {
				// do nothing, question response is not taken into account for min calculation
			}
		}
		
		return min;
	}

	public Double sumDoubleQuestionResponses(String questionCode, String ruleName) throws NonExistingFormItemException, TypeMismatchException {
		Double sum = 0.0;
		for(GroupOccurrence occurrence: this.occurrences) {
			try {
				sum += occurrence.getQuestionAsDouble(questionCode, ruleName);
			} catch (NonExistingFormItemException e) {
				throw e;
			} catch (NonAnsweredQuestionException|TypeMismatchException e) {
				// do nothing, question response is not added
			}
		}
		
		return sum;
	}
	
	public Double maxDoubleQuestionResponses(String questionCode, String ruleName) throws NonExistingFormItemException, TypeMismatchException {
		Double max= Double.MIN_VALUE;
		for(GroupOccurrence occurrence: this.occurrences) {
			try {
				Double current = occurrence.getQuestionAsDouble(questionCode, ruleName);
				if(current > max) max = current;
			} catch (NonExistingFormItemException|TypeMismatchException e) {
				throw e;
			} catch (NonAnsweredQuestionException e) {
				// do nothing, question response is not taken into account for max calculation
			}
		}
		
		return max;
	}

	public Double minDoubleQuestionResponses(String questionCode, String ruleName) throws NonExistingFormItemException, TypeMismatchException {
		Double min= Double.MAX_VALUE;
		for(GroupOccurrence occurrence: this.occurrences) {
			try {
				Double current = occurrence.getQuestionAsDouble(questionCode, ruleName);
				if(current < min) min = current;
			} catch (NonExistingFormItemException|TypeMismatchException e) {
				throw e;
			} catch (NonAnsweredQuestionException e) {
				// do nothing, question response is not taken into account for min calculation
			} 
		}
		
		return min;
	}

    /*--------------------------------------------------------------*
	 * Utilities
	 *--------------------------------------------------------------*/    
    public void addOcurrence(GroupOccurrence occurrence) {
    	if(occurrences == null) this.occurrences = new ArrayList<>();
    	this.occurrences.add(occurrence);
    }

}
