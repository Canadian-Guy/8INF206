package com.dim8inf206.autthtest;



public class Photo {
    private String description;
    private String link;

    public Photo(String description, String link) {
        this.description = description;
        this.link = link;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }


}
