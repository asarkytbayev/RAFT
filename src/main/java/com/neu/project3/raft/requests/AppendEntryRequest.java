package com.neu.project3.raft.requests;

import java.util.List;

public class AppendEntryRequest {

    String term;
    String leaderId;
    Integer prevLogIndex;
    String prevLogTerm;
    List<String> entries;
    Integer commitIndex;
}
