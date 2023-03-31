package com.example.wishlist.Repositories;
import com.example.wishlist.DTO.UserDTO;
import com.example.wishlist.DTO.WishlistDTO;
import com.example.wishlist.Models.User;

import java.util.List;

public interface IRepositoryDB {
    List<User> getUsers();
    List<WishlistDTO> getWishlists();
    void registerUser(User user);
    UserDTO userDTOByID(int userid);

    void addWishListToUser(int userid, int wishlistID);

    void addWishlist(String wishlistName);
    void deleteWishlist(int wishlistId);
}
