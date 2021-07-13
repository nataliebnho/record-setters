package com.example.the_commoners_guinness;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Post")
public class Post extends ParseObject {

    public static final String KEY_CAPTION = "caption";
    public static final String KEY_VIDEO = "video";
    public static final String KEY_USER = "user";
    public static final String KEY_CATEGORY = "category";

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

}
