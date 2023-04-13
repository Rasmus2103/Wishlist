package com.example.wishlist.Controllers;

import com.example.wishlist.DTO.UserDTO;
import com.example.wishlist.DTO.WishlistDTO;
import com.example.wishlist.Models.User;
import com.example.wishlist.Repositories.IRepositoryDB;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
/*
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
    */

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

    //TODO: Fix this, and create relevant mapping for the html pages
    /*
    @GetMapping("news")
    public String news() {
        return "news";
    }


    @GetMapping("about")
    public String about() {
        return "about";
    }


    @GetMapping("FAQ")
    public ResponseEntity<String> getFAQ() throws IOException {
        // Load the HTML document from the resource loader
        ResourceLoader resourceLoader = resourceLoader.getResource("classpath:example.html");
        String htmlContent = new String(Files.readAllBytes(resource.getFile().toPath()), StandardCharsets.UTF_8);

        // Return the HTML content as a ResponseEntity with OK status
        return new ResponseEntity<>(htmlContent, HttpStatus.OK);
    }

    */
}