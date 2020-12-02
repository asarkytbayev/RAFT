package com.neu.project3.raft.service;

import com.neu.project3.raft.models.LogEntry;
import com.neu.project3.raft.models.Peer;
import com.neu.project3.raft.models.State;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
@Getter
@Setter
public class InformationService implements Serializable {

    private static String LOCAL_STATE_ROOT_LOCATION = "./data/state_";

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
    public InformationService(@Value("${peer_file_list}") String peerFile) {
        peerList = readServersFile(peerFile);
        this.saveHostName();
        logEntryList = new ArrayList<>();
        currentTerm = 0;
        commitIndex = -1;
        currentLog = 0;
        votedFor = -1;

        currentState = State.FOLLOWER;
        currentLog = 0;
        lastTimeStampReceived = Instant.now().toEpochMilli();
        peersLogStatus = new HashMap<>();

        onLeaderPromotion();
        loadLocalState();
    }


    private void loadLocalState() {
        String hostname = self != null ? self.hostname : null;
        if (hostname == null) {
            return;
        }
        try (
            InputStream fileSt = new FileInputStream(LOCAL_STATE_ROOT_LOCATION + hostname + ".txt");
            ObjectInput objIn = new ObjectInputStream(fileSt)
        ) {
            objIn.readObject();
            System.out.println("Loaded saved state");
        } catch (FileNotFoundException exp) {
            System.err.println("File not found: " + exp.getMessage());
        } catch (Exception exp) {
            System.err.println("Exception while loading state: " + exp.getMessage());
        }
    }


    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        currentTerm = (Integer) in.readObject();
        votedFor = (Integer) in.readObject();
        logEntryList = (List<LogEntry>) in.readObject();
        commitIndex = (Integer) in.readObject();
        currentLog = (Integer) in.readObject();
        lastTimeStampReceived = (Long) in.readObject();
    }

    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        out.defaultWriteObject();
        out.writeObject(currentTerm);
        out.writeObject(votedFor);
        // TODO serialize
        out.writeObject(logEntryList);
        out.writeObject(commitIndex);
        out.writeObject(currentLog);
        out.writeObject(lastTimeStampReceived);
    }

    private void readObjectNoData()
            throws ObjectStreamException {
        // TODO
    }


    @Scheduled(fixedDelay = 1000)
    private void saveLocalState() {
        String hostname = self != null ? self.hostname : null;
        if (hostname == null) {
            return;
        }
        try (
            OutputStream fileSt = new FileOutputStream(LOCAL_STATE_ROOT_LOCATION+ hostname + ".txt", false);
            ObjectOutput objSt = new ObjectOutputStream(fileSt)
        ) {
            // TODO serialization
//            objSt.writeObject(this);
            System.out.println("Saved local state");
        } catch (Exception exp) {
            System.err.println("Exception while saving state: " + exp.getMessage());
            exp.printStackTrace();
        }
    }

    public static boolean isLeader() {
        //TODO: After leader election code is complete, remove this. Now choosing first host as leader.
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
            self = peerList.stream()
                    .filter(peer -> peer.hostname.equals(hostname)).findFirst().get();
        } catch (Exception ex) {
            System.out.println("Error in saveHostName(): " + ex.getMessage());
        }
    }

    private List<Peer> readServersFile(String filePath) {
        List<Peer> peers = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            stream.map(this::constructPeer).forEach(peers::add);
        } catch (IOException e) {
            System.err.println("Failed to open servers file");
            e.printStackTrace();
        }
        return peers;
    }

    private Peer constructPeer(String hostname) {
        Integer id = Integer.parseInt(hostname.replaceAll("\\D+",""));
        return new Peer(id, hostname);
    }
}
