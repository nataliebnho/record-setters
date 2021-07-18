package com.example.the_commoners_guinness;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Leaderboard")
public class Leaderboard extends ParseObject {

    public static final String KEY_WINNER = "winner";
    public static final String KEY_SECOND = "second";
    public static final String KEY_THIRD = "third";
    public static final String KEY_CATEGORY = "category";

    public Leaderboard() { }

    public Category getLeaderboardCategory() { return (Category) getParseObject(KEY_CATEGORY); }

    public void setLeaderboardCategory(Category category) { put(KEY_CATEGORY, category); }

    public ParseUser getWinner() { return getParseUser(KEY_WINNER); }

    public void setWinner(ParseUser user) {
        put(KEY_WINNER, user);
    }

    public ParseUser getSecond() { return getParseUser(KEY_SECOND); }

    public void setSecond(ParseUser user) {
        put(KEY_SECOND, user);
    }

    public ParseUser getThird() { return getParseUser(KEY_THIRD); }

    public void setThird(ParseUser user) {
        put(KEY_THIRD, user);
    }


}
