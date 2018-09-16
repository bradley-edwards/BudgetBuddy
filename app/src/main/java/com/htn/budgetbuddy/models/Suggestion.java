package com.htn.budgetbuddy.models;

public class Suggestion {
    private String currentName;
    private String suggestedName;
    private String currentURL;
    private String suggestedURL;

    public String getCurrentName() {
        return currentName;
    }

    public void setCurrentName(String currentName) {
        this.currentName = currentName;
    }

    public String getSuggestedName() {
        return suggestedName;
    }

    public void setSuggestedName(String suggestedName) {
        this.suggestedName = suggestedName;
    }

    public String getCurrentURL() {
        return currentURL;
    }

    public void setCurrentURL(String currentURL) {
        this.currentURL = currentURL;
    }

    public String getSuggestedURL() {
        return suggestedURL;
    }

    public void setSuggestedURL(String suggestedURL) {
        this.suggestedURL = suggestedURL;
    }
}
