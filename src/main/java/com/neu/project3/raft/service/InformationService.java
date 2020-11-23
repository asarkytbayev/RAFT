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

    /* Persistent State */
    // latest term server has seen
    public static volatile Integer votedFor;

    // candidate id that received vote in current term
    public static volatile Integer currentTerm;

    // log entries, first index is 1
    public static volatile List<LogEntry> logEntryList;

    /* Volatile state on server */
    // index of highest log entry known to be committed, init 0
    public static Integer commitIndex;
    // index of highest log entry applied to state machine
    public static Integer lastApplied;

    /* Volatile state on leader */

    // index on the next log entry to send to that server
    public static List<Integer> nextIndex;

    // for each server, index of the highest log entry known to be replicated on the server
    public static List<Integer> matchIndex;

    public static volatile List<Peer> peerList;
    public static volatile Peer leader;
    public static volatile Peer self;
    public static volatile State currentState;
    public static volatile Integer currentLog;
    public static volatile Long lastTimeStampReceived;

    @Autowired
    public InformationService(@Value("${peer_file_list}") String peerFile, @Value("${self_id}") String selfId){
        InformationService.peerList = parseFileAndGetPeers(peerFile);
        InformationService.self = InformationService.peerList.get(Integer.parseInt(selfId)-1);
        InformationService.logEntryList = new ArrayList<>();
        InformationService.currentTerm = 0;
        InformationService.currentLog = 0;
        InformationService.votedFor = -1;
        InformationService.logEntryList.add(new LogEntry("init", 0));
        InformationService.currentState = State.FOLLOWER;
        InformationService.currentLog = 0;
        InformationService.lastTimeStampReceived = 0L;
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
