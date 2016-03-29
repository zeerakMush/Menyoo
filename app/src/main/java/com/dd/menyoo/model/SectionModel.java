package com.dd.menyoo.model;

/**
 * Created by Administrator on 3/28/2016.
 */
public class SectionModel extends BaseClassModel {

    private String sectionName;
    private int itemCounts=0;
    private int categoryID;

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public int getItemCounts() {
        return itemCounts;
    }

    public void setItemCounts(int itemCounts) {
        this.itemCounts = itemCounts;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public void updateItemCount(int id){
        if(id==categoryID)
            this.itemCounts++;
    }
}
