package com.neu.project3.raft.service;

import com.neu.project3.raft.models.LogEntry;
import com.neu.project3.raft.models.Peer;
import com.neu.project3.raft.models.State;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Getter
@Setter
public class InformationService {

    /* Temporary leader for now. */
    private static String TEMP_LEADER_NAME = "hostname1";

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

    public static Map<Peer, Integer> peersLogStatus;

    @Autowired
    public InformationService(@Value("${peer_file_list}") String peerFile, @Value("${self_id}") String selfId) {
        InformationService.peerList = parseFileAndGetPeers(peerFile);
        this.saveHostName();
        //InformationService.self = InformationService.peerList.get(Integer.parseInt(selfId)-1);
        InformationService.logEntryList = new ArrayList<>();
        InformationService.currentTerm = 0;
        InformationService.commitIndex = -1;
        InformationService.currentLog = 0;
        InformationService.votedFor = -1;

        InformationService.currentState = State.FOLLOWER;
        InformationService.currentLog = 0;
        InformationService.lastTimeStampReceived = 0L;
        InformationService.peersLogStatus = new HashMap<>();

        onLeaderPromotion();
        InformationService.logEntryList.add(new LogEntry("init", 0));

        //TODO: remove this. Currently added for testing.
        if (isLeader()) {
            InformationService.logEntryList.add(new LogEntry("init2", 0));
            InformationService.logEntryList.add(new LogEntry("init3", 0));
            InformationService.logEntryList.add(new LogEntry("init4", 0));
            InformationService.logEntryList.add(new LogEntry("init5", 0));
            InformationService.logEntryList.add(new LogEntry("init6", 0));
            InformationService.logEntryList.add(new LogEntry("init7", 0));
            InformationService.logEntryList.add(new LogEntry("init8", 0));
        }
    }

    public static boolean isLeader() {
        //TODO: After leader election code is complete, remove this. Now choosing first host as leader.
        return InformationService.self != null && InformationService.self.hostname.equals(TEMP_LEADER_NAME);
        //return true;
        //return InformationService.leader.equals(InformationService.self);
    }

    public static int getMajorityVote() {
        return (peerList.size() / 2) + 1;
    }

    /**
     * Call this function when after a peer is made a leader.
     */
    public static void onLeaderPromotion() {
        if (!isLeader()) {
            return;
        }
        initPeerLogsStatus();
    }

    private static void initPeerLogsStatus() {
        for (Peer peer : InformationService.peerList) {
            InformationService.peersLogStatus.put(peer, InformationService.logEntryList.size() - 1);
        }
    }

    private void saveHostName() {
        try {
        String hostname = InetAddress.getLocalHost().getHostName().trim();
        InformationService.self = InformationService.peerList.stream()
                .filter(peer -> peer.hostname.equals(hostname)).findFirst().get();
        } catch (Exception ex) {
            System.out.println("Error in getHostname: " + ex.getMessage());
        }
    }

    public Peer getLeader() {
        return leader;
    }

    List<Peer> parseFileAndGetPeers(String peerFile){
        // todo, read from file, right now we are hard coding 5 nodes
        Peer peer1 = new Peer(1, TEMP_LEADER_NAME);
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
