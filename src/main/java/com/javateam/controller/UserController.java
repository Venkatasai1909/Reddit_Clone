package com.javateam.controller;

import com.javateam.model.User;
import com.javateam.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;

@Controller
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/register")
    public String register(Model model) {
        User user = new User();

        model.addAttribute("user", user);

        return "register-user";
    }

    @PostMapping("/registerUser")
    public String registerUser(@ModelAttribute("user")User user) {
        if(user.getUserId() != null){
            user.setCreated(Instant.now());
        }

        user.setEnabled(true);

        userService.saveUser(user);

        return "redirect:/posts";
    }


}
