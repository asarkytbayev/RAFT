package com.neu.project3.raft.requests;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class VoteRequest {

    Integer term;
    Integer candidateId;
    Integer lastLogIndex;
    Integer lastLogTerm;
    Integer selfId;

}
