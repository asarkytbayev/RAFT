package com.neu.project3.raft.models;

import java.io.Serializable;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class LogEntry implements Serializable {
    public String command;
    public Integer term;
}
