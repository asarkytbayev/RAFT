package com.neu.project3.raft.models;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class DBRequest {
    private String key;
    private String op;
    private String value;

    public String toString(){
        return "{'key':" + key + ",'value':" + value + ",'op':'" + op + "'}";
    }
}
