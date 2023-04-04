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

    private String SQL;
    private Statement stmt;
    private ResultSet rs;
    private PreparedStatement ps;
    private Connection con;

    public Connection connection() {
        try {
            con = DriverManager.getConnection(db_url,uid,pwd);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return con;
    }

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        try {
            SQL = "SELECT * FROM user";
            stmt = connection().createStatement();
            rs = stmt.executeQuery(SQL);
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

    public List<WishlistDTO> getWishlists() {
        List<WishlistDTO> wishlists = new ArrayList<>();
        try {
            SQL = "SELECT * FROM wishlist";
            stmt = connection().createStatement();
            rs = stmt.executeQuery(SQL);
            while(rs.next()) {
                int id = rs.getInt("wishlistid");
                String name = rs.getString("wishlistname");
                wishlists.add(new WishlistDTO(name, id));
            }
            return wishlists;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void registerUser(User user) {
        try {
            SQL = "INSERT INTO user (name, username, password) VALUES (?, ?, ?)";
            ps = connection().prepareStatement(SQL);
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
            SQL = "SELECT name, username, password, wishlistname, wishname, description, url, price " +
                    "FROM user u " +
                    "JOIN userwishlist uw ON u.userid = uw.userid " +
                    "JOIN wishlist wl ON wl.wishlistid = uw.wishlistid " +
                    "JOIN wishlistwish ww ON ww.wishlistid = wl.wishlistid " +
                    "JOIN wish w ON w.wishid = ww.wishid " +
                    "WHERE u.userid = ?";
            ps = connection().prepareStatement(SQL);
            ps.setInt(1, userid);
            rs = ps.executeQuery();
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

    public void addWishListToUser(int userid, int wishlistID) {
        try {
            SQL = "INSERT INTO userwishlist (userid, wishlistID) VALUES (?, ?)";
            ps = connection().prepareStatement(SQL);
            ps.setInt(1, userid);
            ps.setInt(2, wishlistID);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public int addWishlist(String wishlistName) {
        int generatedWishlistId = -1;

        try {
            String SQL = "INSERT INTO wishlist (wishlistName) VALUES (?)";
            ps = connection().prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, wishlistName);
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                generatedWishlistId = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }
        return generatedWishlistId;
    }

    /*public void addWishlist(String wishlistName) {
        try{
            SQL = "INSERT INTO wishlist (wishlistName) VALUES (?)";
            ps = connection().prepareStatement(SQL);
            ps.setString(1, wishlistName);
            ps.executeUpdate();
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }
    }*/


    /*public void deleteWishlist(int wishlistId) {
        try {
            SQL = "SELECT wishlistid FROM wishlist WHERE = ?";
            ps = connection().prepareStatement(SQL);
            ps.setInt(1, wishlistId);
            rs = ps.executeQuery();
            if (rs.next()) {
                wishlistId = rs.getInt("id");
            }

            SQL = "DELETE FROM wishlistwish WHERE wishlistid = ?";
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
    }*/

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
    /*public void addSuperhero(WishlistDTO form) {
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
