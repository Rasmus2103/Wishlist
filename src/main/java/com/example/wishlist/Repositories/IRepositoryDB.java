package com.example.wishlist.Repositories;
import com.example.wishlist.DTO.UserDTO;
import com.example.wishlist.Models.User;

import java.util.List;

public interface IRepositoryDB {
    List<User> getUsers();
    void registerUser(User user);
    UserDTO userDTOByID(String id);

    void addWishListToUser(String id, UserDTO userDTO);
}
