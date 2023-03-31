package com.example.wishlist.DTO;
import com.example.wishlist.Models.Wish;

import java.util.List;

public class WishlistDTO extends UserDTO {

    private String name;
    private int wishlistid;
    private List<Wish> wishes;

    public WishlistDTO(String name, int wishlistid) {
        this.name = name;
        this.wishlistid = wishlistid;
    }

    public WishlistDTO(String name, List<Wish> wishes){
        this.name = name;
        this.wishes = wishes;
    }
    public WishlistDTO(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
                "name='" + name + '\'' +
                ", wishes=" + wishes +
                '}';
    }

}
