package com.javateam.service;

import com.javateam.repository.SubredditRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class SubredditService {
    private SubredditRepository subredditRepository;

    @Autowired
    public SubredditService(SubredditRepository subredditRepository){
        this.subredditRepository = subredditRepository;
    }

}
