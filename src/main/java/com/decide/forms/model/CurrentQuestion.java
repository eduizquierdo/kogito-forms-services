package com.decide.forms.model;

public class CurrentQuestion {

    private String reference;

    public CurrentQuestion(String reference) {
        this.reference = reference;
    }

    public CurrentQuestion() {
        this.reference = null;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public boolean isPageQuestion(String page, String code) {
        String toCheck=page+"."+code;
        return this.reference!=null && toCheck.equals(this.reference); 
    }
    public boolean isSectionQuestion(String page, String section, String code) {
        String toCheck=page+"."+section+"."+code;
        return this.reference!=null && toCheck.equals(this.reference); 
    }
}
