package com.example.wishlist.Models;

public class Wish {

    private int wishId;
    private String name;
    private String description;
    private String url;
    private String price;
    private int wishlistid;
    private int userid;

    public Wish(int wishlistid, int userid) {
        this.wishlistid = wishlistid;
        this.userid = userid;
    }

    public Wish(int wishId, String name, String description, String url, String price, int wishlistid) {
        this.wishId = wishId;
        this.name = name;
        this.description = description;
        this.url = url;
        this.price = price;
        this.wishlistid = wishlistid;
    }


    public Wish() {

    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getWishId() {
        return wishId;
    }

    public void setWishId(int wishId) {
        this.wishId = wishId;
    }

    public int getWishlistid() {
        return wishlistid;
    }

    public void setWishlistid(int wishlistid) {
        this.wishlistid = wishlistid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Wish{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", price='" + price + '\'' +
                '}';
    }

}