package com.javateam.repository;

import com.javateam.model.Post;
import com.javateam.model.Subreddit;
import com.javateam.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);
    User findByUsername(String username);


    @Query("SELECT p FROM Post p WHERE p.subreddit IN :subreddits")
    List<Post> findPostsBySubreddits(@Param("subreddits") Set<Subreddit> subreddits);

    @Query("SELECT u FROM User u JOIN u.joinedSubreddits s WHERE s.id = ?1")
    Set<User> findUsersBySubredditId(Integer subredditId);


}
