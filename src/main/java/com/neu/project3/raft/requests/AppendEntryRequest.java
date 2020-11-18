package com.neu.project3.raft.requests;

import com.neu.project3.raft.models.LogEntry;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class AppendEntryRequest {

    Integer term;
    Integer leaderId;
    Integer prevLogIndex;
    Integer prevLogTerm;
    List<LogEntry> entries;
    Integer leaderCommit;
    Integer selfId;
}
