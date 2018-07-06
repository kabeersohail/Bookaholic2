package com.example.sohail.bookaholic;

import com.google.firebase.database.Exclude;

public class Upload {
    private String mName;
    private String mImageUrl;
    private String mKey;
    private String mprice;

    public String getPrice() {
        return mprice;
    }

    public void setPrice(String price) {
        this.mprice = price;
    }

    public Upload() {
        //empty constructor needed
    }

    public Upload(String name, String price, String imageUrl) {
        if (name.trim().equals("")) {
            name = "No Name";
        }
        mprice = price;
        mName = name;
        mImageUrl = imageUrl;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    @Exclude
    public String getKey() {
        return mKey;
    }

    @Exclude
    public void setKey(String key) {
        mKey = key;
    }
}