package com.example.the_commoners_guinness;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Post")
public class Post extends ParseObject {

    public static final String KEY_CAPTION = "caption";
    public static final String KEY_VIDEO = "video";
    public static final String KEY_USER = "user";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_VOTECOUNT = "voteCount";
    public static final String KEY_LOCATION = "location";


    public Post() {
    }

    public String getCaption() { return getString(KEY_CAPTION); }

    public void setCaption(String caption) {
        put(KEY_CAPTION, caption);
    }

    public ParseFile getVideo() {
        return getParseFile(KEY_VIDEO);
    }

    public void setVideo(ParseFile parseFile) {
        put(KEY_VIDEO, parseFile);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public Category getCategory() { return (Category) getParseObject(KEY_CATEGORY); }

    public void setCategory(Category category) { put(KEY_CATEGORY, category); }

    public int getVoteCount() { return getInt(KEY_VOTECOUNT); }

    public void setVoteCount(int voteCount) { put(KEY_VOTECOUNT, voteCount); }

    public ParseGeoPoint getLocation() { return getParseGeoPoint(KEY_LOCATION); }

    public void setLocation(ParseGeoPoint location) { put(KEY_LOCATION, location); }

}
