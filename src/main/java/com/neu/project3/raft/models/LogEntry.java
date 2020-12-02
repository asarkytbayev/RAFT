package com.neu.project3.raft.models;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class LogEntry {
    public String command;
    public int term;
}
