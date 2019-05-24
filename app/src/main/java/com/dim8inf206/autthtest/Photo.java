package com.dim8inf206.autthtest;



public class Photo{
    private String photoTimestamp;
    private String description;
    private String link;

    public Photo(String photoTimestamp, String description, String link) {
        this.photoTimestamp = photoTimestamp;
        this.description = description;
        this.link = link;
    }

    public String getPhotoTimestamp() {
        return photoTimestamp;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }


}
