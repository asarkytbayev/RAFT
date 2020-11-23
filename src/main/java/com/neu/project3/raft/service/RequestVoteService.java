package com.neu.project3.raft.service;

import com.neu.project3.raft.requests.VoteRequest;
import com.neu.project3.raft.responses.VoteResponse;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestVoteService {

    private InformationService informationService;

    @Autowired
    public RequestVoteService(InformationService informationService){
        this.informationService = informationService;
    }

    public synchronized VoteResponse checkVoteRequest(VoteRequest voteRequest){
        if (InformationService.votedFor == -1 && voteRequest.getLastLogTerm() >= InformationService.currentLog){
            // save voted for information
            return new VoteResponse(InformationService.currentTerm, true, InformationService.self.id);
        }
        else if (InformationService.votedFor == voteRequest.getCandidateId() && voteRequest.getLastLogTerm() >= InformationService.currentLog){
            return new VoteResponse(InformationService.currentTerm, true, InformationService.self.id);
        }
        return new VoteResponse(InformationService.currentTerm, false, InformationService.self.id);
    }
}
