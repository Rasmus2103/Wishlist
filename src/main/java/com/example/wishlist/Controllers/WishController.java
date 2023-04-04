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
        List<WishlistDTO> wishlistDTOS = repositoryDB.getWishlists();
        model.addAttribute("wishlistDTO", wishlistDTOS);
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
        return "registerwishlist";
    }

    @PostMapping("wishes/{userid}/add")
    public String addWishList(@PathVariable("userid") int userid, @ModelAttribute("wishlistDTO") WishlistDTO wishlistDTO) {
        String wishlistName = wishlistDTO.getName();
        int wishlistID = repositoryDB.addWishlist(wishlistName);
        repositoryDB.addWishListToUser(userid, wishlistID);
        return "redirect:/wishlist";
    }



    /*@PostMapping("wishes/{userid}/add")
    public String addWishList(@PathVariable("userid") int userid, @ModelAttribute("wishlistDTO") WishlistDTO wishlistDTO, int wishlistID, int wishlistID, String wishlistName) {
        String wishlistName = wishlistDTO.getName();
        repositoryDB.addWishlist(wishlistName);
        repositoryDB.addWishListToUser(userid, wishlistID);
       // repositoryDB.addWishListToUser(userid, wishlistID);
        return "redirect:/wishlist/wishes";
    }*/

    /*@PostMapping("wishes/{userid}/adduser")
    public String addWishListToUser(@PathVariable("userid") int userid, int wishlistID) {
        repositoryDB.addWishListToUser(userid, wishlistID);
        return "redirect:/wishlist/wishes";
    }*/

    /*@GetMapping("wishes/slet/{wishlistid}")
    public String deleteWishlist(@PathVariable("wishlistid") int wishlistId, Model model) {
        repositoryDB.deleteWishlist(wishlistId);
        model.addAttribute("wishlist", repositoryDB.getWishlists());
        return "redirect:/wishlist/wishes";
    }*/
}