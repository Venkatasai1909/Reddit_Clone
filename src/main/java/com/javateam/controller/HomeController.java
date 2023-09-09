package com.javateam.controller;

import com.javateam.model.*;
import com.javateam.service.MediaService;
import com.javateam.service.PostService;
import com.javateam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.*;

@Controller
public class HomeController {
    private PostService postService;
    private UserService userService;
    private MediaService mediaService;
    @Autowired
    public HomeController(PostService postService, UserService userService,MediaService mediaService) {
        this.postService = postService;
        this.userService = userService;
        this.mediaService=mediaService;
    }

    @GetMapping("/posts")
    public String getAllPosts(Model model) {
        List<Post> posts = postService.findAll();

        Map<Integer,Media> mediaMap= new HashMap<Integer,Media>();
        for (Post post : posts) {
            if (post.getMediaId() != null) {
                Media media = mediaService.viewById(post.getMediaId());
                mediaMap.put(post.getMediaId(),media);
            }
        }
        model.addAttribute("posts", posts);
        model.addAttribute("mediaMap", mediaMap);
        return "home-page";
    }
    @GetMapping("/display")
    public ResponseEntity<byte[]> displayImage(@RequestParam("id") Integer id) throws IOException, SQLException
    {
        Media media = mediaService.viewById(id);
        byte [] mediaBytes = null;
        mediaBytes = media.getMedia().getBytes(1,(int) media.getMedia().length());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(mediaBytes);
    }

    @GetMapping("/create-post")
    public String createPost(Model model) {
        Post post = new Post();

        List<Subreddit> subreddits = postService.findAllSubreddit();

        model.addAttribute("post", post);

        model.addAttribute("subreddits", subreddits);

        return "create-post";
    }

    @PostMapping("/save-post")
    public String savePost(@ModelAttribute("post") Post post, @RequestParam(value="media", required = false) MultipartFile file) throws IOException, SerialException, SQLException {
        if(post.getPostId() == null) {
            post.setCreatedAt(Instant.now());
        } else {
            post.setUpdatedAt(Instant.now());
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        post.setEmail(authentication.getName());
        User user = userService.findByEmail(post.getEmail());
        post.setUser(user);

        if(file!=null) {
            byte[] bytes = file.getBytes();
            Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);

            Media media = new Media();
            media.setMedia(blob);
            media.setContentType(file.getContentType());
            mediaService.create(media);
            Integer mediaId=media.getId();
            post.setMediaId(mediaId);
        }

        postService.save(post);

        return "redirect:/posts";
    }

    @GetMapping("/{postId}")
    public String getPost(@PathVariable Integer postId, Model model) {
        Post post = postService.findById(postId);

        model.addAttribute("post", post);

        return "post-page";
    }

    @GetMapping("/create-community")
    public String createCommunity(Model model) {
        Subreddit subreddit = new Subreddit();

        model.addAttribute("subreddit", subreddit);

        return "create-subreddit";
    }

    @PostMapping("/save-subreddit")
    public String saveSubreddit(@ModelAttribute("subreddit")Subreddit subreddit) {
        subreddit.setCreatedDate(Instant.now());
        postService.save(subreddit);

        return "redirect:/posts";
    }

    @PostMapping("/update-post/{postId}")
    public String updatePost(@PathVariable Integer postId, Model model) {
        Post post = postService.findById(postId);

        model.addAttribute("post", post);

        return "create-post";
    }

    @PostMapping("/delete-post/{postId}")
    public String deletePost(@PathVariable Integer postId) {
        postService.deleteById(postId);

        return "redirect:/posts";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/posts/upvote/{postId}/{voteType}")
    public String upvote(@PathVariable Integer postId, @RequestParam String email, @PathVariable VoteType voteType, Model model) {
        Post post = postService.findById(postId);
        User user = userService.findByEmail(email);

        Vote existingVote = postService.findVoteByUserAndPost(user, post);

        if (existingVote != null) {
            if (existingVote.getVoteType() != voteType) {
                existingVote.setVoteType(voteType);
                postService.updateVote(existingVote);

                Integer voteCount = post.getVoteCount();
                if (voteType == VoteType.UPVOTE) {
                    post.setVoteCount(voteCount + 1);
                } else {
                    post.setVoteCount(voteCount - 1);
                }
            }
        } else {
            Vote vote = new Vote();
            vote.setPost(post);
            vote.setUser(user);
            vote.setVoteType(voteType);
            postService.createVote(vote);

            Integer voteCount = post.getVoteCount();
            if (voteType == VoteType.UPVOTE) {
                post.setVoteCount(voteCount + 1);
            } else {
                post.setVoteCount(voteCount - 1);
            }
        }

        model.addAttribute("voteType", voteType);

        postService.save(post);

        return "redirect:/posts";
    }

    @GetMapping("/posts/downvote/{postId}/{voteType}")
    public String downvote(@PathVariable Integer postId, @RequestParam String email, @PathVariable VoteType voteType, Model model) {
        Post post = postService.findById(postId);
        User user = userService.findByEmail(email);

        Vote existingVote = postService.findVoteByUserAndPost(user, post);

        if (existingVote != null) {
            if (existingVote.getVoteType() != voteType) {
                existingVote.setVoteType(voteType);
                postService.updateVote(existingVote);

                Integer voteCount = post.getVoteCount();
                if (voteType == VoteType.UPVOTE) {
                    post.setVoteCount(voteCount + 1);
                } else {
                    post.setVoteCount(voteCount - 1);
                }
            }
        } else {
            Vote vote = new Vote();
            vote.setPost(post);
            vote.setUser(user);
            vote.setVoteType(voteType);
            postService.createVote(vote);

            Integer voteCount = post.getVoteCount();
            if (voteType == VoteType.UPVOTE) {
                post.setVoteCount(voteCount + 1);
            } else {
                post.setVoteCount(voteCount - 1);
            }
        }

        model.addAttribute("voteType", voteType);

        postService.save(post);

        return "redirect:/posts";
    }

}
