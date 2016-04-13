package com.dd.menyoo.model;

import java.util.ArrayList;

/**
 * Created by Administrator on 4/12/2016.
 */
public class CategoryExtra {
    private String optionName ;
    private ArrayList<Options> options;

    public String getOptionName() {
        return optionName;
    }

    public ArrayList<Options> getOptions() {
        return options;
    }



    public CategoryExtra(String optionName, ArrayList<Options> options) {
        this.optionName = optionName;
        this.options = options;
    }
}
