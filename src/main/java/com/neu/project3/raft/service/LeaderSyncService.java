package com.neu.project3.raft.service;

import com.neu.project3.raft.manager.AppendRequestSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class LeaderSyncService {

    private InformationService informationService;
    private AppendRequestSender appendRequestSender;

    @Autowired
    public LeaderSyncService(InformationService informationService, AppendRequestSender appendRequestSender){
        this.informationService = informationService;
        this.appendRequestSender = appendRequestSender;
    }

    @Scheduled(fixedDelay = 500)
    public synchronized void syncPeers(){

    }
}
