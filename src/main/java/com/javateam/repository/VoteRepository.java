package com.javateam.repository;

import com.javateam.model.Post;
import com.javateam.model.User;
import com.javateam.model.Vote;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Integer> {
    Vote findByUserAndPost(User user, Post post);
}
