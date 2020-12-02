package com.neu.project3.raft.service;

import com.neu.project3.raft.models.State;
import com.neu.project3.raft.requests.VoteRequest;
import com.neu.project3.raft.responses.VoteResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestVoteService {

    @Autowired
    public RequestVoteService(){
    }

    public synchronized VoteResponse checkVoteRequest(VoteRequest voteRequest) {
        System.out.println("Received vote request from: " + voteRequest.getCandidateId());
        boolean voteGranted = false;
        if (voteRequest.getTerm() > InformationService.currentTerm) {
            InformationService.currentTerm = voteRequest.getTerm();
            InformationService.currentState = State.FOLLOWER;
        }
        if (voteRequest.getTerm() < InformationService.currentTerm) {
            voteGranted = false;
            return new VoteResponse(InformationService.currentTerm, voteGranted, InformationService.self.id);
        } else if (InformationService.votedFor != -1) {
            voteGranted = false;
            return new VoteResponse(InformationService.currentTerm, voteGranted, InformationService.self.id);
        } else if (InformationService.votedFor == -1 && voteRequest.getLastLogTerm() >= InformationService.currentLog) {
            // save voted for information
            InformationService.votedFor = voteRequest.getCandidateId();
            InformationService.currentTerm = voteRequest.getTerm();
            voteGranted = true;
            return new VoteResponse(InformationService.currentTerm, voteGranted, InformationService.self.id);
        }
        else if (InformationService.votedFor.equals(voteRequest.getCandidateId()) && voteRequest.getLastLogTerm() >= InformationService.currentLog){
            InformationService.votedFor = voteRequest.getCandidateId();
            InformationService.currentTerm = voteRequest.getTerm();
            voteGranted = true;
            return new VoteResponse(InformationService.currentTerm, voteGranted, InformationService.self.id);
        }
        return new VoteResponse(InformationService.currentTerm, voteGranted, InformationService.self.id);
    }
}
