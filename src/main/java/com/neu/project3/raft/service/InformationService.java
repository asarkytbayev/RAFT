package com.neu.project3.raft.service;

import com.neu.project3.raft.models.LogEntry;
import com.neu.project3.raft.models.Peer;
import com.neu.project3.raft.models.State;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Getter
@Setter
public class InformationService {

    private List<Peer> peerList;
    private List<LogEntry> logEntryList;
    private Peer leader;
    private Peer self;
    private Integer currentTerm;
    private State currentState;
    private Integer currentLog;
    private Integer votedFor;

    @Autowired
    public InformationService(@Value("${peer_file_list}") String peerFile, @Value("${self_id}") String selfId){
        this.peerList = parseFileAndGetPeers(peerFile);
        this.self = this.peerList.get(Integer.parseInt(selfId)-1);
        this.logEntryList = new ArrayList<>();
        this.currentTerm = 0;
        this.currentLog = 0;
        this.votedFor = -1;
        this.logEntryList.add(new LogEntry("init", 0));
        this.currentState = State.FOLLOWER;
        this.currentLog = 0;
    }

    public Peer getLeader(){
        return leader;
    }

    List<Peer> parseFileAndGetPeers(String peerFile){
        // todo, read from file, right now we are hard coding 5 nodes
        Peer peer1 = new Peer(1, "hostname1");
        Peer peer2 = new Peer(2, "hostname2");
        Peer peer3 = new Peer(3, "hostname3");
        Peer peer4 = new Peer(4, "hostname4");
        Peer peer5 = new Peer(5, "hostname5");
        List<Peer> peerList = new ArrayList<>();
        peerList.add(peer1);
        peerList.add(peer2);
        peerList.add(peer3);
        peerList.add(peer4);
        peerList.add(peer5);
        return peerList;
    }


}
