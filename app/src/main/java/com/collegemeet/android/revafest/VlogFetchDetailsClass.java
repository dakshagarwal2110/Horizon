package com.collegemeet.android.revafest;

import android.os.Parcel;
import android.os.Parcelable;

public class VlogFetchDetailsClass implements Parcelable {


    private String image;
    private String eventName;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public VlogFetchDetailsClass(String image, String eventName) {
        this.image = image;
        this.eventName = eventName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.image);
        dest.writeString(this.eventName);
    }

    public void readFromParcel(Parcel source) {
        this.image = source.readString();
        this.eventName = source.readString();
    }

    protected VlogFetchDetailsClass(Parcel in) {
        this.image = in.readString();
        this.eventName = in.readString();
    }

    public static final Parcelable.Creator<VlogFetchDetailsClass> CREATOR = new Parcelable.Creator<VlogFetchDetailsClass>() {
        @Override
        public VlogFetchDetailsClass createFromParcel(Parcel source) {
            return new VlogFetchDetailsClass(source);
        }

        @Override
        public VlogFetchDetailsClass[] newArray(int size) {
            return new VlogFetchDetailsClass[size];
        }
    };
}
