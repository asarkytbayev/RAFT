package com.neu.project3.raft.factory;

import org.springframework.http.HttpHeaders;

public interface HttpHeadersFactory {

    static HttpHeaders getObject(){
        return new HttpHeaders();
    }
}
