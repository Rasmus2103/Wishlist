package com.example.wishlist.Repositories;
import com.example.wishlist.DTO.UserDTO;
import com.example.wishlist.DTO.WishlistDTO;
import com.example.wishlist.Models.User;
import com.example.wishlist.Models.Wish;

import java.util.List;

public interface IRepositoryDB {
    User getUser(int userid);
    int getUserid(String username);
    List<User> getUsers();
    List<WishlistDTO> getWishlists(int userId);
    void registerUser(User user);
    UserDTO userDTOByID(int userid);
    void addWishlist(int userID, String wishlistName);
    List<Wish> getWishes(int userId);
    void deleteWishlist(int wishlistId);
    void deleteWish(int wishId);
    void addWishToWishlist(Wish wish, int wishlistid);

}