package com.neu.project3.raft.controller;

import com.neu.project3.raft.requests.AppendEntryRequest;
import com.neu.project3.raft.requests.VoteRequest;
import com.neu.project3.raft.responses.AppendEntryResponse;
import com.neu.project3.raft.responses.VoteResponse;
import com.neu.project3.raft.service.AppendEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppendEntries {

    private AppendEntryService appendEntryService;

    @Autowired
    public AppendEntries(AppendEntryService appendEntryService){
        this.appendEntryService = appendEntryService;
    }

    @PostMapping(value = "/append_entry")
    AppendEntryResponse requestVote(AppendEntryRequest request){
        return this.appendEntryService.handleAppendEntryRequest(request);
    }
}
