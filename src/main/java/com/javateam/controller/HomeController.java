package com.javateam.controller;

import com.javateam.model.*;
import com.javateam.service.PostService;
import com.javateam.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@Controller
public class HomeController {
    private PostService postService;
    private UserService userService;
    public HomeController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @GetMapping("/posts")
    public String getAllPosts(Model model) {
        List<Post> posts = postService.findAll();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();


        model.addAttribute("posts", posts);

        return "home-page";
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
    public String savePost(@ModelAttribute("post") Post post) {
        if(post.getPostId() == null) {
            post.setCreatedDate(Instant.now());
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
            // User has already voted, check if it's the same vote type
            if (existingVote.getVoteType() != voteType) {
                // Change the vote type
                existingVote.setVoteType(voteType);
                postService.updateVote(existingVote);

                // Update the vote count based on the new vote type
                Integer voteCount = post.getVoteCount();
                if (voteType == VoteType.UPVOTE) {
                    post.setVoteCount(voteCount + 1); // +2 because user changes from downvote to upvote
                } else {
                    post.setVoteCount(voteCount - 1); // -2 because user changes from upvote to downvote
                }
            }
        } else {
            // User has not voted for this post, create a new vote record
            Vote vote = new Vote();
            vote.setPost(post);
            vote.setUser(user);
            vote.setVoteType(voteType);
            postService.createVote(vote);

            // Update the vote count based on the new vote type
            Integer voteCount = post.getVoteCount();
            if (voteType == VoteType.UPVOTE) {
                post.setVoteCount(voteCount + 1);
            } else {
                post.setVoteCount(voteCount - 1);
            }
        }

        model.addAttribute("voteType", voteType);

        // Save the updated post
        postService.save(post);

        return "redirect:/posts";
    }

    @GetMapping("/posts/downvote/{postId}/{voteType}")
    public String downvote(@PathVariable Integer postId, @RequestParam String email, @PathVariable VoteType voteType, Model model) {
        Post post = postService.findById(postId);
        User user = userService.findByEmail(email);

        Vote existingVote = postService.findVoteByUserAndPost(user, post);

        if (existingVote != null) {
            // User has already voted, check if it's the same vote type
            if (existingVote.getVoteType() != voteType) {
                // Change the vote type
                existingVote.setVoteType(voteType);
                postService.updateVote(existingVote);

                // Update the vote count based on the new vote type
                Integer voteCount = post.getVoteCount();
                if (voteType == VoteType.UPVOTE) {
                    post.setVoteCount(voteCount + 1); // +2 because user changes from downvote to upvote
                } else {
                    post.setVoteCount(voteCount - 1); // -2 because user changes from upvote to downvote
                }
            }
        } else {
            // User has not voted for this post, create a new vote record
            Vote vote = new Vote();
            vote.setPost(post);
            vote.setUser(user);
            vote.setVoteType(voteType);
            postService.createVote(vote);

            // Update the vote count based on the new vote type
            Integer voteCount = post.getVoteCount();
            if (voteType == VoteType.UPVOTE) {
                post.setVoteCount(voteCount + 1);
            } else {
                post.setVoteCount(voteCount - 1);
            }
        }

        model.addAttribute("voteType", voteType);

        // Save the updated post
        postService.save(post);

        return "redirect:/posts";
    }


}
