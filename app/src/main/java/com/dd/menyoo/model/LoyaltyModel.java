package com.dd.menyoo.model;

import java.util.ArrayList;

/**
 * Created by Administrator on 4/13/2016.
 */
public class LoyaltyModel {

    private int currentStamps;
    private ArrayList<LTYModel> LTY;
    private ArrayList<Integer> levels ;
    private int MaxStamps;
    private int AllTimeStamps ;

    public LoyaltyModel(int currentStamps, ArrayList<LTYModel> LTY, ArrayList<Integer> levels, int maxStamps,int allTimeStamps) {
        this.currentStamps = currentStamps;
        this.LTY = LTY;
        this.levels = levels;
        this.MaxStamps = maxStamps;
        this.AllTimeStamps = allTimeStamps;

    }

    public int getCurrentStamps() {
        return currentStamps;
    }

    public ArrayList<LTYModel> getLTY() {
        return LTY;
    }

    public ArrayList<Integer> getLevels() {
        return levels;
    }

    public int getMaxStamps() {
        return MaxStamps;
    }

    public int getAllTimeStamps() {
        return AllTimeStamps;
    }

    public void setCurrentStamps(int currentStamps) {
        this.currentStamps = currentStamps;
    }
}
