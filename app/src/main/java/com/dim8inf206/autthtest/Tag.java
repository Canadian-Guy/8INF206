package com.dim8inf206.autthtest;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Tag {
    private String tagName;


    public Tag(String tagName) {
        this.tagName = tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagName() {
        return tagName;
    }

    @Override
    public String toString() {
        return tagName;
    }
}
