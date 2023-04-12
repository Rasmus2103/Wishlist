package com.example.wishlist.Controllers;

import com.example.wishlist.DTO.UserDTO;
import com.example.wishlist.DTO.WishlistDTO;
import com.example.wishlist.Models.User;
import com.example.wishlist.Repositories.IRepositoryDB;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("wishlist")
public class WishController {
    private IRepositoryDB repositoryDB;

    public WishController(ApplicationContext context, @Value("wishlist_DB") String impl) {
        repositoryDB =(IRepositoryDB) context.getBean(impl);
    }

    @GetMapping("")
    public String getUsers(Model model) {
        List<User> users = repositoryDB.getUsers();
        model.addAttribute("user", users);
        return "index";
    }

    @GetMapping("wishes/{id}")
    public String getWishes(@PathVariable("id") int userid, Model model) {
        UserDTO userDTO = repositoryDB.userDTOByID(userid);
        model.addAttribute("userDTO", userDTO);
        return "wishes";
    }

    @GetMapping("register")
    public String register(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "register";
    }

    @PostMapping("register")
    public String registerUser(@ModelAttribute("user") User user) {
        repositoryDB.registerUser(user);
        return "redirect:/wishlist";
    }

    @GetMapping("wishes/{userid}/add")
    public String addWishList(@PathVariable("userid") int userid, Model model) {
        WishlistDTO wishlistDTO = new WishlistDTO();
        model.addAttribute("wishlistDTO", wishlistDTO);

        List<String> wishes = repositoryDB.getWishes();
        model.addAttribute("wishes", wishes);

        return "registerwishlist";
    }

    @GetMapping("addwish/{userid}")
    public String addWishToWishList(@PathVariable("userid") int userid, Model model) {
        WishlistDTO wishlistDTO = new WishlistDTO();
        model.addAttribute("wishlistDTO", wishlistDTO);
        return "registerwish";
    }

    @PostMapping("addwish")
    public String addWishToWishList(Model model) {
        WishlistDTO wishlistDTO = new WishlistDTO();
        model.addAttribute("wishlistDTO", wishlistDTO);
        return "redirect:/wishlist";
    }

    @PostMapping("wishes/{userid}/add")
    //public String addWishList(@PathVariable("userid") int userid, @ModelAttribute("wishlistDTO") WishlistDTO wishlistDTO) {
        public String addWishList(@PathVariable("userid") int userid, @ModelAttribute("wishlistDTO") WishlistDTO wishlistDTO, Integer wishlistID, String wishlistName){
        wishlistDTO.setUserId(userid);
        wishlistDTO.setWishlistname(wishlistName);
        System.out.println(wishlistDTO.getUserId() + " " + wishlistDTO.getWishlistname());
        repositoryDB.addWishlist(wishlistDTO.getUserId(), wishlistDTO.getWishlistname());
        repositoryDB.addWishListToUser(userid, wishlistID);
        System.out.println(wishlistDTO.getUserId() + " " + wishlistDTO.getWishlistname());

        //repositoryDB.addWishListToUser(userid, wishlistDTO.g);
        //return addWishListToUser(userid, wishlistDTO.getUserId());
        return "redirect:/wishlist/wishes/{userid}";
    }

    /*@PostMapping("wishes/{userid}/adduser")
    public String addWishListToUser(@PathVariable("userid") int userid, Integer wishlistID) {
        repositoryDB.addWishListToUser(userid, wishlistID);
        return "redirect:/wishlist/wishes";
    }*/

    @GetMapping("wishes/slet/{userid}")
    public String deleteWishlist(@PathVariable("userid") int wishlistId, Model model) {
        repositoryDB.deleteWishlist(wishlistId);
        model.addAttribute("wishlist", repositoryDB.getWishlists());
        return "redirect:/wishlist/wishes/{userid}";
    }
}