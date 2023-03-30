package com.example.wishlist.Controllers;

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

    @GetMapping("wishes/{username}")
    public String getWishes(@PathVariable("username") String username, Model model) {
        List<WishlistDTO> wishes = repositoryDB.wishes(username);
        model.addAttribute("wishes", wishes);
        return "wishes";
    }

    @GetMapping("register")
        public String register() {
            return "register";
        }

    @PostMapping("register")
        public String registerUser(@ModelAttribute User user) {
            repositoryDB.registerUser(user);
            return "redirect:/wishlist";
    }

}



