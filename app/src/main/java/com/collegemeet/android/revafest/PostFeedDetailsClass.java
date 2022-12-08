package com.collegemeet.android.revafest;

import android.os.Parcel;
import android.os.Parcelable;

public class PostFeedDetailsClass implements Parcelable {

    private String name;
    private String profileImage;
    private String date;
    private String path;
    private String imageUri;
    private String caption;
    private String uid;
    private String coordinates; //this will help to create sub file in realtime database for likes
    private String postUserUid;
    private String type;
    private String frameUri;


    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public PostFeedDetailsClass(String name, String profileImage, String date, String path, String imageUri, String caption, String uid, String coordinates) {
        this.name = name;
        this.profileImage = profileImage;
        this.date = date;
        this.path = path;
        this.imageUri = imageUri;
        this.caption = caption;
        this.uid = uid;
        this.coordinates = coordinates;

    }

    //searched /profile
    public PostFeedDetailsClass(String name, String profileImage, String date, String path, String imageUri, String caption, String uid, String coordinates,String postUserUid , String frameUri) {
        this.name = name;
        this.profileImage = profileImage;
        this.date = date;
        this.path = path;
        this.imageUri = imageUri;
        this.caption = caption;
        this.uid = uid;// uid of user uploaded ie searched
        this.coordinates = coordinates;//gives type in searched
        this.postUserUid = postUserUid;//gives coordinates in searched
        this.frameUri = frameUri;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    //type and postUserUid work are altered

    public String getFrameUri() {
        return frameUri;
    }

    public void setFrameUri(String frameUri) {
        this.frameUri = frameUri;
    }

    //feed / fav
    public PostFeedDetailsClass(String name, String profileImage, String date, String path, String imageUri, String caption, String uid, String coordinates,String postUserUid,String type,String frameUri) {
        this.name = name;
        this.profileImage = profileImage;
        this.date = date;
        this.path = path;
        this.imageUri = imageUri;
        this.caption = caption;
        this.uid = uid;
        this.coordinates = coordinates;
        this.postUserUid = postUserUid;
        this.type = type;
        this.frameUri = frameUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPostUserUid() {
        return postUserUid;
    }

    public void setPostUserUid(String postUserUid) {
        this.postUserUid = postUserUid;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.profileImage);
        dest.writeString(this.date);
        dest.writeString(this.path);
        dest.writeString(this.imageUri);
        dest.writeString(this.caption);
        dest.writeString(this.uid);
        dest.writeString(this.coordinates);
        dest.writeString(this.postUserUid);
        dest.writeString(this.frameUri);
    }

    public void readFromParcel(Parcel source) {
        this.name = source.readString();
        this.profileImage = source.readString();
        this.date = source.readString();
        this.path = source.readString();
        this.imageUri = source.readString();
        this.caption = source.readString();
        this.uid = source.readString();

        this.coordinates= source.readString();
        this.postUserUid= source.readString();
        this.frameUri= source.readString();


    }

    public PostFeedDetailsClass() {
    }

    protected PostFeedDetailsClass(Parcel in) {
        this.name = in.readString();
        this.profileImage = in.readString();
        this.date = in.readString();
        this.path = in.readString();
        this.imageUri = in.readString();
        this.caption = in.readString();
        this.uid = in.readString();
        this.coordinates = in.readString();
        this.postUserUid = in.readString();
        this.frameUri = in.readString();
    }

    public static final Parcelable.Creator<PostFeedDetailsClass> CREATOR = new Parcelable.Creator<PostFeedDetailsClass>() {
        @Override
        public PostFeedDetailsClass createFromParcel(Parcel source) {
            return new PostFeedDetailsClass(source);
        }

        @Override
        public PostFeedDetailsClass[] newArray(int size) {
            return new PostFeedDetailsClass[size];
        }
    };
}