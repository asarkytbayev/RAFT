package com.neu.project3.raft.controller;

import com.neu.project3.raft.requests.AppendEntryRequest;
import com.neu.project3.raft.responses.AppendEntryResponse;
import com.neu.project3.raft.service.AppendEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppendEntryController {

    private AppendEntryService appendEntryService;

    @Autowired
    public AppendEntryController(AppendEntryService appendEntryService){
        this.appendEntryService = appendEntryService;
    }

    @PostMapping(value = "/append_entry")
    AppendEntryResponse requestVote(@RequestBody AppendEntryRequest request) {
        return this.appendEntryService.handleAppendEntryRequest(request);
    }
}
