package com.snowgears.arathibasin.score;


import org.bukkit.DyeColor;

/**
 * This class keeps track of current score tick and calculates how much score to give based on tick and.
 */
public class ScoreTick {

    private DyeColor color;
    private int tick;

    public ScoreTick(DyeColor color){
        this.color = color;
    }

    public DyeColor getColor(){
        return color;
    }

    public int getPoints(int bases){
        tick++;
        switch (bases){
            case 1:
                if(tick >= 12) {
                    tick = 0;
                    return 10;
                }
                break;
            case 2:
                if(tick >= 9) {
                    tick = 0;
                    return 10;
                }
                break;
            case 3:
                if(tick >= 6) {
                    tick = 0;
                    return 10;
                }
                break;
            case 4:
                if(tick >= 3) {
                    tick = 0;
                    return 10;
                }
                break;
            case 5:
                if(tick >= 1) {
                    tick = 0;
                    return 30;
                }
                break;
        }
        return 0;
    }
}
