package com.dd.menyoo.model;

/**
 * Created by Administrator on 19-Feb-16.
 */
public class MenuModel extends BaseClassModel {
    private String title;
    private String description;
    private String imageName;
    private double price;
    private int itemID;
    private String discount;
    private boolean isSpecial;
    private boolean isPopular;
    private boolean isfirstTimeItem;
    private int itemType;
    private boolean isExtraData = false;
    private String categoryName;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public boolean isSpecial() {
        return isSpecial;
    }

    public void setSpecial(boolean special) {
        isSpecial = special;
        if(isSpecial)
            itemType = type.Specail.getValue();
    }

    public boolean isPopular() {
        return isPopular;
    }

    public void setPopular(boolean popular) {
        isPopular = popular;
        if(isPopular)
            itemType = type.Poplar.getValue();
    }

    public boolean isfirstTimeItem() {
        return isfirstTimeItem;
    }

    public void setIsfirstTimeItem(boolean isfirstTimeItem) {
        this.isfirstTimeItem = isfirstTimeItem;
        if(isfirstTimeItem)
            itemType = type.FirstTime.getValue();
    }

    public String getDiscount() {
        return discount;
    }

    public boolean isExtraData() {
        return isExtraData;
    }

    public void setExtraData(boolean extraData) {
        isExtraData = extraData;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public MenuModel() {
    }

    public MenuModel(String title, String description,
                     String imageName, double price, int itemID, boolean isSpecial, String dicount,boolean isPopular,boolean isfirstTimeItem) {
        this.title = title;
        this.description = description;
        this.imageName = imageName;
        this.price = price;
        this.itemID = itemID;
        this.discount = dicount;
        setSpecial(isSpecial);
        setPopular(isPopular);
        setIsfirstTimeItem(isfirstTimeItem);
       /* this.isSpecial = isSpecial;
        this.isPopular = isPopular;*/
    }

    public MenuModel(String title, String description, double price, int itemID,boolean isExtraData) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.itemID = itemID;
        this.isExtraData=isExtraData;
    }

    public static enum type{
        Poplar{
            @Override
            public int getValue() {
                return 1;
            }
        },
        Specail{
            @Override
            public int getValue() {
                return 2;
            }
        },FirstTime{
            @Override
            public int getValue() {
                return 3;
            }
        };
        public abstract int getValue();
    }

    public int getItemType() {
        return itemType;
    }
}
