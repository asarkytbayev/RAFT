package com.neu.project3.raft.controller;

import com.neu.project3.raft.models.DBRequest;
import com.neu.project3.raft.models.DBResponse;
import com.neu.project3.raft.models.LogEntry;
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

    @Autowired
    public DatabaseController(InformationService informationService, DatabaseService databaseService){
        this.informationService = informationService;
        this.databaseService = databaseService;
    }

    @PostMapping(value = "/upsert_key")
    public  DBResponse upsertRequest(@RequestBody DBRequest request) {
        DBResponse response = this.databaseService.upsertKey(request);
        informationService.logEntryList.add(new LogEntry(request.toString(), informationService.currentTerm));
        return response;
    }

    @PostMapping(value = "/delete_key")
    public  DBResponse deleteKey(@RequestBody DBRequest request){
        DBResponse response = this.databaseService.deleteKey(request);
        informationService.logEntryList.add(new LogEntry(request.toString(), informationService.currentTerm));
        return response;
    }

    @PostMapping(value = "/get_key")
    public DBResponse getRequest(@RequestBody DBRequest request){
        DBResponse response = this.databaseService.getKey(request);
        informationService.logEntryList.add(new LogEntry(request.toString(), informationService.currentTerm));
        return response;
    }
}
