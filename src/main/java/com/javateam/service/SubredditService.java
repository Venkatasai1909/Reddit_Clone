package com.javateam.service;

import com.javateam.model.Subreddit;
import com.javateam.repository.SubredditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class SubredditService {
    private SubredditRepository subredditRepository;

    @Autowired
    public SubredditService(SubredditRepository subredditRepository){
        this.subredditRepository = subredditRepository;
    }

    public Subreddit findBySubredditId(Integer subredditId) {
        Subreddit subreddit = subredditRepository.findById(subredditId).get();

        return subreddit;
    }

    public void save(Subreddit subreddit) {
        subredditRepository.save(subreddit);
    }

    public List<Subreddit> findAllSubreddit() {
        return subredditRepository.findAll();
    }
    public Subreddit findBySubredditName(String subredditName) {
        return subredditRepository.findByName(subredditName);
    }

}
