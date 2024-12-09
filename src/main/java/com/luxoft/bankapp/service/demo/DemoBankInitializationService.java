package com.luxoft.bankapp.service.demo;

import com.luxoft.bankapp.service.feed.BankFeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;

@Service
@Profile("dev")
public class DemoBankInitializationService implements BankInitializationService {

    @Autowired
    private BankFeedService feedService;

    @Value("${feed.filename:demo.feed}")
    private String fileName;

    @Override
    @PostConstruct
    public void createClientsForDemo() {
        feedService.loadFeed(new File(fileName));
    }
}