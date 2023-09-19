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

    @Query("SELECT u FROM User u WHERE u.username = :username AND u.password = :password")
    User findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    @Query("SELECT p FROM Post p WHERE p.subreddit IN :subreddits")
    List<Post> findPostsBySubreddits(@Param("subreddits") Set<Subreddit> subreddits);


}
