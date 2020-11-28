package com.neu.project3.raft.service;

import com.neu.project3.raft.manager.AppendRequestSender;
import com.neu.project3.raft.models.LogEntry;
import com.neu.project3.raft.models.Peer;
import com.neu.project3.raft.models.State;
import com.neu.project3.raft.requests.AppendEntryRequest;
import com.neu.project3.raft.responses.AppendEntryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppendEntryService {

    private InformationService informationService;

    private AppendRequestSender appendRequestSender;

    private final int MAX_LOGS_TO_ADD = 5;

    @Autowired
    public AppendEntryService(InformationService informationService,
                              AppendRequestSender appendRequestSender) {
        this.informationService = informationService;
        this.appendRequestSender = appendRequestSender;
    }

    public synchronized AppendEntryResponse handleAppendEntryRequest(AppendEntryRequest appendEntryRequest) {
        informationService.lastTimeStampReceived = Instant.now().toEpochMilli();
        // TODO how does this work with statements below?
        if (appendEntryRequest.getTerm() >= informationService.currentTerm) {
            informationService.currentState = State.FOLLOWER;
            informationService.currentTerm = appendEntryRequest.getTerm();
        }

//        System.out.println("Received append entry request from: " + appendEntryRequest.getSelfId());

        int prevLogIndex = appendEntryRequest.getPrevLogIndex();
        int prevLogTerm = appendEntryRequest.getPrevLogTerm();
        if (informationService.currentTerm > appendEntryRequest.getTerm()) {
            return new AppendEntryResponse(informationService.currentTerm, false, informationService.self.id,
                    false);
        }
        //Reply false if log does not contain an entry at prevLogIndex.
        if (prevLogIndex >= informationService.logEntryList.size()) {
            return new AppendEntryResponse(informationService.currentTerm, false, informationService.self.id,
                    true);
        }
        //Reply false if log contains an entry at prevLogIndex but it's term does not match prevLogTerm
        LogEntry logEntry = prevLogIndex >= 0 ? informationService.logEntryList.get(prevLogIndex) : null;
        if (prevLogTerm >= 0 && logEntry != null &&  logEntry.term != prevLogTerm) {
            return new AppendEntryResponse(informationService.currentTerm, false, informationService.self.id,
                    true);
        }
        return acceptAppendEntryRequest(appendEntryRequest);
    }

    public AppendEntryResponse acceptAppendEntryRequest(AppendEntryRequest appendReq) {
        // TODO: Test the behaviour.
        int prevLogIndex = appendReq.getPrevLogIndex();
        List<LogEntry> entriesToAdd = appendReq.getEntries();
        informationService.logEntryList = informationService.logEntryList.subList(0, prevLogIndex + 1);
        informationService.logEntryList.addAll(entriesToAdd);

        informationService.commitIndex = Math.min(appendReq.getLeaderCommit(),
                informationService.logEntryList.size() - 1);
//        System.out.println("Current State: " + informationService.logEntryList.toString());
        return new AppendEntryResponse(informationService.currentTerm, true, informationService.self.id,
                false);
        // 3. if an existing entry conflicts with a new one, delete the existing entry and all that follow it
        // 4. append any new entries not in the log
        // 5. if leader commit > commit index, set commit index = min(leaderCommit, index of last new entry)
    }

    private AppendEntryRequest getAppendRequest() {
        AppendEntryRequest appendReq = new AppendEntryRequest();
        appendReq.setTerm(informationService.currentTerm);
        appendReq.setLeaderId(informationService.self.getId());
        appendReq.setLeaderCommit(informationService.commitIndex);
        appendReq.setSelfId(informationService.self.id);
        return appendReq;
    }

    /**
     * Update commit index based on log indices of the peers.
     */
    public static int getCommitIndex(List<Integer> peerReplicationIndices, int minimumVotes) {
//        System.out.println("getCommitIndex()");
        peerReplicationIndices = peerReplicationIndices.stream().
                filter(val -> val >= 0).collect(Collectors.toList());
        peerReplicationIndices.sort(Integer::compareTo);
        Collections.reverse(peerReplicationIndices);
        int result = -1;
        for (int ii = 0; ii < peerReplicationIndices.size(); ii++) {
            if (ii + 1 >= minimumVotes) {
                result = peerReplicationIndices.get(ii);
                break;
            }
        }
        return result;
    }

    @Scheduled(fixedDelay = 100)
    void sendAppendEntriesToPeers() {
        if (!informationService.isLeader()) {
            return;
        }
//        informationService.lastTimeStampReceived = Instant.now().toEpochMilli();
//        System.out.println("I'm a " + informationService.currentState);
        List<Integer> peerReplicationIndices = new ArrayList<>();
        for (Peer peer: informationService.peerList) {
            if (peer.equals(informationService.self)) {
                peerReplicationIndices.add(informationService.logEntryList.size() - 1);
                continue;
            }
            Integer currentPeerLogIndex = -1;
            if (!informationService.peersLogStatus.isEmpty()) {
                currentPeerLogIndex = informationService.peersLogStatus.get(peer);
            }
            int lastEntryTerm = -1;

            // TODO initial election
            // Check if the the index is within boundaries of the log entries.
            if (currentPeerLogIndex >= 0 && currentPeerLogIndex < informationService.logEntryList.size()) {
                if (!informationService.logEntryList.isEmpty()) {
                    lastEntryTerm = informationService.logEntryList.get(currentPeerLogIndex).getTerm();
                }
            }
            List<LogEntry> logEntries = new ArrayList<>();
            //Add entries not present in peers that have to be appended.
            for (int ii = Math.max(currentPeerLogIndex, 0); ii < informationService.logEntryList.size(); ii++) {
                if (logEntries.size() >= MAX_LOGS_TO_ADD) {
                    break;
                }
                if (ii > currentPeerLogIndex) {
                    logEntries.add(informationService.logEntryList.get(ii));
                }
            }
            AppendEntryRequest appendReq = getAppendRequest();
            appendReq.setPrevLogIndex(currentPeerLogIndex);
            appendReq.setPrevLogTerm(lastEntryTerm);
            appendReq.setEntries(logEntries);

            AppendEntryResponse response = this.appendRequestSender.sendAppendRequest(appendReq, peer.getHostname());
            if (response != null) {
                informationService.lastTimeStampReceived = Instant.now().toEpochMilli();
                if (!response.getLogInConsistent()) {
                    //Able to append logs. So increment the index based on the count of logs added.
                    if (!informationService.peersLogStatus.isEmpty()) {
                        int oldIndex = informationService.peersLogStatus.get(peer);
                        //System.out.println("Merge Success: " + currentPeerLogIndex + " " + response.toString());
                        informationService.peersLogStatus.put(peer, oldIndex + logEntries.size());
                        peerReplicationIndices.add(oldIndex + logEntries.size());
                    }
                } else {
                    //System.out.println("Merge Failed: " + currentPeerLogIndex + " " + response.toString());
                    //Decrement log index if peer doesn't have the entry sent to it currently.
                    informationService.peersLogStatus.put(peer, Math.max(-1, currentPeerLogIndex - 1));
                }
            } else {
//                System.out.println("Null response: " + peer.toString());
            }
        }
        informationService.commitIndex = getCommitIndex(peerReplicationIndices,
                informationService.getMajorityVote());
    }


}
