package com.neu.project3.raft.controller;

import com.neu.project3.raft.models.DBRequest;
import com.neu.project3.raft.models.DBResponse;
import com.neu.project3.raft.requests.HeartbeatRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DatabaseController {

    @PostMapping(value = "/upsert_key")
    public DBResponse upsertRequest(DBRequest request) {
        return null;
        // return this.heartbeatService (request);
    }

    @PostMapping(value = "/delete_key")
    public DBResponse deleteKey(DBRequest request){
        return null;
    }

    @PostMapping(value = "/get_key")
    public DBResponse getRequest(DBRequest request){
        return null;
    }
}
