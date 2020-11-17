package com.neu.project3.raft.service;

import com.neu.project3.raft.requests.AppendEntryRequest;
import com.neu.project3.raft.requests.VoteRequest;
import com.neu.project3.raft.responses.AppendEntryResponse;
import com.neu.project3.raft.responses.VoteResponse;
import org.springframework.stereotype.Service;

@Service
public class AppendEntryService {

    public AppendEntryResponse handleAppendEntryRequest(AppendEntryRequest appendEntryRequest){
        return null;
    }
}
