package com.neu.project3.raft.service;

import com.neu.project3.raft.manager.VoteRequestSender;
import com.neu.project3.raft.models.Peer;
import com.neu.project3.raft.models.State;
import com.neu.project3.raft.requests.VoteRequest;
import com.neu.project3.raft.responses.VoteResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class ElectionService {

    private InformationService informationService;
    private VoteRequestSender voteRequestSender;
    private static final int MIN_ELECTION_DELAY = 0;
    private static final int MAX_ELECTION_DELAY = 500;

    @Autowired
    public ElectionService(InformationService informationService, VoteRequestSender voteRequestSender){
        this.informationService = informationService;
        this.voteRequestSender = voteRequestSender;
    }

    @Scheduled(fixedDelay = 500)
    public synchronized void initElection(){
        // need to add a random time out here (sleep)
        // spring will ensure that this is running in background
        long timeout = getRandomNumberUsingNextInt();
        try{
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long electionTimeout = Instant.now().getEpochSecond() - InformationService.lastTimeStampReceived;
        if (electionTimeout < timeout){
            return;
        }
        // start an election if timeout was greater than random timeout value
        InformationService.currentTerm++;
        InformationService.currentState = State.CANDIDATE;
        InformationService.votedFor = InformationService.self.id;
        VoteRequest voteRequest = constructRequestVoteRPC();
        List<VoteResponse> responseList = new ArrayList<>();
        for(Peer p : InformationService.peerList){
            // need to add reactive annotation to add parallel calls
            responseList.add(this.voteRequestSender.sendVoteRequest(voteRequest, p.id));
        }
    }

    private long getRandomNumberUsingNextInt() {
        Random random = new Random();
        Integer val =  random.nextInt(MAX_ELECTION_DELAY - MIN_ELECTION_DELAY) + MIN_ELECTION_DELAY;
        return val.longValue();
    }

    private synchronized VoteRequest constructRequestVoteRPC() {
        int lastLogIndex = InformationService.logEntryList.size()+1;
        int lastLogTerm = InformationService.logEntryList.get(InformationService.logEntryList.size()-1).term;
        VoteRequest requestVoteRPC = new VoteRequest(InformationService.currentTerm, InformationService.self.id,
                lastLogIndex, lastLogTerm, InformationService.self.id);
        return requestVoteRPC;
    }
}
