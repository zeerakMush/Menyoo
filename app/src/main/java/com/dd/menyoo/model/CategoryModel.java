package com.dd.menyoo.model;

/**
 * Created by Administrator on 22-Feb-16.
 */
public class CategoryModel {
    private int categoryId;
    private String name ;
    private String imageName;
    private String parentID;
    private String categoryMessage;
    private int priority;


    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageName() {
        return imageName;
    }

    public String getParentID() {
        return parentID;
    }

    public void setParentID(String parentID) {
        this.parentID = parentID;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getCategoryMessage() {
        return categoryMessage;
    }

    public void setCategoryMessage(String categoryMessage) {
        this.categoryMessage = categoryMessage;
    }

    public CategoryModel(int categoryId, String name, String imageName, String parentID,String categoryMessage) {
        this.categoryId = categoryId;
        this.name = name;
        this.imageName = imageName;
        this.parentID = parentID;
        this.categoryMessage = categoryMessage;
    }
}
