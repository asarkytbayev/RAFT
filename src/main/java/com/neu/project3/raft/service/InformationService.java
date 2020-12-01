package com.neu.project3.raft.service;

import com.neu.project3.raft.models.LogEntry;
import com.neu.project3.raft.models.Peer;
import com.neu.project3.raft.models.State;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Transient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Getter
@Setter
public class InformationService implements Serializable {

    private static String LOCAL_STATE_ROOT_LOCATION = "./state_";
    //private static String LOCAL_STATE_ROOT_LOCATION = "/app/data/state_";

    /* Temporary leader for now. */
//    private static String TEMP_LEADER_NAME = "hostname1";

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
    public static volatile Long leaderTimeStamp;

    public static Map<Peer, Integer> peersLogStatus;

    @Autowired
    public InformationService(@Value("${peer_file_list}") String peerFile, @Value("${self_id}") String selfId) {
        this.peerList = parseFileAndGetPeers(peerFile);
        this.saveHostName();
//        this.peerList = readServersFile();
        //this.self = this.peerList.get(Integer.parseInt(selfId)-1);
        this.logEntryList = new ArrayList<>();
        this.currentTerm = 0;
        this.commitIndex = -1;
        this.currentLog = 0;
        this.votedFor = -1;

        this.currentState = State.FOLLOWER;
        this.currentLog = 0;
        this.lastTimeStampReceived = Instant.now().toEpochMilli();;
        this.peersLogStatus = new HashMap<>();
//        this.self = this.peerList.get(Integer.parseInt(selfId) - 1);

        onLeaderPromotion();
        loadLocalState();
//        this.logEntryList.add(new LogEntry("init", 0));
//
//        //TODO: remove this. Currently added for testing.
//        if (isLeader()) {
//            this.logEntryList.add(new LogEntry("init2", 0));
//            this.logEntryList.add(new LogEntry("init3", 0));
//            this.logEntryList.add(new LogEntry("init4", 0));
//            this.logEntryList.add(new LogEntry("init5", 0));
//            this.logEntryList.add(new LogEntry("init6", 0));
//            this.logEntryList.add(new LogEntry("init7", 0));
//            this.logEntryList.add(new LogEntry("init8", 0));
//        }
//        System.out.println(this.self.hostname);
    }


    private void loadLocalState() {
        String hostname = self != null ? self.hostname : null;
        if (hostname == null) {
            return;
        }
        try {
            InputStream fileSt = new FileInputStream(LOCAL_STATE_ROOT_LOCATION + hostname + ".txt");
            ObjectInput objOut = new ObjectInputStream(fileSt);
            objOut.readObject();
        } catch (FileNotFoundException exp) {
        } catch (Exception exp) {
            System.out.println("Exception while loading state: " + exp.getMessage());
        }
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        logEntryList = (List<LogEntry>) in.readObject();
        commitIndex = (Integer) in.readObject();
        currentLog = (Integer) in.readObject();
        lastTimeStampReceived = (Long) in.readObject();
        lastTimeStampReceived = (Long) in.readObject();
    }

    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        out.defaultWriteObject();
        //out.writeObject(votedFor);
        //out.writeObject(currentTerm);
        out.writeObject(logEntryList);
        out.writeObject(commitIndex);
        //out.writeObject(lastApplied);
        //out.writeObject(nextIndex);
        //out.writeObject(matchIndex);
        //out.writeObject(peerList);
        //out.writeObject(leader);
        //out.writeObject(currentState);
        out.writeObject(currentLog);
        out.writeObject(lastTimeStampReceived);
        out.writeObject(lastTimeStampReceived);
    }

    @Scheduled(fixedDelay = 7000)
    private void saveLocalState() {
        String hostname = self != null ? self.hostname : null;
        if (hostname == null) {
            return;
        }
        try {
            OutputStream fileSt = new FileOutputStream(LOCAL_STATE_ROOT_LOCATION+ hostname + ".txt", false);
            ObjectOutput objSt = new ObjectOutputStream(fileSt);
            objSt.writeObject(this);
            objSt.close();
            fileSt.close();
        } catch (Exception exp) {
            System.out.println("Exception while saving state: " + exp.getMessage());
        }
    }

    public static boolean isLeader() {
        //TODO: After leader election code is complete, remove this. Now choosing first host as leader.
//        return this.self != null && this.self.hostname.equals(TEMP_LEADER_NAME);
        //return true;
//        return this.leader.equals(this.self);
        return currentState == State.LEADER;
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
            if (!InformationService.logEntryList.isEmpty()) {
                InformationService.peersLogStatus.put(peer, InformationService.logEntryList.size() - 1);
            } else {
                // TODO
            }
        }
    }

    private void saveHostName() {
        try {
            String hostname = InetAddress.getLocalHost().getHostName().trim();
            System.out.println(hostname);
            this.self = this.peerList.stream()
                    .filter(peer -> peer.hostname.equals(hostname)).findFirst().get();
        } catch (Exception ex) {
            System.out.println("Error in saveHostName(): " + ex.getMessage());
        }
    }

    public Peer getLeader() {
        return leader;
    }

    List<Peer> parseFileAndGetPeers(String peerFile) {
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

    private List<Peer> readServersFile() {
//        String filePath = "src/main/java/com/neu/project3/raft/data/hostnames";
        String filePath = "com/neu/project3/raft/data/hostnames";
        List<Peer> peers = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            stream.map(this::constructPeer).forEach(peers::add);
//            peers.forEach(System.out::println);
        } catch (IOException e) {
            System.err.println("Failed to open servers file");
            e.printStackTrace();
        }
        return peers;
    }

    private Peer constructPeer(String hostname) {
        Integer id = Integer.parseInt(hostname.replaceAll("\\D+",""));
        String name = hostname;
        return new Peer(id, name);
    }
}
