package com.example.wishlist.Repositories;

import com.example.wishlist.DTO.UserDTO;
import com.example.wishlist.DTO.WishlistDTO;
import com.example.wishlist.Models.User;
import com.example.wishlist.Models.Wish;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository("wishlist_DB")
public class RepositoryDB implements IRepositoryDB {

    @Value("${spring.datasource.url}")
    private String db_url;

    @Value("${spring.datasource.username}")
    private String uid;

    @Value("${spring.datasource.password}")
    private String pwd;

    public Connection connection() {
        try {
            Connection con = DriverManager.getConnection(db_url,uid,pwd);
            return con;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public User getUser(int userid) {
        User user = null;
        try {
            String SQL = "SELECT * FROM user WHERE userid = ?";
            PreparedStatement ps = connection().prepareStatement(SQL);
            ps.setInt(1, userid);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                String id = rs.getString("userid");
                String name = rs.getString("name");
                String username = rs.getString("username");
                String password = rs.getString("password");
                user = new User(id, name, username, password);
            }
            return user;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public int getUserid(String username) {
        try {
            String SQL = "select userid from user where username = ?";
            PreparedStatement ps = connection().prepareStatement(SQL);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.getInt("userid");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        try {
            String SQL = "SELECT * FROM user";
            Statement stmt = connection().createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            while(rs.next()) {
                String id = rs.getString("userid");
                String name = rs.getString("name");
                String username = rs.getString("username");
                String password = rs.getString("password");
                users.add(new User(id, name, username, password));
            }
            return users;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<WishlistDTO> getWishlists(int userId) {
        List<WishlistDTO> wishlists = new ArrayList<>();
        try {
            String SQL = "SELECT wishlistname, wishlistid FROM wishlist WHERE userid = ?;";
            PreparedStatement ps = connection().prepareStatement(SQL);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                String name = rs.getString("wishlistname");
                int wishlistid = rs.getInt("wishlistid");
                wishlists.add(new WishlistDTO(name, wishlistid));
            }
            return wishlists;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<Wish> getWishes(int userId) {
        List<Wish> wishes = new ArrayList<>();
        try {
            String SQL = "SELECT wishname, description, url, price FROM Wish\n" +
                    "JOIN Wishlist ON Wish.wishlistid = Wishlist.wishlistid\n" +
                    "WHERE Wishlist.userid = ?;";
            PreparedStatement ps = connection().prepareStatement(SQL);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                String wish = rs.getString("wishname");
                String desription = rs.getString("description");
                String url = rs.getString("url");
                String price = rs.getString("price");
                wishes.add(new Wish(wish, desription, url, price));
            }
            return wishes;
        } catch (SQLException e){
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }
    }

    public void registerUser(User user) {
        try {
            String SQL = "INSERT INTO user (name, username, password) VALUES (?, ?, ?)";
            PreparedStatement ps = connection().prepareStatement(SQL);
            ps.setString(1, user.getName());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getPassword());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public UserDTO userDTOByID (int userid) {
        UserDTO userDTO = new UserDTO();
        List<WishlistDTO> wishListList = new ArrayList<>();
        try {
            String SQL = "SELECT name, username, password, wishlistname, wishname, description, url, price " +
                    "FROM user u " +
                    "JOIN userwishlist uw ON u.userid = uw.userid " +
                    "JOIN wishlist wl ON wl.wishlistid = uw.wishlistid " +
                    "JOIN wishlistwish ww ON ww.wishlistid = wl.wishlistid " +
                    "JOIN wish w ON w.wishid = ww.wishid " +
                    "WHERE u.userid = ?";
            PreparedStatement ps = connection().prepareStatement(SQL);
            ps.setInt(1, userid);
            ResultSet rs = ps.executeQuery();
            String currentWishlist = "";
            WishlistDTO wishlistDTO;
            List<Wish> wishes = null;
            while (rs.next()) {
                String name = rs.getString("name");
                String password = rs.getString("password");
                String userusername = rs.getString("username");
                String wishlistname = rs.getString("wishlistname");
                String wishname = rs.getString("wishname");
                String description = rs.getString("description");
                String url = rs.getString("url");
                String price = rs.getString("price");
                if (wishlistname.equals(currentWishlist)){
                    wishes.add(new Wish(wishname, description, url, price));
                } else {
                    wishes = new ArrayList<>(List.of(new Wish(wishname, description, url, price)));
                    wishlistDTO = new WishlistDTO(wishlistname, wishes);
                    wishListList.add(wishlistDTO);
                    userDTO = new UserDTO(userid, name, userusername, password, null);
                    currentWishlist = wishlistname;
                }
            }
            return userDTO;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void addWishToWishlist(Wish wish, int wishlistid) {
        try {
            String SQL = "INSERT INTO wish (wishname, description, url, price, wishlistid) VALUES (?, ?, ?, ?,?)";
            PreparedStatement ps = connection().prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, wish.getName());
            ps.setString(2, wish.getDescription());
            ps.setString(3, wish.getUrl());
            ps.setString(4, wish.getPrice());
            ps.setInt(5, wishlistid);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void addWishlist(int userid, String wishlistName){
        try{
            String SQL = "INSERT INTO wishlist (wishlistName, userid) VALUES (?,?)";
            PreparedStatement ps = connection().prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, wishlistName);
            ps.setInt(2, userid);
            ps.executeUpdate();
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }
    }

    public void deleteWishlist(int wishlistId) {
        try {
            String SQL = "SELECT wishlistid FROM wishlist WHERE wishlistid = ?";
            PreparedStatement ps = connection().prepareStatement(SQL);
            ps.setInt(1, wishlistId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                wishlistId = rs.getInt("wishlistid");
            }

            SQL = "DELETE FROM wish WHERE wishlistid = ?";
            ps = connection().prepareStatement(SQL);
            ps.setInt(1, wishlistId);
            ps.executeUpdate();

            SQL = "DELETE FROM wishlist WHERE wishlistid = ?";
            ps = connection().prepareStatement(SQL);
            ps.setInt(1, wishlistId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void deleteWish(int wishId) {
       try {
           String SQL = "SELECT wishid FROM wish WHERE wishid = ?";
           PreparedStatement ps = connection().prepareStatement(SQL);
           ps.setInt(1, wishId);
           ResultSet rs = ps.executeQuery();
           if (rs.next()) {
               wishId = rs.getInt("wishid");
           }
           SQL = "DELETE FROM wish WHERE wishid = ?";
           ps = connection().prepareStatement(SQL);
           ps.setInt(1, wishId);
           ps.executeUpdate();
       } catch (SQLException e) {
           System.out.println(e.getMessage());
           throw new RuntimeException(e);
       }
    }


}