package com.example.wishlist.DTO;
import java.util.List;

public class UserDTO {

    private int userid;
    private String name;
    private String username;
    private String password;
    private List<WishlistDTO> wishlistdto;

    public UserDTO(int userid, String name, String username, String password, List<WishlistDTO> wishlistdto) {
        this.name = name;
        this.userid = userid;
        this.username = username;
        this.password = password;
        this.wishlistdto = wishlistdto;
    }

    public UserDTO() {

    }

    public int getUserId() {
        return userid;
    }

    public void setUserId(int userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<WishlistDTO> getWishlistdto() {
        return wishlistdto;
    }

    public void setWishlistdto(List<WishlistDTO> wishlistdto) {
        this.wishlistdto = wishlistdto;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }


}
