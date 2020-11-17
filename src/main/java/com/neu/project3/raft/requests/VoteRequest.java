package com.neu.project3.raft.requests;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VoteRequest {

    String term;
    String candidateId;
    Integer lastLogIndex;
    Integer lastLogTerm;

}
