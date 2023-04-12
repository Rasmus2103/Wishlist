package com.example.wishlist.DTO;
import com.example.wishlist.Models.Wish;

import java.util.List;

public class WishlistDTO extends UserDTO {

    private String wishlistname;
    private int wishlistid;
    private List<Wish> wishes;

    public WishlistDTO(String wishlistname, int wishlistid) {
        this.wishlistname = wishlistname;
        this.wishlistid = wishlistid;
    }

    public WishlistDTO(String wishlistname, List<Wish> wishes) {
        this.wishlistname = wishlistname;
        this.wishes = wishes;
    }

    public WishlistDTO(String wishlistname, int wishlistid, List<Wish> wishes){
        this.wishlistname = wishlistname;
        this.wishlistid = wishlistid;
        this.wishes = wishes;
    }

    public WishlistDTO(){

    }

    public String getWishlistname() {
        return wishlistname;
    }

    public void setWishlistname(String wishlistname) {
        this.wishlistname = wishlistname;
    }

    public int getWishlistid(){
        return wishlistid;
    }

    public void setWishlistid(int wishlistid){
        this.wishlistid = wishlistid;
    }

    public List<Wish> getWishes() {
        return wishes;
    }

    public void setWishes(List<Wish> wishes) {
        this.wishes = wishes;
    }

    @Override
    public String toString() {
        return "WishlistDTO{" +
                "name='" + wishlistname + '\'' +
                ", wishes=" + wishes +
                '}';
    }

}