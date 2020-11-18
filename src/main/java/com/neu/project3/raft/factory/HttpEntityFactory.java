package com.neu.project3.raft.factory;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

public interface HttpEntityFactory {

    static HttpEntity<Object> createObjectWithBodyAndHeaders(HttpHeaders requestHeaders, Object body){
        HttpEntity<Object> request;
        request = new HttpEntity<>(body, requestHeaders);
        return request;
    }

    static HttpEntity<Object> createObjectWithHeaders(HttpHeaders requestHeaders){
        HttpEntity<Object> request;
        request = new HttpEntity<>(requestHeaders);
        return request;
    }

    static HttpEntity<Object> createObjectWithBody(Object body){
        HttpEntity<Object> request;
        request = new HttpEntity<>(body);
        return request;
    }

}
