package com.neu.project3.raft.service;

import com.neu.project3.raft.manager.VoteRequestSender;
import com.neu.project3.raft.models.Peer;
import com.neu.project3.raft.models.State;
import com.neu.project3.raft.requests.VoteRequest;
import com.neu.project3.raft.responses.VoteResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ElectionService {

    private InformationService informationService;
    private VoteRequestSender voteRequestSender;

    @Autowired
    public ElectionService(InformationService informationService, VoteRequestSender voteRequestSender){
        this.informationService = informationService;
        this.voteRequestSender = voteRequestSender;
    }

    @Scheduled(fixedDelay = 1000)
    public void initElection(){
        // need to add a random time out here (sleep)
        // spring will ensure that this is running in background
        informationService.setCurrentTerm(informationService.getCurrentTerm() + 1);
        informationService.setCurrentState(State.CANDIDATE);
        informationService.setVotedFor(informationService.getSelf().id);
        VoteRequest voteRequest = constructRequestVoteRPC();
        List<VoteResponse> responseList = new ArrayList<>();
        for(Peer p : informationService.getPeerList()){
            // need to add reactive annotation to add parallel calls
            responseList.add(this.voteRequestSender.sendVoteRequest(voteRequest, p.id));
        }
    }

    private VoteRequest constructRequestVoteRPC() {
        int lastLogIndex = informationService.getLogEntryList().size()+1;
        int lastLogTerm = informationService.getLogEntryList().get(informationService.getLogEntryList().size()-1).term;
        VoteRequest requestVoteRPC = new VoteRequest(informationService.getCurrentTerm(), informationService.getSelf().id,
                lastLogIndex, lastLogTerm, informationService.getSelf().id);
        return requestVoteRPC;
    }
}
