package com.dim8inf206.autthtest;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Tag implements Parcelable {
    private String tagName;
    public boolean isSelected = false;

    public Tag(String tagName) {
        this.tagName = tagName;
    }

    private Tag(Parcel in){
        tagName = in.readString();
        isSelected = in.readByte() != 0; //== true if byte != 0
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagName() {
        return tagName;
    }

    public void switchSelection(){
        if(isSelected)
            isSelected = false;
        else
            isSelected = true;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "tagName='" + tagName + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(tagName);
        parcel.writeByte((byte) (isSelected ? 1 : 0)); //== true if != 0
    }

    public static final Parcelable.Creator<Tag> CREATOR
            = new Parcelable.Creator<Tag>() {
        public Tag createFromParcel(Parcel in) {
            return new Tag(in);
        }

        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };
}