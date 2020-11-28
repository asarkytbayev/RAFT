package com.neu.project3.raft.controller;

import com.neu.project3.raft.requests.HeartbeatRequest;
import com.neu.project3.raft.service.HeartbeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HeartbeatController {

    private HeartbeatService heartbeatService;

    @Autowired
    public HeartbeatController(HeartbeatService heartbeatService){
        this.heartbeatService = heartbeatService;
    }

    @PostMapping(value = "/heartbeat")
    String heartbeatResponse(HeartbeatRequest request) {
        return "ok";
        // return this.heartbeatService (request);
    }
}
