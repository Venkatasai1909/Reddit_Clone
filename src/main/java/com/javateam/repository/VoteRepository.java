package com.javateam.repository;

import com.javateam.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Integer> {
    Vote findByUserAndPost(User user, Post post);
    Vote findByUserAndComment(User user, Comment comment);

   // @Query("SELECT v FROM Vote v WHERE v.user.id = ?1 AND v.voteType = ?2 AND v.comment.id == null")


    @Query("SELECT v FROM Vote v WHERE v.user.userId = :userId AND v.voteType=:voteType AND v.comment.commentId = null")
    List<Vote> findAllPostByUserIdAndVoteType(Integer userId, VoteType voteType);

}
