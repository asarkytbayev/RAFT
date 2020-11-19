package com.neu.project3.raft.service;

import com.neu.project3.raft.manager.HeartbeatSender;
import com.neu.project3.raft.manager.VoteRequestSender;
import com.neu.project3.raft.models.Peer;
import com.neu.project3.raft.requests.HeartbeatRequest;
import com.neu.project3.raft.responses.HeartbeatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HeartbeatService {

    private InformationService informationService;
    private HeartbeatSender heartbeatSender;

    @Autowired
    public HeartbeatService(InformationService informationService, HeartbeatSender heartbeatSender){
        this.informationService = informationService;
        this.heartbeatSender = heartbeatSender;
    }

    @Scheduled(fixedDelay = 1000)
    void sendHeartBeat(){
        List<HeartbeatResponse> responseList = new ArrayList<>();
        HeartbeatRequest request = getHeartbeatRequest();
        // need to add random delay
        for(Peer p : informationService.getPeerList()){
            // need to add reactive annotation to add parallel calls
            responseList.add(this.heartbeatSender.sendHeartBeat(request, p.id));
        }
    }

    private HeartbeatRequest getHeartbeatRequest(){
        return null;
    }
}
