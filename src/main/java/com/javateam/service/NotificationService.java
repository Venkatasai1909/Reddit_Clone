package com.javateam.service;

import com.javateam.model.Notification;
import com.javateam.model.Post;
import com.javateam.model.Subreddit;
import com.javateam.model.User;
import com.javateam.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class NotificationService {

    private NotificationRepository notificationRepository;
    private EmailNotificationService emailNotificationService;


    @Autowired
    public NotificationService(NotificationRepository notificationRepository, EmailNotificationService emailNotificationService) {
        this.notificationRepository = notificationRepository;
        this.emailNotificationService = emailNotificationService;
    }

    public void sendPostCreationNotifications(Subreddit subreddit, Post post) {
        Set<User> joinedUsers = subreddit.getMembers();
        int index = post.getDescription().indexOf(".");
        String content;

        if(index != 0) {
              content = post.getDescription().substring(0,index);
        } else {
            content = post.getDescription();
        }

        for (User user : joinedUsers) {
            String message = "A new post has been created in the subreddit: " + subreddit.getName() + content;

            Notification notification = new Notification();
            notification.setUser(user);
            notification.setSubreddit(subreddit);
            notification.setContent(message);
            notification.setTimestamp(LocalDateTime.now());

            notificationRepository.save(notification);

            emailNotificationService.sendEmailNotification(user, message);
        }
    }

}

