package com.neu.project3.raft.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class AppendEntryResponse {
    Integer term;
    Boolean success;
    Integer selfId;

}
