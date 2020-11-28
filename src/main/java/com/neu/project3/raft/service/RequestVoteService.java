package com.neu.project3.raft.service;

import com.neu.project3.raft.requests.VoteRequest;
import com.neu.project3.raft.responses.VoteResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class RequestVoteService {

    private InformationService informationService;

    @Autowired
    public RequestVoteService(InformationService informationService){
        this.informationService = informationService;
    }

    public synchronized VoteResponse checkVoteRequest(VoteRequest voteRequest) {
        System.out.println("Received vote request from: " + voteRequest.getCandidateId());
        Boolean voteGranted = false;
        if (voteRequest.getTerm() < informationService.currentTerm) {
            voteGranted = false;
            return new VoteResponse(informationService.currentTerm, voteGranted, informationService.self.id);
        } else if (informationService.votedFor != -1) {
            voteGranted = false;
            return new VoteResponse(informationService.currentTerm, voteGranted, informationService.self.id);
        } else if (informationService.votedFor == -1 && voteRequest.getLastLogTerm() >= informationService.currentLog) {
            // save voted for information
            informationService.votedFor = voteRequest.getCandidateId();
            informationService.currentTerm = voteRequest.getTerm();
            voteGranted = true;
            return new VoteResponse(informationService.currentTerm, voteGranted, informationService.self.id);
        }
        else if (informationService.votedFor == voteRequest.getCandidateId() && voteRequest.getLastLogTerm() >= informationService.currentLog){
            informationService.votedFor = voteRequest.getCandidateId();
            informationService.currentTerm = voteRequest.getTerm();
            voteGranted = true;
            return new VoteResponse(informationService.currentTerm, voteGranted, informationService.self.id);
        }
        return new VoteResponse(informationService.currentTerm, voteGranted, informationService.self.id);
    }
}
