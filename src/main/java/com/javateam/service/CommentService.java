package com.javateam.service;

import com.javateam.model.Comment;
import com.javateam.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
@Service
public class CommentService {
    private CommentRepository commentRepository;
    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment saveComment(Comment comment) {
        if (comment.getId() == null) {
            comment.setCreatedAt(Instant.now());
        }
        else{
            comment.setUpdatedAt(Instant.now());
        }
        return commentRepository.save(comment);
    }

    public Comment getCommentById(Integer commentId) {
        return commentRepository.findById(commentId).get();
    }

    public void deleteCommentById(Integer commentId) {
        commentRepository.deleteById(commentId);
    }

}
