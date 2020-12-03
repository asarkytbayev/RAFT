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
import java.util.*;

@Service
public class ElectionService {

    private final VoteRequestSender voteRequestSender;
    private static final Random RANDOM = new Random();
    private static final int MIN_ELECTION_DELAY = 5000;
    private static final int MAX_ELECTION_DELAY = 10000;

    @Autowired
    public ElectionService(VoteRequestSender voteRequestSender){
        this.voteRequestSender = voteRequestSender;
    }

    @Scheduled(fixedDelay = 1000)
    public synchronized void initElection() {
        System.out.println("I'm a " + InformationService.currentState);
        System.out.println("Voted for: " + InformationService.votedFor);
        System.out.println("Current term: " + InformationService.currentTerm);
        System.out.println("State: " + InformationService.logEntryList);

        long timeout = getRandomNumberUsingNextInt();
        long electionTimeout = Instant.now().toEpochMilli() - InformationService.lastTimeStampReceived;
        if (electionTimeout < timeout) {
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
        if (responseList.isEmpty() || InformationService.currentState == State.FOLLOWER) {
            InformationService.currentState = State.FOLLOWER;
            InformationService.votedFor = -1;
            InformationService.lastTimeStampReceived = Instant.now().toEpochMilli();
            return;
        }

        int largerTermCount = responseList.stream()
                .max(Comparator.comparing(VoteResponse::getTerm))
                .get()
                .getTerm();

        if (largerTermCount > InformationService.currentTerm) {
            InformationService.currentState = State.FOLLOWER;
            InformationService.votedFor = -1;
            InformationService.lastTimeStampReceived = Instant.now().toEpochMilli();
            InformationService.currentTerm = largerTermCount;
            return;
        }

        long votes = responseList.stream()
                .filter(VoteResponse::getVoteGranted)
                .count() + 1;
        if (votes >= InformationService.getMajorityVote() && InformationService.currentState == State.CANDIDATE) {
            InformationService.currentState = State.LEADER;
            System.out.println("I was elected: " + InformationService.currentState);
            InformationService.onLeaderPromotion();
        } else {
            InformationService.currentState = State.FOLLOWER;
            InformationService.votedFor = -1;
        }
//        informationService.votedFor = -1;
        InformationService.lastTimeStampReceived = Instant.now().toEpochMilli();

    }

    private long getRandomNumberUsingNextInt() {
        long val =  RANDOM.nextInt(MAX_ELECTION_DELAY - MIN_ELECTION_DELAY) + MIN_ELECTION_DELAY;
        return val;
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
        return new VoteRequest(InformationService.currentTerm, InformationService.self.id, lastLogIndex, lastLogTerm, InformationService.self.id);
    }
}
