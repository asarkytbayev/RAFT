package com.neu.project3.raft.controller;

import com.neu.project3.raft.manager.DatabaseRequestSender;
import com.neu.project3.raft.models.DBRequest;
import com.neu.project3.raft.models.DBResponse;
import com.neu.project3.raft.models.LogEntry;
import com.neu.project3.raft.service.AppendEntryService;
import com.neu.project3.raft.service.DatabaseService;
import com.neu.project3.raft.service.InformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DatabaseController {

    private InformationService informationService;
    private DatabaseService databaseService;
    private DatabaseRequestSender databaseRequestSender;
    private AppendEntryService appendEntryService;

    @Autowired
    public DatabaseController(InformationService informationService, DatabaseService databaseService,
                              DatabaseRequestSender sender, AppendEntryService appendEntryService){
        this.informationService = informationService;
        this.databaseService = databaseService;
        this.databaseRequestSender = sender;
        this.appendEntryService = appendEntryService;
    }

    @PostMapping(value = "/upsert_key")
    public  DBResponse upsertRequest(@RequestBody DBRequest request) {
        if (!InformationService.isLeader()){
            return this.databaseRequestSender.sendUpsert(request);
        }
        DBResponse response = this.databaseService.upsertKey(request);
        informationService.logEntryList.add(new LogEntry(request.toString(), informationService.currentTerm));
        return response;
    }

    @PostMapping(value = "/delete_key")
    public  DBResponse deleteKey(@RequestBody DBRequest request){
        if (!InformationService.isLeader()){
            return this.databaseRequestSender.sendDelete(request);
        }
        DBResponse response = this.databaseService.deleteKey(request);
        informationService.logEntryList.add(new LogEntry(request.toString(), informationService.currentTerm));
        appendEntryService.sendAppendEntriesToPeers();
        return response;
    }

    @PostMapping(value = "/get_key")
    public DBResponse getRequest(@RequestBody DBRequest request){
        if (!InformationService.isLeader()){
            return this.databaseRequestSender.sendGet(request);
        }
        DBResponse response = this.databaseService.getKey(request);
        informationService.logEntryList.add(new LogEntry(request.toString(), informationService.currentTerm));
        appendEntryService.sendAppendEntriesToPeers();
        return response;
    }
}
