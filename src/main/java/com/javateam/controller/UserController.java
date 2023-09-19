package com.javateam.controller;

import com.javateam.model.Media;
import com.javateam.model.Subreddit;
import com.javateam.model.User;
import com.javateam.service.MediaService;
import com.javateam.service.PostService;
import com.javateam.service.SubredditService;
import com.javateam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class UserController {
    private UserService userService;
    private PasswordEncoder passwordEncoder;
    private PostService postService;
    private MediaService mediaService;
    private SubredditService subredditService;
    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder,
                            MediaService mediaService, PostService postService, SubredditService subredditService) {
        this.userService = userService;
        this.mediaService = mediaService;
        this.passwordEncoder = passwordEncoder;
        this.postService = postService;
        this.subredditService = subredditService;
    }

    @GetMapping("/register")
    public String register(Model model) {
        User user = new User();

        model.addAttribute("user", user);

        return "register-user";
    }

    @PostMapping("/registerUser")
    public String registerUser(@ModelAttribute("user")User user) {
        if(userService.findByEmail(user.getEmail()) != null) {
            return "register-user";
        }

        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        String bcryptPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(bcryptPassword);
        userService.saveUser(user);

        return "redirect:/posts";
    }

    @GetMapping("/updateUser")
    public String updateUser(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(authentication.getName());
        model.addAttribute("user",user);
        List<Subreddit> subreddits = subredditService.findAllSubreddit();
        model.addAttribute("subreddits", subreddits);
        return "update-user";
    }

    @PostMapping("/updateUser")
    public String saveUpdatedUser(@ModelAttribute("user") User updatedUser,
                                  @RequestParam(value="email")String email,
                                  @RequestParam(value="file", required = false) MultipartFile file,Model model)
            throws IOException, SerialException, SQLException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User existingUser = userService.findByEmail(authentication.getName());

        if(!email.equals(authentication.getName())) {
            return "update-user";
        }

        existingUser.setUsername(updatedUser.getUsername().trim());

        if(updatedUser.getPassword()!=null && !updatedUser.getPassword().isEmpty()){
            String bcryptPassword = passwordEncoder.encode(updatedUser.getPassword());
            existingUser.setPassword(bcryptPassword);
        }

        if(file!=null && !file.isEmpty()) {
            byte[] bytes = file.getBytes();
            Blob blob = new SerialBlob(bytes);

            if (existingUser.getMedia() != null) {
                existingUser.getMedia().setMedia(blob);
                existingUser.getMedia().setContentType(file.getContentType());
                mediaService.create(existingUser.getMedia());
            } else {
                Media media = new Media();
                media.setMedia(blob);
                media.setContentType(file.getContentType());
                mediaService.create(media);
                existingUser.setMedia(media);
            }
        }
        userService.saveUser(existingUser);

        return "redirect:/posts";
    }

}
