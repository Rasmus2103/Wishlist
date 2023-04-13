package com.example.wishlist.Repositories;

import com.example.wishlist.DTO.UserDTO;
import com.example.wishlist.DTO.WishlistDTO;
import com.example.wishlist.Models.User;
import com.example.wishlist.Models.Wish;
import org.springframework.aop.scope.ScopedObject;
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

    /*private String SQL;
    private Statement stmt;
    private ResultSet rs;
    private PreparedStatement ps;
    private Connection con;*/

    public Connection connection() {
        try {
            Connection con = DriverManager.getConnection(db_url,uid,pwd);
            return con;
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

    public WishlistDTO findWishlistById(int wishlistId) {
        WishlistDTO wishlistDTO = null;
        try {
            String SQL = "SELECT wishname, description, url, price, wishlistname FROM wish w \n" +
                    "JOIN wishlistwish ww ON w.wishid = ww.wishid \n" +
                    "JOIN wishlist wl ON wl.wishlistid = w.wishid WHERE wishlistid = ?";
            PreparedStatement ps = connection().prepareStatement(SQL);
            ps.setInt(1, wishlistId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int wishlistid = rs.getInt("wishlistid");
                String wishlistName = rs.getString("wishlistname");
                List<Wish> wishes = getWishes(wishlistId);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return wishlistDTO;
    }

    public List<WishlistDTO> getWishlists(int userId) {
        List<WishlistDTO> wishlists = new ArrayList<>();
        try {
            String SQL = "SELECT wishlistname, w.wishlistid, name FROM wishlist w \n" +
                    "JOIN userwishlist uw ON w.wishlistid = uw.wishlistid\n" +
                    "JOIN user u ON u.userid = w.wishlistid WHERE u.userid = ?;";
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
                    userDTO = new UserDTO(userid, name, userusername, password, wishListList);
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
            int wishId = 0;

            String SQL = "INSERT INTO wish (wishname, description, url, price) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = connection().prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, wish.getName());
            ps.setString(2, wish.getDescription());
            ps.setString(3, wish.getUrl());
            ps.setString(4, wish.getPrice());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                wishId = rs.getInt(1);
            }

            String SQL2 = "SELECT wishlistid FROM wishlistwish WHERE wishlistid = ?";
            PreparedStatement ps2 = connection().prepareStatement(SQL2);
            ps2.setInt(1, wishlistid);
            ResultSet rs2= ps.executeQuery();
            if (rs.next()) {
                wishlistid = rs2.getInt(1);
            }

            String SQL3 = "INSERT INTO wishlistwish VALUES (?,?)";
            PreparedStatement ps3 = connection().prepareStatement(SQL3);
            ps3.executeUpdate();
            ps3.setInt(1, wishlistid);
            ps3.setInt(2, wishId);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void addWishListToUser(int userid, int wishlistID){
        try {
            String SQL = "INSERT INTO userwishlist (userid, wishlistID) VALUES (?, ?)";
            PreparedStatement ps = connection().prepareStatement(SQL);
            ps.setInt(1, userid);
            ps.setInt(2, wishlistID);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
    public void addWishlist(int userid, String wishlistName){
        try{
            String SQL = "INSERT INTO wishlist (wishlistName) VALUES (?)";
            PreparedStatement ps = connection().prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, wishlistName);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            int wishlistID = 0;
            if(rs.next()){
                wishlistID = rs.getInt(1);
                System.out.println(wishlistID);
            }
            String SQL2 = "INSERT INTO userwishlist (userid, wishlistID) VALUES (?, ?)";
            PreparedStatement ps2 = connection().prepareStatement(SQL2);
            ps2.setInt(1, userid);
            ps2.setInt(2, wishlistID);
            ps2.executeUpdate();
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }
    }

    public List<Wish> getWishes(int userId) {
        List<Wish> wishes = new ArrayList<>();
        try {
            String SQL = "SELECT wl.wishlistname, w.wishname, w.description, w.url, w.price\n" +
                    "FROM user u\n" +
                    "JOIN userwishlist uw ON u.userid = uw.userid\n" +
                    "JOIN wishlist wl ON wl.wishlistid = uw.wishlistid\n" +
                    "JOIN wishlistwish ww ON ww.wishlistid = wl.wishlistid\n" +
                    "JOIN wish w ON w.wishid = ww.wishid WHERE u.userid = ?";
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

    public void deleteWishlist(int wishlistId) {
        try {
            String SQL = "SELECT wishlistid FROM wishlist WHERE wishlistid = ?";
            PreparedStatement ps = connection().prepareStatement(SQL);
            ps.setInt(1, wishlistId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                wishlistId = rs.getInt("wishlistid");
            }

            SQL = "DELETE FROM wishlistwish WHERE wishlistid = ?";
            ps = connection().prepareStatement(SQL);
            ps.setInt(1, wishlistId);
            ps.executeUpdate();

            SQL = "DELETE FROM userwishlist WHERE wishlistid = ?";
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
           SQL = "DELETE FROM wishlistwish WHERE wishid = ?";
           ps = connection().prepareStatement(SQL);
           ps.setInt(1, wishId);
           ps.executeUpdate();

           SQL = "DELETE FROM wish WHERE wishid = ?";
           ps = connection().prepareStatement(SQL);
           ps.setInt(1, wishId);
           ps.executeUpdate();

       } catch (SQLException e) {
           System.out.println(e.getMessage());
           throw new RuntimeException(e);
       }
    }

    /*public WishlistDTO getWishListById(int id) {
        WishlistDTO wishlistDTO = null;
        try {
            SQL = "SELECT wishlistname FROM wishlist " +
                    "JOIN wishlistwish ON Wishlist.wishlistid = wishlistwish.wishlistid" +
                    "JOIN Wish ON wishlistwish.wishid = Wish.wishid" +
                    "WHERE ";
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }*/


    //CREATE Superhero
    /*public void addSuperhero(SuperHeroForm form) {
        try {
            // ID's
            int cityId = 0;
            int heroId = 0;
            List<Integer> powerIDs = new ArrayList<>();
            // find city_id
            String SQL1 = "select id from city where cityname = ?";
            PreparedStatement pstmt = connection().prepareStatement(SQL1);
            pstmt.setString(1, form.getCity());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                cityId = rs.getInt("id");
            }
            // insert row in superhero table
            String SQL2 = "insert into superhero (heroname, realname, creationyear, cityid) " +
                    "values(?, ?, ?, ?)";
            pstmt = connection().prepareStatement(SQL2, Statement.RETURN_GENERATED_KEYS); // return autoincremented key
            pstmt.setString(1, form.getHeroName());
            pstmt.setString(2, form.getRealName());
            pstmt.setInt(3, form.getCreationYear());
            pstmt.setInt(4, cityId);
            int rows = pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                heroId = rs.getInt(1);
            }
            // find power_ids
            String SQL3 = "select id from superpower where powername = ?;";
            pstmt = connection().prepareStatement(SQL3);
            for (String power : form.getPowerList()) {
                pstmt.setString(1, power);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    powerIDs.add(rs.getInt("id"));
                }
            }
            // insert entries in superhero_powers join table
            String SQL4 = "insert into superheropower values (?,?);";
            pstmt = connection().prepareStatement(SQL4);
            for (int i = 0; i < powerIDs.size(); i++) {
                pstmt.setInt(1, heroId);
                pstmt.setInt(2, powerIDs.get(i));
                rows = pstmt.executeUpdate();
            }
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }
    //UPDATE Superhero
    public void updateHero(int id, SuperHeroForm form) {
        try {
            String cityQuery = "SELECT id FROM city WHERE cityname = ?";
            PreparedStatement cityPs = connect().prepareStatement(cityQuery);
            cityPs.setString(1, form.getCity());
            ResultSet cityRs = cityPs.executeQuery();
            int cityId = 0;
            if (cityRs.next()) {
                cityId = cityRs.getInt("id");
            }
            cityRs.close();
            cityPs.close();
            // Then, update the superhero using the fetched cityid
            String SQL = "UPDATE superhero SET heroname = ?, realname = ?, creationyear = ?, cityid = ? WHERE id = ?";
            PreparedStatement ps = connect().prepareStatement(SQL);
            ps.setString(1, form.getHeroName());
            ps.setString(2, form.getRealName());
            ps.setInt(3, form.getCreationYear());
            ps.setInt(4, cityId);
            ps.setInt(5, id);
            ps.executeUpdate();
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
        deleteSuperheroPowers(id);
        addSuperheroPowers(id, form.getPowerList());
    }
    public void deleteSuperheroPowers(int heroId) {
        try {
            SQL = "DELETE FROM superheropower WHERE superheroid = ?";
            ps = connect().prepareStatement(SQL);
            ps.setInt(1, heroId);
            ps.executeUpdate();
            ps.close();
            connect().close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void addSuperheroPowers(int heroId, List<String> powers) {
        PreparedStatement localPs;
        try {
            String SQL = "INSERT INTO superheropower (superheroid, superpowerid) VALUES (?, ?)";
            localPs = connect().prepareStatement(SQL);
            for (String powerName : powers) {
                int powerId = getPowerId(powerName);
                localPs.setInt(1, heroId);
                localPs.setInt(2, powerId);
                localPs.addBatch();
            }
            localPs.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public int getPowerId(String powerName) {
        int powerId = 0;
        String SQL = "SELECT id FROM superpower WHERE powername = ?";
        try (Connection connection = connect();
             PreparedStatement ps = connection.prepareStatement(SQL)) {
            ps.setString(1, powerName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    powerId = rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return powerId;
    }
    //DELETE Superhero
    public void deleteHero(int heroId) {
        try {
            SQL = "SELECT id FROM superhero WHERE id = ?";
            ps = connect().prepareStatement(SQL);
            ps.setInt(1, heroId);
            rs = ps.executeQuery();
            if (rs.next()) {
                heroId = rs.getInt("id");
            }
            SQL = "DELETE FROM superheropower WHERE superheroid = ?";
            ps = connect().prepareStatement(SQL);
            ps.setInt(1, heroId);
            ps.executeUpdate();
            SQL = "DELETE FROM superhero WHERE id = ?";
            ps = connect().prepareStatement(SQL);
            ps.setInt(1, heroId);
            ps.executeUpdate();
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }
*/


}