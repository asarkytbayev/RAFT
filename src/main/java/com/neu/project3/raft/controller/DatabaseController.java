package com.neu.project3.raft.controller;

import com.neu.project3.raft.models.DBRequest;
import com.neu.project3.raft.models.DBResponse;
import com.neu.project3.raft.models.LogEntry;
import com.neu.project3.raft.service.InformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DatabaseController {

    @Autowired
    private InformationService informationService;

    @PostMapping(value = "/upsert_key")
    public String upsertRequest(@RequestBody DBRequest request) {
        informationService.logEntryList.add(new LogEntry("Hi", informationService.currentTerm));
        return "Hello";
        // return this.heartbeatService (request);
    }

    @PostMapping(value = "/delete_key")
    public DBResponse deleteKey(@RequestBody DBRequest request){
        return null;
    }

    @PostMapping(value = "/get_key")
    public DBResponse getRequest(@RequestBody DBRequest request){
        return null;
    }
}
