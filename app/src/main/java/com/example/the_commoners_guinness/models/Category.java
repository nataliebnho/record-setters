package com.example.the_commoners_guinness.models;

import android.util.Log;

import androidx.annotation.Nullable;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//@Parcel (analyze = Category.class)
@ParseClassName("Category")
public class Category extends ParseObject {

    public static final String KEY_NAME = "name";
    public static final String KEY_WINNER = "winner";
    public static final String KEY_WINNERUSER = "winnerUser";
    public static final String KEY_VOTINGPERIOD = "votingPeriod";
    public static final String KEY_VOTINGPERIODTIME = "votingPeriodTime";
    public static final String KEY_FIRSTCHALLENGEPOST = "firstChallengePost";
    public static final String KEY_USERSVOTED = "usersVoted";

    public boolean hasVoted(ParseUser user) {
        if(getUsersVoted() != null) {
            JSONArray usersVoted = getUsersVoted();

            for(int i = 0; i < usersVoted.length(); i++) {
                String userPointer = null;
                try {
                    userPointer = usersVoted.getString(i);
                    if (userPointer.equals(user.getObjectId())) {
                        Log.d("Category", "hasVoted: true equal to user");
                        return true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d("Category", "hasVoted: false not equal to user");
        return false;
    }

    public void removeVote(ParseUser currentUser) throws JSONException {
        JSONArray userVoted = getUsersVoted();

        if (userVoted == null)
            userVoted = new JSONArray();

        for (int i  = 0; i < userVoted.length(); i++) {
            String userPointer = userVoted.getString(i);
            if (userPointer.equals(currentUser.getObjectId())){
                userVoted.remove(i);
            }
        }
        put(KEY_USERSVOTED, userVoted);
    }



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

    public ParseObject getFirstChallengePost() { return getParseObject(KEY_FIRSTCHALLENGEPOST); }

    public void setFirstChallengePost(Post post) { put(KEY_FIRSTCHALLENGEPOST, post); }

    public JSONArray getUsersVoted() { return getJSONArray("usersVoted"); }

    public void setUsersVoted( String userID ) { addUnique("usersVoted", userID); }


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
