package com.example.helmi.pengaduan.model;

import android.os.Parcel;
import android.os.Parcelable;

public class UserAccountSettings implements Parcelable {

    private String description;
    private String display_name;
    private long posts;
    private String profile_photo;
    private String username;
    private String user_id;

    public UserAccountSettings(String description, String display_name, long posts, String profile_photo, String username, String user_id) {
        this.description = description;
        this.display_name = display_name;
        this.posts = posts;
        this.profile_photo = profile_photo;
        this.username = username;
        this.user_id = user_id;
    }

    public UserAccountSettings() {
    }

    protected UserAccountSettings(Parcel in) {
        description = in.readString();
        display_name = in.readString();
        posts = in.readLong();
        profile_photo = in.readString();
        username = in.readString();
        user_id = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeString(display_name);
        dest.writeLong(posts);
        dest.writeString(profile_photo);
        dest.writeString(username);
        dest.writeString(user_id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserAccountSettings> CREATOR = new Creator<UserAccountSettings>() {
        @Override
        public UserAccountSettings createFromParcel(Parcel in) {
            return new UserAccountSettings(in);
        }

        @Override
        public UserAccountSettings[] newArray(int size) {
            return new UserAccountSettings[size];
        }
    };

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public long getPosts() {
        return posts;
    }

    public void setPosts(long posts) {
        this.posts = posts;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
