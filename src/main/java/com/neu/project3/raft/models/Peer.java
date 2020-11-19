package com.neu.project3.raft.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Peer {
    public Integer id;
    public String hostname;
}
