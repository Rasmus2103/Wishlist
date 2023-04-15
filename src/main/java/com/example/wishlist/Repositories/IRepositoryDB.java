package com.example.wishlist.Repositories;
import com.example.wishlist.DTO.WishlistDTO;
import com.example.wishlist.Models.User;
import com.example.wishlist.Models.Wish;

import java.util.List;

public interface IRepositoryDB {
    User getUser(int userid);
    int getUserid(String username);
    List<User> getUsers();
    List<WishlistDTO> getWishlists(int userId);
    public WishlistDTO getSpecificWishlists(int wishlistid);
    void registerUser(User user);
    void addWishlist(int userID, String wishlistName);
    List<Wish> getWishes(int userId);
    List<Wish> getSpecificWishes(int wishlistid);
    void deleteWishlist(int wishlistId);
    void deleteWish(int wishId);
    void addWishToWishlist(Wish wish, int wishlistid);
    boolean usernameExists(String username);
}