package com.javateam.controller;

import com.javateam.model.Comment;
import com.javateam.model.Post;
import com.javateam.model.User;
import com.javateam.service.CommentService;
import com.javateam.service.PostService;
import com.javateam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CommentController {
    private PostService postService;
    private CommentService commentService;
    private UserService userService;
    @Autowired
    public CommentController(PostService postService, CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.postService = postService;
        this.userService = userService;
    }

    @PostMapping("/addComment/{postId}")
    public String addComment(
            @PathVariable Integer postId,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String commentText
    ){
            Post post = postService.findById(postId);
            Comment comment = new Comment();
            User user = userService.findByEmail(email);
            comment.setUser(user);
            comment.setName(name);
            comment.setEmail(email);
            comment.setPostId(postId);
            comment.setText(commentText);
            comment.setPost(post);

            commentService.saveComment(comment);
            return "redirect:/"+post.getPostId();
    }

    @GetMapping("/replyOnComment/{commentId}")
    public String replyComment(@PathVariable Integer commentId, @RequestParam Integer postId, Model model) {
        Comment comment = commentService.getCommentById(commentId);
        Comment replyComment = new Comment();
        model.addAttribute("comment", comment);
        model.addAttribute("replyComment",replyComment);
        return "reply_comment";
    }

    @PostMapping("/replyOnComment/{commentId}")
    public String saveReplyComment( @PathVariable Integer commentId,
                                    Comment replyComment,
                                    Model model
    ){
            Comment comment = commentService.getCommentById(commentId);
            replyComment.setParentComment(comment);
            replyComment.setPost(comment.getPost());
            commentService.saveComment(replyComment);
            return "redirect:/" + comment.getPost().getPostId();
    }

    @GetMapping("/viewReplies/{commentId}")
    public String viewReplies(@PathVariable Integer commentId,
                              Model model
    ){
        Comment comment = commentService.getCommentById(commentId);
            List<Comment> replies = comment.getReplies();
            Post post = comment.getPost();

            model.addAttribute("comment",comment);
            model.addAttribute("post",post);
            model.addAttribute("replies",replies);

            return "view_replies";
    }

    @GetMapping("/editComment/{commentId}")
    public String editComment(@PathVariable Integer commentId, Model model) {
        Comment comment = commentService.getCommentById(commentId);
        model.addAttribute("comment", comment);
        return "comment_edit";
    }

    @PostMapping("/editComment/{commentId}")
    public String saveEditedComment(
            @PathVariable Integer commentId,
            Comment comment, Model model
    ) {
        Comment existingComment = commentService.getCommentById(commentId);
        existingComment.setText(comment.getText());
        commentService.saveComment(existingComment);
        return "redirect:/" + existingComment.getPost().getPostId();
    }

    @GetMapping("/cancelEdit/{commentId}")
    public String cancelEdit(@PathVariable Integer commentId) {
        Comment comment = commentService.getCommentById(commentId);
        return "redirect:/" + comment.getPost().getPostId();
    }

    @GetMapping("/deleteComment/{commentId}")
    public String deleteComment(@PathVariable Integer commentId) {
        Comment comment = commentService.getCommentById(commentId);
        commentService.deleteCommentById(commentId);

        return "redirect:/" + comment.getPost().getPostId();
    }

}
