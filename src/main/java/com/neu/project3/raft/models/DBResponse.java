package com.neu.project3.raft.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class DBResponse {
    private String key;
    private String op;
    private String value;
}
