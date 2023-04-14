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

    @GetMapping("news")
    public String getNews() {
        return "news";
    }

    @GetMapping("FAQ")
    public String getFAQ() {
        return "FAQ";
    }

    @GetMapping("about")
    public String getAbout() {
        return "about";
    }

    @GetMapping("wishes/{id}")
    public String getWishes(@PathVariable("id") int userid, Model model) {
        User user = repositoryDB.getUser(userid);
        model.addAttribute("user", user);

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

    @GetMapping("wishes/{userid}/createwishlist")
    public String addWishList(@PathVariable("userid") int userid, Model model) {
        WishlistDTO wishlistDTO = new WishlistDTO(userid);
        model.addAttribute("wishlistDTO", wishlistDTO);

        List<Wish> wishes = repositoryDB.getWishes(userid);
        model.addAttribute("wishes", wishes);

        return "registerwishlist";
    }

    @PostMapping("wishes/{userid}/createwishlist")
    public String addWishList(@ModelAttribute("wishlistDTO") WishlistDTO wishlistDTO, String name) {
        repositoryDB.addWishlist(wishlistDTO.getUserid(), name);
        return "redirect:/wishlist";
    }

    @GetMapping("addwish/{wishlistid}")
    public String addWishToWishList(@PathVariable("wishlistid") int wishlistid, Model model) {
        Wish wish = new Wish(wishlistid);
        model.addAttribute("wishes", wish);
        return "registerwish";
    }

    @PostMapping("addwish/{wishlistid}")
    public String addWishToWishList(@ModelAttribute("wishes") Wish wish) {
        repositoryDB.addWishToWishlist(wish, wish.getWishlistid());
        return "redirect:/wishlist";
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

    @GetMapping("login")
    public String login() {
        return "login";
    }

    @PostMapping("login")
    public String login(@RequestParam String username, @RequestParam String password, Model model) {
        User user = repositoryDB.getUser(repositoryDB.getUserid(username));
        if (user != null && user.getPassword().equals(password)) {
            return "wishes";
        } else {
            return "error";
        }
    }

}