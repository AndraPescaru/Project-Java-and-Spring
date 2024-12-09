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

    private static final String DEMO_FEED_FILE_NAME = "demo";
    @Autowired
    private BankFeedService feedService;

    @Value("${feed.filename}")
    private String fileName;


    @Override
    @PostConstruct
    public void createClientsForDemo()
    {
        if (fileName == null)
        {
            fileName = DEMO_FEED_FILE_NAME;
        }

        feedService.loadFeed(new File(fileName));
    }

}