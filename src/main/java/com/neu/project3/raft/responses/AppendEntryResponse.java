package com.neu.project3.raft.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AppendEntryResponse {
    Integer term;
    Boolean success;
    Integer selfId;
    Boolean logInConsistent;
}
