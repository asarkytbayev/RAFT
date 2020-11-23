package com.neu.project3.raft.service;

import com.neu.project3.raft.models.LogEntry;
import com.neu.project3.raft.requests.AppendEntryRequest;
import com.neu.project3.raft.requests.VoteRequest;
import com.neu.project3.raft.responses.AppendEntryResponse;
import com.neu.project3.raft.responses.VoteResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppendEntryService {

    private InformationService informationService;

    @Autowired
    public AppendEntryService(InformationService informationService){
        this.informationService = informationService;
    }

    public synchronized AppendEntryResponse handleAppendEntryRequest(AppendEntryRequest appendEntryRequest){
        int prevLogIndex = appendEntryRequest.getPrevLogIndex();
        int prevLogTerm = appendEntryRequest.getPrevLogTerm();
        List<LogEntry> logEntryList = appendEntryRequest.getEntries();
        LogEntry lastEntry = logEntryList.get(prevLogIndex);
        if (InformationService.currentTerm > appendEntryRequest.getTerm()){
            return new AppendEntryResponse(InformationService.currentTerm, false, InformationService.self.id);
        }

        else if(logEntryList.size() == 0){
            return new AppendEntryResponse(InformationService.currentTerm, false, InformationService.self.id);
        }
        else if (lastEntry.term != prevLogTerm){
            return new AppendEntryResponse(InformationService.currentTerm, false, InformationService.self.id);
        }
        return acceptAppendEntryRequest(appendEntryRequest);
    }

    public AppendEntryResponse acceptAppendEntryRequest(AppendEntryRequest request){
        // todo
        // 3. if an existing entry conflicts with a new one, delete the existing entry and all that follow it
        // 4. append any new entries not in the log
        // 5. if leader commit > commit index, set commit index = min(leaderCommit, index of last new entry)
        return null;
    }
}
