package com.example.wishlist.Repositories;

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
        int userId = 0;
        try {
            String SQL = "select userid from user where username = ?";
            PreparedStatement ps = connection().prepareStatement(SQL);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                userId = rs.getInt("userid");
            }
            return userId;
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

    public WishlistDTO getSpecificWishlists(int wishlistid) {
        WishlistDTO wishlist = null;
        try {
            String SQL = "SELECT wishlistname, wishlistid FROM wishlist WHERE wishlistid = ?;";
            PreparedStatement ps = connection().prepareStatement(SQL);
            ps.setInt(1, wishlistid);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                String name = rs.getString("wishlistname");
                wishlistid = rs.getInt("wishlistid");
                wishlist = new WishlistDTO(name, wishlistid);
            }
            return wishlist;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<Wish> getSpecificWishes(int wishlistid) {
        List<Wish> wishes = new ArrayList<>();
        try {
            String SQL = "SELECT wishid, wishname, description, url, price FROM wish\n" +
                    "JOIN wishlist ON wish.wishlistid = wishlist.wishlistid\n" +
                    "WHERE wishlist.wishlistid = ?;";
            PreparedStatement ps = connection().prepareStatement(SQL);
            ps.setInt(1, wishlistid);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                int wishid = rs.getInt("wishid");
                String wish = rs.getString("wishname");
                String desription = rs.getString("description");
                String url = rs.getString("url");
                String price = rs.getString("price");

                wishes.add(new Wish(wishid ,wish, desription, url, price, wishlistid));
            }
            return wishes;
        } catch (SQLException e){
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }
    }

    public void registerUser(User user) {
        try {
            if(usernameExists(user.getUsername())) {
                throw new IllegalArgumentException("Username already exists");
            }
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

    public void addWish(Wish wish, int wishlistid) {
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
            String SQL = "INSERT INTO wishlist (wishlistname, userid) VALUES (?,?)";
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

    public boolean usernameExists(String username) {
        try {
            String SQL = "SELECT COUNT(*) FROM user WHERE username = ?";
            PreparedStatement ps = connection().prepareStatement(SQL);
            ps.setString(1, username);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }
        return false;
    }


}