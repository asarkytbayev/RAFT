package com.neu.project3.raft.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class VoteResponse {
    Integer term;
    Boolean voteGranted;
    Integer selfId;
}
