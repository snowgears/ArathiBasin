package com.snowgears.arathibasin.score;

import java.util.Comparator;

/**
 * Comparator for determining which {@link PlayerScore} is higher than another.
 */
public class PlayerScoreComparator implements Comparator<PlayerScore> {

    @Override
    public int compare(PlayerScore score1, PlayerScore score2){
        int a = score1.getPoints();
        int b = score2.getPoints();
        return a > b ? +1 : a < b ? -1 : 0;
    }
}
