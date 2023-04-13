package com.example.wishlist.Controllers;

import com.example.wishlist.DTO.UserDTO;
import com.example.wishlist.DTO.WishlistDTO;
import com.example.wishlist.Models.User;
import com.example.wishlist.Models.Wish;
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

        List<WishlistDTO> wishLists = repositoryDB.getWishlists(userid);
        model.addAttribute("wishlists", wishLists);

        List<Wish> wishes = repositoryDB.getWishes(userid);
        model.addAttribute("wishes", wishes);
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

        List<Wish> wishes = repositoryDB.getWishes(userid);
        model.addAttribute("wishes", wishes);

        return "registerwishlist";
    }

    @GetMapping("addwish/{wishlistid}")
    public String addWishToWishList(@PathVariable("wishlistid") int wishlistid, Model model) {
        Wish wish = new Wish();
        model.addAttribute("wishes", wish);
        return "registerwish";
    }

    @PostMapping("addwish/{wishlistid}")
    public String addWishToWishList(@ModelAttribute("wishes") Wish wish, @PathVariable int wishlistid) {
        repositoryDB.addWishToWishlist(wish, wishlistid);
        return "redirect:/wishlist";
    }

    @PostMapping("wishes/{userid}/add")
    //public String addWishList(@PathVariable("userid") int userid, @ModelAttribute("wishlistDTO") WishlistDTO wishlistDTO) {
        public String addWishList(@PathVariable("userid") int userid, @ModelAttribute("wishlistDTO") WishlistDTO wishlistDTO, Integer wishlistID, String wishlistName){
        wishlistDTO.setUserId(userid);
        wishlistDTO.setName(wishlistName);
        System.out.println(wishlistDTO.getUserId() + " " + wishlistDTO.getName());
        repositoryDB.addWishlist(wishlistDTO.getUserId(), wishlistDTO.getName());
        //repositoryDB.addWishListToUser(userid, wishlistDTO.g);
        return addWishListToUser(userid, wishlistDTO.getUserId());
        // repositoryDB.addWishListToUser(userid, wishlistID);
    }

    @PostMapping("wishes/{userid}/adduser")
    public String addWishListToUser(@PathVariable("userid") int userid, Integer wishlistID) {
        repositoryDB.addWishListToUser(userid, wishlistID);
        return "redirect:/wishlist/wishes/{userid}";
    }

    @GetMapping("wishes/slet/{userid}")
    public String deleteWishlist(@PathVariable("userid") int wishlistId, Model model) {
        repositoryDB.deleteWishlist(wishlistId);
        model.addAttribute("wishlist", repositoryDB.getWishlists(wishlistId));
        return "redirect:/wishlist/wishes/{userid}";
    }

    @GetMapping("wishes/sletwish/{wishid}")
    public String deleteWish(@PathVariable("wishid") int wishId, Model model){
        repositoryDB.deleteWish(wishId);
        model.addAttribute("wishes", repositoryDB.getWishlists(wishId));
        return "redirect:/wishlist";
    }
}