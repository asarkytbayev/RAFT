package com.neu.project3.raft.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VoteRequest {

    Integer term;
    Integer candidateId;
    Integer lastLogIndex;
    Integer lastLogTerm;
    Integer selfId;

}
