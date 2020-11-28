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
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class ElectionService {

    private InformationService informationService;
    private VoteRequestSender voteRequestSender;
    private static final int MIN_ELECTION_DELAY = 5000;
    private static final int MAX_ELECTION_DELAY = 10000;

    @Autowired
    public ElectionService(InformationService informationService, VoteRequestSender voteRequestSender){
        this.informationService = informationService;
        this.voteRequestSender = voteRequestSender;
    }

    @Scheduled(fixedDelay = 1000)
    public synchronized void initElection() {
        System.out.println("I'm a " + InformationService.currentState);
        // need to add a random time out here (sleep)
        // spring will ensure that this is running in background
        long timeout = getRandomNumberUsingNextInt();
        System.out.println("timeout: " + timeout);
//        try {
//            System.out.println("Sleeping for: " + timeout);
//            Thread.sleep(timeout);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        long electionTimeout = Instant.now().toEpochMilli() - InformationService.lastTimeStampReceived;
        System.out.println("election timeout: " + electionTimeout);
        if (electionTimeout < timeout) {
            System.out.println("return");
            return;
        }

        // start an election if timeout was greater than random timeout value
        InformationService.currentTerm++;
        InformationService.currentState = State.CANDIDATE;
        InformationService.lastTimeStampReceived = Instant.now().toEpochMilli();
        System.out.println("Now I'm a " + InformationService.currentState);
        InformationService.votedFor = InformationService.self.id;
        VoteRequest voteRequest = constructRequestVoteRPC();
        List<VoteResponse> responseList = new ArrayList<>();
        for (Peer p : InformationService.peerList){
            if (p == InformationService.self) {
                continue;
            }
            // need to add reactive annotation to add parallel calls
            responseList.add(this.voteRequestSender.sendVoteRequest(voteRequest, p.hostname));
        }
        responseList.removeAll(Collections.singleton(null));
        if (responseList.isEmpty()) {
            InformationService.currentState = State.FOLLOWER;
            return;
        }
        long votes = responseList.stream()
                .filter(VoteResponse::getVoteGranted)
                .count() + 1;
        if (votes >= InformationService.getMajorityVote()) {
            InformationService.currentState = State.LEADER;
        } else {
            InformationService.currentState = State.FOLLOWER;
        }
        InformationService.votedFor = -1;
        System.out.println("I was elected: " + InformationService.currentState);
        InformationService.lastTimeStampReceived = Instant.now().toEpochMilli();
    }

    private long getRandomNumberUsingNextInt() {
        Random random = new Random();
        Integer val =  random.nextInt(MAX_ELECTION_DELAY - MIN_ELECTION_DELAY) + MIN_ELECTION_DELAY;
        return val.longValue();
    }

    private synchronized VoteRequest constructRequestVoteRPC() {
        int lastLogIndex;
        int lastLogTerm;
        if (InformationService.logEntryList.isEmpty()) {
            lastLogIndex = 0;
            lastLogTerm = 0;
        } else {
            lastLogIndex = InformationService.logEntryList.size()+1;
            lastLogTerm = InformationService.logEntryList.get(InformationService.logEntryList.size()-1).term;
        }
        VoteRequest requestVoteRPC = new VoteRequest(InformationService.currentTerm, InformationService.self.id,
                lastLogIndex, lastLogTerm, InformationService.self.id);
        return requestVoteRPC;
    }
}
