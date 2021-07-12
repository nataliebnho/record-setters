package com.example.the_commoners_guinness;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@ParseClassName("Category")
public class Category extends ParseObject {

    public static final String KEY_POSTS = "Posts";
    public static final String KEY_NAME = "name";

    public Category() {
    }

    public List<Post> getPosts() {
        return getList(KEY_POSTS);
    }

    public void addPosts(Post post) {
        add(KEY_POSTS, post);
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }





}
