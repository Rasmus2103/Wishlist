package com.example.wishlist.Repositories;

import com.example.wishlist.DTO.WishlistDTO;
import com.example.wishlist.Models.User;

import java.util.List;

public interface IRepositoryDB {
    List<User> getUsers();
    List<WishlistDTO> wishes(String username);
}
