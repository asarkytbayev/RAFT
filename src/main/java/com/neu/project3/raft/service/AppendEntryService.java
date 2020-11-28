package com.neu.project3.raft.service;

import com.neu.project3.raft.manager.AppendRequestSender;
import com.neu.project3.raft.models.LogEntry;
import com.neu.project3.raft.models.Peer;
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
        InformationService.lastTimeStampReceived = Instant.now().toEpochMilli();
//        System.out.println("Received append entry request from: " + appendEntryRequest.getSelfId());

        int prevLogIndex = appendEntryRequest.getPrevLogIndex();
        int prevLogTerm = appendEntryRequest.getPrevLogTerm();
        if (InformationService.currentTerm > appendEntryRequest.getTerm()){
            return new AppendEntryResponse(InformationService.currentTerm, false, InformationService.self.id,
                    false);
        }
        //Reply false if log does not contain an entry at prevLogIndex.
        if (prevLogIndex >= InformationService.logEntryList.size()) {
            return new AppendEntryResponse(InformationService.currentTerm, false, InformationService.self.id,
                    true);
        }
        //Reply false if log contains an entry at prevLogIndex but it's term does not match prevLogTerm
        LogEntry logEntry = prevLogIndex >= 0 ? InformationService.logEntryList.get(prevLogIndex) : null;
        if (prevLogTerm >= 0 && logEntry != null &&  logEntry.term != prevLogTerm) {
            return new AppendEntryResponse(InformationService.currentTerm, false, InformationService.self.id,
                    true);
        }
        return acceptAppendEntryRequest(appendEntryRequest);
    }

    public AppendEntryResponse acceptAppendEntryRequest(AppendEntryRequest appendReq) {
        // TODO: Test the behaviour.
        int prevLogIndex = appendReq.getPrevLogIndex();
        List<LogEntry> entriesToAdd = appendReq.getEntries();
        InformationService.logEntryList = InformationService.logEntryList.subList(0, prevLogIndex + 1);
        InformationService.logEntryList.addAll(entriesToAdd);

        InformationService.commitIndex = Math.min(appendReq.getLeaderCommit(),
                InformationService.logEntryList.size() - 1);
        System.out.println("Current State: " + InformationService.logEntryList.toString());
        return new AppendEntryResponse(InformationService.currentTerm, true, InformationService.self.id,
                false);
        // 3. if an existing entry conflicts with a new one, delete the existing entry and all that follow it
        // 4. append any new entries not in the log
        // 5. if leader commit > commit index, set commit index = min(leaderCommit, index of last new entry)
    }

    private AppendEntryRequest getAppendRequest() {
        AppendEntryRequest appendReq = new AppendEntryRequest();
        appendReq.setTerm(InformationService.currentTerm);
        appendReq.setLeaderId(InformationService.self.getId());
        appendReq.setLeaderCommit(InformationService.commitIndex);
        appendReq.setSelfId(InformationService.self.id);
        return appendReq;
    }

    /**
     * Update commit index based on log indices of the peers.
     */
    public static int getCommitIndex(List<Integer> peerReplicationIndices, int minimumVotes) {
        System.out.println("getCommitIndex()");
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
        if (!InformationService.isLeader()) {
            return;
        }
//        InformationService.lastTimeStampReceived = Instant.now().toEpochMilli();
//        System.out.println("I'm a " + InformationService.currentState);
        List<Integer> peerReplicationIndices = new ArrayList<>();
        for (Peer peer: InformationService.peerList) {
            if (peer.equals(InformationService.self)) {
                peerReplicationIndices.add(InformationService.logEntryList.size() - 1);
                continue;
            }
            Integer currentPeerLogIndex = -1;
            if (!InformationService.peersLogStatus.isEmpty()) {
                currentPeerLogIndex = InformationService.peersLogStatus.get(peer);
            }
            int lastEntryTerm = -1;

            // TODO initial election
            // Check if the the index is within boundaries of the log entries.
            if (currentPeerLogIndex >= 0 && currentPeerLogIndex < InformationService.logEntryList.size()) {
                if (!InformationService.logEntryList.isEmpty()) {
                    lastEntryTerm = InformationService.logEntryList.get(currentPeerLogIndex).getTerm();
                }
            }
            List<LogEntry> logEntries = new ArrayList<>();
            //Add entries not present in peers that have to be appended.
            for (int ii = Math.max(currentPeerLogIndex, 0); ii < InformationService.logEntryList.size(); ii++) {
                if (logEntries.size() >= MAX_LOGS_TO_ADD) {
                    break;
                }
                if (ii > currentPeerLogIndex) {
                    logEntries.add(InformationService.logEntryList.get(ii));
                }
            }
            AppendEntryRequest appendReq = getAppendRequest();
            appendReq.setPrevLogIndex(currentPeerLogIndex);
            appendReq.setPrevLogTerm(lastEntryTerm);
            appendReq.setEntries(logEntries);

            AppendEntryResponse response = this.appendRequestSender.sendAppendRequest(appendReq, peer.getHostname());
            if (response != null) {
                InformationService.lastTimeStampReceived = Instant.now().toEpochMilli();
                if (!response.getLogInConsistent()) {
                    //Able to append logs. So increment the index based on the count of logs added.
                    if (!InformationService.peersLogStatus.isEmpty()) {
                        int oldIndex = InformationService.peersLogStatus.get(peer);
                        //System.out.println("Merge Success: " + currentPeerLogIndex + " " + response.toString());
                        InformationService.peersLogStatus.put(peer, oldIndex + logEntries.size());
                        peerReplicationIndices.add(oldIndex + logEntries.size());
                    }
                } else {
                    //System.out.println("Merge Failed: " + currentPeerLogIndex + " " + response.toString());
                    //Decrement log index if peer doesn't have the entry sent to it currently.
                    InformationService.peersLogStatus.put(peer, Math.max(-1, currentPeerLogIndex - 1));
                }
            } else {
                System.out.println("Null response: " + peer.toString());
            }
        }
        InformationService.commitIndex = getCommitIndex(peerReplicationIndices,
                InformationService.getMajorityVote());
    }


}
