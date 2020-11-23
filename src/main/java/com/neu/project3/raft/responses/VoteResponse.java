package com.neu.project3.raft.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VoteResponse {
    Integer term;
    Boolean voteGranted;
    Integer selfId;
}
