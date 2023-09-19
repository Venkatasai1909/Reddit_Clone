package com.javateam.service;

import com.javateam.model.*;
import com.javateam.repository.UserRepository;
import com.javateam.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private VoteRepository voteRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, VoteRepository voteRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.voteRepository = voteRepository;
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public User findByUserId(Integer userId) {
        return userRepository.findById(userId).get();
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findByUserName(String username) {
        return userRepository.findByUsername(username);
    }

    public List<Post> findAllUpvotePostGivenByUserId(Integer userId) {
        List<Vote> votes = voteRepository.findAllPostByUserIdAndVoteType(userId, VoteType.UPVOTE);
        List<Post> postList = new ArrayList<>();
        votes.forEach(vote -> {
            postList.add(vote.getPost());
        });

        return postList;
    }

    public List<Post> findAllDownvotePostGivenByUserId(Integer userId) {
        List<Vote> votes = voteRepository.findAllPostByUserIdAndVoteType(userId, VoteType.DOWNVOTE);
        List<Post> postList = new ArrayList<>();
        votes.forEach(vote -> {
            postList.add(vote.getPost());
        });

        return postList;
    }

    public User findByUsernameAndPassword(String username, String password) {
        User user = userRepository.findByUsernameAndPassword(username,password);
        return user;
    }

    public List<Post> findAllPostsBySubreddit(Set<Subreddit> subreddits) {
        List<Post> posts = userRepository.findPostsBySubreddits(subreddits);

        return posts;
    }

}
