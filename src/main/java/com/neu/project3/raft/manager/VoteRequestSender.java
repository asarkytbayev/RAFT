package com.neu.project3.raft.manager;

import com.neu.project3.raft.factory.HttpEntityFactory;
import com.neu.project3.raft.factory.HttpHeadersFactory;
import com.neu.project3.raft.requests.VoteRequest;
import com.neu.project3.raft.responses.VoteResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class VoteRequestSender {

    private final RestTemplate restTemplate;

    private static final String VOTE_REQUEST_PATH = ":8080/request_vote";

    @Autowired
    public VoteRequestSender(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public VoteResponse sendVoteRequest(VoteRequest voteRequest, String hostname) {
        try {
            HttpEntity payload = HttpEntityFactory.createObjectWithBodyAndHeaders(getHttpHeaderObject(), voteRequest);
            return restTemplate.postForObject(getPathToSend(hostname), payload, VoteResponse.class);
        }catch (Exception e) {
            System.err.println("sendVoteRequest(): " + hostname);
            return null;
        }
    }

    private HttpHeaders getHttpHeaderObject(){
        HttpHeaders headers = HttpHeadersFactory.getObject();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("cache-control", "no-cache");
        return headers;
    }

    private String getPathToSend(String hostname){
        return "http://" + hostname + VOTE_REQUEST_PATH;
    }
}
