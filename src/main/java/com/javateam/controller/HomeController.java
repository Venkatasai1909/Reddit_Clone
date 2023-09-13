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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    private PostService postService;
    private UserService userService;
    private MediaService mediaService;

    @Autowired
    public HomeController(PostService postService, UserService userService, MediaService mediaService) {
        this.postService = postService;
        this.userService = userService;
        this.mediaService = mediaService;
    }

    @GetMapping("/posts")
    public String getAllPosts(Model model) {
        List<Post> posts = postService.findAll();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(authentication.getName());

        if (user != null) {
            model.addAttribute("user", user);
        }

        List<Subreddit> subredditList = postService.findAllSubreddit();

        model.addAttribute("subredditList", subredditList);
        model.addAttribute("posts", posts);

        return "home-page";
    }

    @GetMapping("/display")
    public ResponseEntity<byte[]> displayImage(@RequestParam("id") Integer id) throws IOException, SQLException {
        Media media = mediaService.viewById(id);
        byte[] mediaBytes = null;
        mediaBytes = media.getMedia().getBytes(1, (int) media.getMedia().length());
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
    public String savePost(@ModelAttribute("post") Post post, @RequestParam(value="file", required = false) MultipartFile file,
                           @RequestParam(value="action")String action) throws IOException, SerialException, SQLException {
        if(post.getPostId() == null) {
            post.setCreatedAt(LocalDateTime.now());

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findByEmail(authentication.getName());
            post.setUser(user);

            if(file!=null && !file.isEmpty()) {
                byte[] bytes = file.getBytes();
                Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);

                Media media = new Media();
                media.setMedia(blob);
                media.setContentType(file.getContentType());
                mediaService.create(media);
                post.setMedia(media);
            }

            if(action.equals("Draft")) {
                post.setIsPublished(false);
                post.setCreatedAt(LocalDateTime.now());
            } else if(action.equals("Publish")) {
                post.setIsPublished(true);
                post.setPublishedAt(LocalDateTime.now());
                post.setCreatedAt(LocalDateTime.now());
            }

            postService.save(post);
        } else {
            Post existingPost=postService.findById(post.getPostId());

            existingPost.setDescription(post.getDescription());

            if(file!=null && !file.isEmpty()) {
                byte[] bytes = file.getBytes();
                Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);

                if (existingPost.getMedia() != null) {
                    existingPost.getMedia().setMedia(blob);
                    existingPost.getMedia().setContentType(file.getContentType());
                    mediaService.create(existingPost.getMedia());
                } else {
                    Media media = new Media();
                    media.setMedia(blob);
                    media.setContentType(file.getContentType());
                    mediaService.create(media);
                    existingPost.setMedia(media);
                }
            }

            postService.save(existingPost);
        }

        if(action.equals("Update Draft")){
            return "redirect:/seeDrafts";
        }
        else if(action.equals("Update")){
            return "redirect:/"+post.getPostId();
        }

        return "redirect:/posts";
    }

    @GetMapping("/{postId}")
    public String getPost(@PathVariable Integer postId, Model model) {
        Post post = postService.findById(postId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(authentication.getName());

        if (user != null) {
            model.addAttribute("user", user);
        }

        List<Subreddit> subredditList = postService.findAllSubreddit();

        model.addAttribute("subredditList", subredditList);

        model.addAttribute("post", post);

        return "post-page";
    }

    @GetMapping("/create-community")
    public String createCommunity(Model model) {
        Subreddit subreddit = new Subreddit();
        subreddit.setName("r/");

        model.addAttribute("subreddit", subreddit);

        return "create-subreddit";
    }

    @PostMapping("/save-subreddit")
    public String saveSubreddit(@ModelAttribute("subreddit") Subreddit subreddit) {
        Subreddit existingSubreddit = postService.findBySubredditName(subreddit.getName());
        if (existingSubreddit != null) {
            return "create-subreddit";
        }

        subreddit.setCreatedAt(LocalDateTime.now());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(authentication.getName());
        subreddit.setUser(user);

        postService.save(subreddit);

        return "redirect:/posts";
    }

    @PostMapping("/update-post/{postId}")
    public String updatePost(@PathVariable Integer postId, Model model) {
        Post post = postService.findById(postId);

        model.addAttribute("post", post);

        return "create-post";
    }
    @GetMapping("/drafts")
    public String showDrafts(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(authentication.getName());
        List<Post> draftPostsOfLoggedUser  = postService.findAllDraftPostsOfUser(user.getUserId());
        model.addAttribute("user", user);
        model.addAttribute("posts",draftPostsOfLoggedUser);

        return "profile-page";
    }

    @GetMapping("/posts/{postId}/publish")
    public String publishPost(@PathVariable Integer postId, Model model){
        Post post = postService.findById(postId);
        post.setIsPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        postService.save(post);

        return "redirect:/" + post.getPostId();
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
    public String upvote(@PathVariable Integer postId, @PathVariable VoteType voteType, Model model) {
        Post post = postService.findById(postId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(authentication.getName());

        Vote existingVote = postService.findVoteByUserAndPost(user, post);

        if (existingVote != null) {
            if (existingVote.getVoteType() != voteType) {
                existingVote.setVoteType(voteType);
                postService.updateVote(existingVote);
                Integer voteCount = post.getVoteCount();
                post.setVoteCount(voteCount + 1);
            }
        } else {
            Vote vote = new Vote();
            vote.setPost(post);
            vote.setUser(user);
            vote.setVoteType(voteType);
            postService.createVote(vote);
            Integer voteCount = post.getVoteCount();
            post.setVoteCount(voteCount + 1);
        }

        if (post.getVoteCount() % 10 == 0) {
            Integer karma = post.getUser().getKarma();
            karma = karma + 1;
            post.getUser().setKarma(karma);
        }

        model.addAttribute("voteType", voteType);

        postService.save(post);

        return "redirect:/posts";
    }

    @GetMapping("/posts/downvote/{postId}/{voteType}")
    public String downvote(@PathVariable Integer postId, @PathVariable VoteType voteType, Model model) {
        Post post = postService.findById(postId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(authentication.getName());

        Vote existingVote = postService.findVoteByUserAndPost(user, post);

        if (existingVote != null) {
            if (existingVote.getVoteType() != voteType) {
                existingVote.setVoteType(voteType);
                postService.updateVote(existingVote);
                Integer voteCount = post.getVoteCount();
                post.setVoteCount(voteCount - 1);
            }
        } else {
            Vote vote = new Vote();
            vote.setPost(post);
            vote.setUser(user);
            vote.setVoteType(voteType);
            postService.createVote(vote);
            Integer voteCount = post.getVoteCount();
            post.setVoteCount(voteCount - 1);
        }

        if (post.getUser().getKarma() > 0 && post.getVoteCount() % 10 == 0) {
            Integer karma = post.getUser().getKarma();
            karma = karma - 1;
            post.getUser().setKarma(karma);
        }

        model.addAttribute("voteType", voteType);

        postService.save(post);

        return "redirect:/posts";
    }

    @GetMapping("/subreddit/r/{subredditName}")
    public String getSubredditPosts(@PathVariable(name = "subredditName") String subredditName, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(authentication.getName());
        List<Post> posts = postService.findAllPostBySubredditName("r/" + subredditName);
        List<Subreddit> subredditList = postService.findAllSubreddit();

        if (user != null) {
            model.addAttribute("user", user);
        }

        model.addAttribute("subredditList", subredditList);
        model.addAttribute("posts", posts);

        return "home-page";
    }

    @GetMapping("/subreddit")
    public String getSubredditPost(@RequestParam(name = "subredditName", required = false) String subredditName, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(authentication.getName());
        List<Post> posts = postService.findAllPostBySubredditName(subredditName);
        List<Subreddit> subredditList = postService.findAllSubreddit();

        if (user != null) {
            model.addAttribute("user", user);
        }
        if (subredditName == null || subredditName.isEmpty()) {
            return "redirect:/posts";
        }

        model.addAttribute("subredditList", subredditList);
        model.addAttribute("posts", posts);

        return "home-page";
    }

    @GetMapping("/search")
    public String search(@RequestParam(name = "text", required = false) String text, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(authentication.getName());
        List<Post> posts = postService.SearchByPostNameSubredditDescriptionURL(text);
        List<Subreddit> subredditList = postService.findAllSubreddit();

        if (user != null) {
            model.addAttribute("user", user);
        }

        if (text == null || text.isEmpty()) {
            return "redirect:/posts";
        }

        model.addAttribute("subredditList", subredditList);
        model.addAttribute("posts", posts);

        return "home-page";
    }

    @GetMapping("/new")
    public String newSortPost(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(authentication.getName());
        List<Post> posts = postService.findAllNewPost();
        List<Subreddit> subredditList = postService.findAllSubreddit();

        if (user != null) {
            model.addAttribute("user", user);
        }


        model.addAttribute("subredditList", subredditList);
        model.addAttribute("posts", posts);

        return "home-page";
    }

    @GetMapping("/top")
    public String topSortPost(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(authentication.getName());
        List<Post> posts = postService.findAllTopPost();
        List<Subreddit> subredditList = postService.findAllSubreddit();

        if (user != null) {
            model.addAttribute("user", user);
        }

        model.addAttribute("subredditList", subredditList);
        model.addAttribute("posts", posts);

        return "home-page";
    }

    @GetMapping("/hot")
    public String hotSortPost(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(authentication.getName());
        List<Post> posts = postService.findAllHotPost();
        List<Subreddit> subredditList = postService.findAllSubreddit();

        if (user != null) {
            model.addAttribute("user", user);
        }

        model.addAttribute("subredditList", subredditList);
        model.addAttribute("posts", posts);

        return "home-page";
    }

    @GetMapping("/profile")
    public String userProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(authentication.getName());
        List<Post> posts = user != null ? postService.findALlPostsByUserIdByOrderByVoteCountDesc(user.getUserId()) : new ArrayList<>();
        List<Subreddit> subredditList = postService.findAllSubreddit();

        if (user != null) {
            model.addAttribute("user", user);
        }

        model.addAttribute("subredditList", subredditList);
        model.addAttribute("posts", posts);
        model.addAttribute("commentList", null);

        return "profile-page";
    }

    @GetMapping("/profile/comment")
    public String userProfileComment(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(authentication.getName());
        List<Comment> commentList  = user.getComments();
        List<Subreddit> subredditList = postService.findAllSubreddit();


        model.addAttribute("subredditList", subredditList);
        model.addAttribute("posts", null);
        model.addAttribute("commentList", commentList);
        model.addAttribute("user", user);

        return "profile-page";
    }

    @GetMapping("/profile/downvote")
    public String userProfileDownvote(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(authentication.getName());
        List<Post> postList = userService.findAllDownvotePostGivenByUserId(user.getUserId());
        List<Subreddit> subredditList = postService.findAllSubreddit();

        model.addAttribute("subredditList", subredditList);
        model.addAttribute("user", user);
        model.addAttribute("posts", postList);
        model.addAttribute("commentList", null);

        return "profile-page";
    }

    @GetMapping("/profile/upvote")
    public String userProfileUpvote(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(authentication.getName());
        List<Post> postList = userService.findAllUpvotePostGivenByUserId(user.getUserId());
        List<Subreddit> subredditList = postService.findAllSubreddit();


        model.addAttribute("subredditList", subredditList);
        model.addAttribute("user", user);
        model.addAttribute("posts", postList);
        model.addAttribute("commentList", null);

        return "profile-page";
    }

    @GetMapping("/profile/downvote/{postId}/{voteType}")
    public String downvoteOnProfile(@PathVariable Integer postId, @PathVariable VoteType voteType, Model model) {
        Post post = postService.findById(postId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(authentication.getName());

        Vote existingVote = postService.findVoteByUserAndPost(user, post);

        if (existingVote != null) {
            if (existingVote.getVoteType() != voteType) {
                existingVote.setVoteType(voteType);
                postService.updateVote(existingVote);

                Integer voteCount = post.getVoteCount();
                    post.setVoteCount(voteCount - 1);
            }
        } else {
            Vote vote = new Vote();
            vote.setPost(post);
            vote.setUser(user);
            vote.setVoteType(voteType);
            postService.createVote(vote);

            Integer voteCount = post.getVoteCount();
            post.setVoteCount(voteCount - 1);

        }

        model.addAttribute("voteType", voteType);

        postService.save(post);

        return "redirect:/profile/downvote";
    }

    @GetMapping("/profile/upvote/{postId}/{voteType}")
    public String upvoteOnProfile(@PathVariable Integer postId, @PathVariable VoteType voteType, Model model) {
        Post post = postService.findById(postId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(authentication.getName());
        Vote existingVote = postService.findVoteByUserAndPost(user, post);

        if (existingVote != null) {
            if (existingVote.getVoteType() != voteType) {
                existingVote.setVoteType(voteType);
                postService.updateVote(existingVote);
                Integer voteCount = post.getVoteCount();
                post.setVoteCount(voteCount + 1);

            }
        } else {
            Vote vote = new Vote();
            vote.setPost(post);
            vote.setUser(user);
            vote.setVoteType(voteType);
            postService.createVote(vote);
            Integer voteCount = post.getVoteCount();
            post.setVoteCount(voteCount + 1);

        }

        model.addAttribute("voteType", voteType);

        postService.save(post);

        return "redirect:/profile/upvote";
    }

    @PostMapping("/publish")
    public String publishPost(@RequestParam("postId") Integer postId) {
        Post post = postService.findById(postId);
        post.setIsPublished(true);

        postService.save(post);

        return "redirect:/posts";
    }
}
