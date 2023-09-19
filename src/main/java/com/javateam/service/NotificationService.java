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
    private EmailNotificationService emailNotificationServiceForUser;
    private UserService userService;


    @Autowired
    public NotificationService(NotificationRepository notificationRepository,
                               EmailNotificationService emailNotificationServiceForUser, UserService userService) {
        this.notificationRepository = notificationRepository;
        this.emailNotificationServiceForUser = emailNotificationServiceForUser;
        this.userService = userService;
    }

    public void sendPostCreationNotifications(Subreddit subreddit, Post post) {
        Set<User> joinedUsers = userService.findUsersBySubredditId(subreddit.getSubredditId());

        String title=post.getPostName();

        for (User user : joinedUsers) {
            System.out.println(subreddit.getName());
            String message = "A new post has been created in the subreddit: " + title;

            Notification notification = new Notification();
            notification.setUser(user);
            notification.setSubreddit(subreddit);
            notification.setContent(message);
            notification.setTimestamp(LocalDateTime.now());

            notificationRepository.save(notification);

            emailNotificationServiceForUser.sendEmailNotification(user, notification);

        }
    }

}

