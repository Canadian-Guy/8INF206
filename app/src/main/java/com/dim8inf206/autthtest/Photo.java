package com.dim8inf206.autthtest;


import android.os.Parcel;
import android.os.Parcelable;

public class Photo implements Parcelable {
    private String photoTimestamp;
    private String description;
    private String link;

    public Photo(String photoTimestamp, String description, String link) {
        this.photoTimestamp = photoTimestamp;
        this.description = description;
        this.link = link;
    }

    private Photo(Parcel in){
        photoTimestamp = in.readString();
        description = in.readString();
        link = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(photoTimestamp);
        parcel.writeString(description);
        parcel.writeString(link);
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

    public static final Parcelable.Creator<Photo> CREATOR
            = new Parcelable.Creator<Photo>() {
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

}
