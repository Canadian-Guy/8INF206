package com.dim8inf206.autthtest;

import android.net.Uri;

public class Photo {
    private String description;
    private Uri link;

    public Photo(){}

    public Photo(String description, Uri link) {
        this.description = description;
        this.link = link;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLink(Uri link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public Uri getLink() {
        return link;
    }
}
