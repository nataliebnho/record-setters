package com.example.the_commoners_guinness;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
}

class RecommendationTest {
    LinkedList<String> finalRecommended = new LinkedList<>();

    public LinkedList<String> setRecommended(ArrayList<String> userLikes, ArrayList<ArrayList<String>> candLikes) {
        HashMap<String, Double> recommended = new HashMap<>();
        // 1. Generate hashmap of normalized scores per category for current user
        HashMap<String, Double> currHM = new HashMap<>();
        currHM = generateNormHM(userLikes);
        // 2. Sample 10 random candidate users to do recommendations
        // 3. Loop through each candidate user
        // 3.1 Retrieve categories liked by each use
        // 3.2 Generate hashmap of normalized scores per category for candidate user
        for (ArrayList<String> candidate : candLikes) {
            HashMap<String, Double> candHM = generateNormHM(candidate);
            double overlapScore = 0;
            for (String categoryName : userLikes) {
                if (candHM.containsKey(categoryName)) {
                    overlapScore += candHM.get(categoryName) + currHM.get(categoryName);
                }
            }
            ArrayList<String> seenInThisCandidate = new ArrayList<String>();
            for (String categoryName : candidate) {
                if (!currHM.containsKey(categoryName)) {
                    if (recommended.containsKey(categoryName) && !seenInThisCandidate.contains(categoryName)) {
                        recommended.put(categoryName, recommended.get(categoryName) + overlapScore);
                    } else {
                        recommended.put(categoryName, overlapScore);
                        seenInThisCandidate.add(categoryName);
                    }
                }
            }
        }
        sortByValue(recommended);
        finalRecommended = sortByValue(recommended);
        return finalRecommended;
    }


    public HashMap<String, Double> generateNormHM(List<String> list) {
        HashMap<String, Double> map = new HashMap<String, Double>();
        for (String categoryName : list) {
            if (map.containsKey(categoryName)) {
                map.put(categoryName, map.get(categoryName) + 1);
            } else {
                Double d = new Double(1);
                map.put(categoryName, d);
            }
        }
        for (String categoryName : map.keySet()) {
            map.put(categoryName, map.get(categoryName) / list.size());
        }
        return map;
    }

    public static LinkedList<String> sortByValue(HashMap<String, Double> hm) {
        LinkedList<String> finalRecommended = new LinkedList<>();
        // Create a list from elements of HashMap
        List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(hm.entrySet());
        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1,
                               Map.Entry<String, Double> o2) {

                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
        // put data from sorted list to hashmap
        HashMap<String, Double> temp = new LinkedHashMap<String, Double>();
        for (Map.Entry<String, Double> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
            if (aa.getValue() != 0) {
                finalRecommended.add(aa.getKey());
            }
        }
        return finalRecommended;
    }

    // The current user likes categories "A", "B", and "C". User 2 has three of the same categories liked (A, B, C)
    // and User 3 has two of the (A, B). Thus, we conclude the current user is more similar to User 2 than they are to User 3,
    // and from a human perspective it is clear that the current user should be more highly recommended category D
    // (what User 2 liked) than category E (what User 3 liked).
    @Test
    public void recommendation_simple() {
        ArrayList<String> curr_user_likes = new ArrayList<>(Arrays.asList("A", "B", "C"));

        ArrayList<String> user_2_likes = new ArrayList<>(Arrays.asList("A", "B", "C", "D"));
        ArrayList<String> user_3_likes = new ArrayList<>(Arrays.asList("A", "B", "E"));
        ArrayList<ArrayList<String>> candLikes = new ArrayList<ArrayList<String>>(Arrays.asList(user_2_likes, user_3_likes));

        LinkedList<String> expected = new LinkedList<>(Arrays.asList("D", "E"));
        assertEquals(expected, setRecommended(curr_user_likes, candLikes));
    }


    // This is similar to the first test, just adding a 4th user that is even less similar to the current user than the 3rd
    // user is to the current user. As expected, we see that because the User 4 only holds one similarity to the current
    // user, category "F" is pitched lower than "D" and "E".

    @Test
    public void recommendation_simple2() {

        ArrayList<String> curr_user_likes = new ArrayList<>(Arrays.asList("A", "B", "C"));

        ArrayList<String> user_2_likes = new ArrayList<>(Arrays.asList("A", "B", "C", "D"));
        ArrayList<String> user_3_likes = new ArrayList<>(Arrays.asList("A", "B", "E"));
        ArrayList<String> user_4_likes = new ArrayList<>(Arrays.asList("A", "F"));

        ArrayList<ArrayList<String>> candLikes = new ArrayList<ArrayList<String>>(Arrays.asList(user_2_likes, user_3_likes, user_4_likes));
        LinkedList<String> expected = new LinkedList<>(Arrays.asList("D", "E", "F"));

        assertEquals(expected, setRecommended(curr_user_likes, candLikes));

    }


    // The current user and User 2 share no category likes in common, so we should not pitch categories "E" or "F"
    // to the current user because they are not similar users and would not have similar interests
    @Test
    public void recommendation_no_similarity() {
        ArrayList<String> curr_user_likes = new ArrayList<>(Arrays.asList("A", "B", "C"));

        ArrayList<String> user_2_likes = new ArrayList<>(Arrays.asList("E", "F"));
        ArrayList<ArrayList<String>> candLikes = new ArrayList<ArrayList<String>>(Arrays.asList(user_2_likes));
        LinkedList<String> expected = new LinkedList<>();

        assertEquals(expected, setRecommended(curr_user_likes, candLikes));
    }


    // The users have the exact same distribution, so the algorithm should not pitch the current user anything
    @Test

    public void recommendation_identical() {
        ArrayList<String> curr_user_likes = new ArrayList<>(Arrays.asList("A", "B", "C"));

        ArrayList<String> user_2_likes = new ArrayList<>(Arrays.asList("A", "B", "C"));
        ArrayList<ArrayList<String>> candLikes = new ArrayList<ArrayList<String>>(Arrays.asList(user_2_likes));
        LinkedList<String> expected = new LinkedList<>(Arrays.asList());
        assertEquals(expected, setRecommended(curr_user_likes, candLikes));
    }


    // This test is slightly ambiguous, and from a human perspective is a little more difficult to determine
    // what should happen. We still have our current user as most similar to User 2. However, User 3 and User 4,
    // while less similar (but still sharing some similarity!) to the current user, both like category E. In other
    // words, Category "D" is more finely tuned towards the current user, but Category "E" is a more popular category
    // overall. To me, this could go either way and I wouldn't necessarily fault it either way. As we see, the test
    // outcome pitches it the more popular category "E" first.
    @Test
    public void recommendation_ambiguous() {
        ArrayList<String> curr_user_likes = new ArrayList<>(Arrays.asList("A", "B", "C"));

        ArrayList<String> user_2_likes = new ArrayList<>(Arrays.asList("A", "B", "C", "D"));
        ArrayList<String> user_3_likes = new ArrayList<>(Arrays.asList("A", "B", "E"));
        ArrayList<String> user_4_likes = new ArrayList<>(Arrays.asList("A", "E"));

        ArrayList<ArrayList<String>> candLikes = new ArrayList<ArrayList<String>>(Arrays.asList(user_2_likes, user_3_likes, user_4_likes));

        LinkedList<String> expected = new LinkedList<>(Arrays.asList("E", "D"));
        assertEquals(expected, setRecommended(curr_user_likes, candLikes));
    }


    // Similar ambiguous case as the one above, but I believe this should pitch "D"
    // rather than "E" because the current user shares more similarity to User 2 than User 3, even though
    // User 3 shares decent similar to the current user and really enjoys catgeory "E", category "D" is
    // a better, more fine tuned pitch for the current user.

    @Test
    public void recommendation_ambiguous2() {

        ArrayList<String> curr_user_likes = new ArrayList<>(Arrays.asList("A", "B", "C"));
        ArrayList<String> user_2_likes = new ArrayList<>(Arrays.asList("A", "B", "C", "D"));
        ArrayList<String> user_3_likes = new ArrayList<>(Arrays.asList("A", "B", "E", "E"));

        ArrayList<ArrayList<String>> candLikes = new ArrayList<ArrayList<String>>(Arrays.asList(user_2_likes, user_3_likes));
        LinkedList<String> expected = new LinkedList<>(Arrays.asList("D", "E"));
        assertEquals(expected, setRecommended(curr_user_likes, candLikes));

    }


    // Here I have kept the same current user, User 2, and User 3 as the test above, however I made the category
    // "E" more popular by adding User 4 which doesn't share any of the same likes as the current user but
    // likes category "E". I want to ensure that the addition of User 4 making "E" more popular doesn't change
    // the fact that Category D is pitched first because Category D is still the more fine tuned reccommendation
    // for the current user.

    @Test

    public void recommendation_ambiguous2_tweak() {

        ArrayList<String> curr_user_likes = new ArrayList<>(Arrays.asList("A", "B", "C"));

        ArrayList<String> user_2_likes = new ArrayList<>(Arrays.asList("A", "B", "C", "D"));
        ArrayList<String> user_3_likes = new ArrayList<>(Arrays.asList("A", "B", "E", "E"));
        ArrayList<String> user_4_likes = new ArrayList<>(Arrays.asList("G", "E", "E"));

        ArrayList<ArrayList<String>> candLikes = new ArrayList<ArrayList<String>>(Arrays.asList(user_2_likes, user_3_likes, user_4_likes));

        LinkedList<String> expected = new LinkedList<>(Arrays.asList("D", "E"));
        assertEquals(expected, setRecommended(curr_user_likes, candLikes));
    }


    // In this case, I have once again made Category "E" more popular but instead of having User 4 share no likes
    // with the current user, "E" does share a catgeory like. This is another ambiguous case but from a human
    // perspective I would ideally like to have "E" be pitched before "D".
    @Test

    public void recommendation_ambiguous2_tweak2() {

        ArrayList<String> curr_user_likes = new ArrayList<>(Arrays.asList("A", "B", "C"));

        ArrayList<String> user_2_likes = new ArrayList<>(Arrays.asList("A", "B", "C", "D"));
        ArrayList<String> user_3_likes = new ArrayList<>(Arrays.asList("A", "B", "E", "E"));
        ArrayList<String> user_4_likes = new ArrayList<>(Arrays.asList("C", "E"));

        ArrayList<ArrayList<String>> candLikes = new ArrayList<ArrayList<String>>(Arrays.asList(user_2_likes, user_3_likes, user_4_likes));
        LinkedList<String> expected = new LinkedList<>(Arrays.asList("E", "D"));
        assertEquals(expected, setRecommended(curr_user_likes, candLikes));
    }


    // The current user and User 2 again share a very similar profile. However, we introduce a user
    // who is very heavily distributed on the category "A", which the current user has also liked, but only once.
    // From a human perspective, I would pitch category "E" over category "F" because User 2 is more similar to
    // the current user, but I want to test to make sure that this heavy distribution of liking category "A" does not
    // pitch category "F" over category "E".

    @Test

    public void recommendation_skewed() {

        ArrayList<String> curr_user_likes = new ArrayList<>(Arrays.asList("A", "B", "C"));
        ArrayList<String> user_2_likes = new ArrayList<>(Arrays.asList("A", "B", "C", "E"));
        ArrayList<String> user_3_likes = new ArrayList<>(Arrays.asList("A", "A", "A", "A", "A", "A", "A", "F"));
        ArrayList<ArrayList<String>> candLikes = new ArrayList<ArrayList<String>>(Arrays.asList(user_2_likes, user_3_likes));

        LinkedList<String> expected = new LinkedList<>(Arrays.asList("E", "F"));
        assertEquals(expected, setRecommended(curr_user_likes, candLikes));
    }


    // In this test, we observe that the current user is heavily distributed on category "B", indicating
    // a deep affinity for this category. I would expect "F" to be pitched higher than "E" because even though
    // User 2 shares two catgeories, "A" and "C", with the current user, User 3 has liked category "B", which
    // from a human persective I would say that User 3 is more similar to the current user.
    @Test
    public void recommendation_skewed2() {
        ArrayList<String> curr_user_likes = new ArrayList<>(Arrays.asList("A", "B", "B", "B", "B", "C"));

        ArrayList<String> user_2_likes = new ArrayList<>(Arrays.asList("A", "C", "E"));
        ArrayList<String> user_3_likes = new ArrayList<>(Arrays.asList("B", "F"));
        ArrayList<ArrayList<String>> candLikes = new ArrayList<ArrayList<String>>(Arrays.asList(user_2_likes, user_3_likes));

        LinkedList<String> expected = new LinkedList<>(Arrays.asList("F", "E"));

        assertEquals(expected, setRecommended(curr_user_likes, candLikes));

    }

}


