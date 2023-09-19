package com.javateam.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;
    private String username;
    private String password;
    private String email;
    private LocalDateTime createdAt;
    private boolean enabled;
    private Integer karma = 0;
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = {CascadeType.REMOVE,CascadeType.MERGE})
    private List<Post> posts;
    @OneToMany(mappedBy = "user",cascade = {CascadeType.REMOVE, CascadeType.MERGE})
    private List<Subreddit> subreddits;
    @OneToMany(mappedBy = "user", cascade = {CascadeType.REMOVE, CascadeType.MERGE})
    private List<Comment> comments;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.REMOVE, CascadeType.MERGE})
    private List<Vote> votes;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "media_id")
    private Media media;

    @ManyToMany(cascade = {CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REMOVE, CascadeType.DETACH})
    @JoinTable(name = "user_subreddit",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "subreddit_id"))
    private Set<Subreddit> joinedSubreddits;
    @OneToMany(mappedBy = "user")
    private List<Notification> notifications;

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getKarma() {
        return karma;
    }

    public void setKarma(Integer karma) {
        this.karma = karma;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<Subreddit> getSubreddits() {
        return subreddits;
    }

    public void setSubreddits(List<Subreddit> subreddits) {
        this.subreddits = subreddits;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public Set<Subreddit> getJoinedSubreddits() {
        return joinedSubreddits;
    }

    public void setJoinedSubreddits(Set<Subreddit> joinedSubreddits) {
        this.joinedSubreddits = joinedSubreddits;
    }

}
