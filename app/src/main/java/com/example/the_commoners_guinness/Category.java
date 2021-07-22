package com.example.the_commoners_guinness;

import androidx.annotation.Nullable;

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
    public static final String KEY_VOTINGPERIODTIME = "votingPeriodTime";

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

    public long getVotingPeriodTime() { return getLong(KEY_VOTINGPERIODTIME); }

    public void setVotingPeriodTime(Long number) { put(KEY_VOTINGPERIODTIME, number); }

    @Override
    public boolean equals(@Nullable @org.jetbrains.annotations.Nullable Object obj) {
        if (this == obj) {
            return true;
        }

        if (this.getObjectId().equals(((Category) obj).getObjectId())) {
            return true;
        }

        return false;
    }
}
