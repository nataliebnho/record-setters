package com.example.the_commoners_guinness;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.List;

//@Parcel (analyze = Category.class)
@ParseClassName("Category")
public class Category extends ParseObject {

    public static final String KEY_NAME = "name";
    public static final String KEY_WINNER = "winner";
    public static final String KEY_WINNERUSER = "winnerUser";
    public static final String KEY_VOTINGPERIOD = "votingPeriod";

    public Category() {
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public ParseObject getWinner() { return getParseObject(KEY_WINNER); }

    public void setWinner(ParseObject post) { put(KEY_WINNER, post); }

    public ParseUser getWinnerUser() { return getParseUser(KEY_WINNERUSER); }

    public void setWinnerUser(ParseUser user) { put(KEY_WINNERUSER, user); }

    public Boolean getVotingPeriod() { return getBoolean(KEY_VOTINGPERIOD); }

    public void setVotingPeriod(Boolean bool) { put(KEY_VOTINGPERIOD, bool); }

}
