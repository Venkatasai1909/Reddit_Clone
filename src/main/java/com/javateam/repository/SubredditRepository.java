package com.javateam.repository;

import com.javateam.model.Subreddit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SubredditRepository extends JpaRepository<Subreddit, Integer> {
    Subreddit findByName(String name);

}
