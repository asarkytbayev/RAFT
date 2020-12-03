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

    private InformationService informationService;
    private VoteRequestSender voteRequestSender;
    private static final Random RANDOM = new Random();
    private static final int MIN_ELECTION_DELAY = 5000;
    private static final int MAX_ELECTION_DELAY = 10000;
    private static final int MIN_LEADER_DELAY = 10000;
    private static final int MAX_LEADER_DELAY = 15000;

    @Autowired
    public ElectionService(InformationService informationService, VoteRequestSender voteRequestSender){
        this.informationService = informationService;
        this.voteRequestSender = voteRequestSender;
    }

    @Scheduled(fixedDelay = 1000)
    public synchronized void initElection() {
        System.out.println("I'm a " + informationService.currentState);
        System.out.println("Voted for: " + informationService.votedFor);
        System.out.println("Current term: " + informationService.currentTerm);
        System.out.println("State: " + informationService.logEntryList);

        long timeout = getRandomNumberUsingNextInt(MIN_ELECTION_DELAY, MAX_ELECTION_DELAY);
        long electionTimeout = Instant.now().toEpochMilli() - informationService.lastTimeStampReceived;
        if (electionTimeout < timeout) {
            return;
        }

        // start an election if timeout was greater than random timeout value
        informationService.currentTerm++;
        informationService.currentState = State.CANDIDATE;
        informationService.lastTimeStampReceived = Instant.now().toEpochMilli();
        System.out.println("Now I'm a " + informationService.currentState);
        informationService.votedFor = informationService.self.id;
        VoteRequest voteRequest = constructRequestVoteRPC();
        List<VoteResponse> responseList = new ArrayList<>();
        for (Peer p : informationService.peerList){
            if (p == informationService.self) {
                continue;
            }
            // need to add reactive annotation to add parallel calls
            responseList.add(this.voteRequestSender.sendVoteRequest(voteRequest, p.hostname));
        }
        responseList.removeAll(Collections.singleton(null));
        if (responseList.isEmpty() || informationService.currentState == State.FOLLOWER) {
            informationService.currentState = State.FOLLOWER;
            informationService.votedFor = -1;
            informationService.lastTimeStampReceived = Instant.now().toEpochMilli();
            return;
        }

        int largerTermCount = responseList.stream()
                .max(Comparator.comparing(VoteResponse::getTerm))
                .get()
                .getTerm();

        if (largerTermCount > informationService.currentTerm) {
            informationService.currentState = State.FOLLOWER;
            informationService.votedFor = -1;
            informationService.lastTimeStampReceived = Instant.now().toEpochMilli();
            informationService.currentTerm = largerTermCount;
            return;
        }

        long votes = responseList.stream()
                .filter(VoteResponse::getVoteGranted)
                .count() + 1;
        if (votes >= informationService.getMajorityVote() && informationService.currentState == State.CANDIDATE) {
            informationService.currentState = State.LEADER;
            System.out.println("I was elected: " + informationService.currentState);
            informationService.onLeaderPromotion();
        } else {
            informationService.currentState = State.FOLLOWER;
            informationService.votedFor = -1;
        }
//        informationService.votedFor = -1;
        informationService.lastTimeStampReceived = Instant.now().toEpochMilli();
        informationService.leaderTimeStamp = Instant.now().toEpochMilli();

    }

    private long getRandomNumberUsingNextInt(int min, int max) {
        Integer val =  RANDOM.nextInt(max - min) + min;
        return val.longValue();
    }

    private synchronized VoteRequest constructRequestVoteRPC() {
        int lastLogIndex;
        int lastLogTerm;
        if (informationService.logEntryList.isEmpty()) {
            lastLogIndex = 0;
            lastLogTerm = 0;
        } else {
            lastLogIndex = informationService.logEntryList.size()+1;
            lastLogTerm = informationService.logEntryList.get(informationService.logEntryList.size()-1).term;
        }
        VoteRequest requestVoteRPC =
                new VoteRequest(informationService.currentTerm, informationService.self.id, lastLogIndex, lastLogTerm, informationService.self.id);
        return requestVoteRPC;
    }
}
