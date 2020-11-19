package com.neu.project3.raft.manager;

import com.neu.project3.raft.factory.HttpEntityFactory;
import com.neu.project3.raft.factory.HttpHeadersFactory;
import com.neu.project3.raft.requests.AppendEntryRequest;
import com.neu.project3.raft.requests.VoteRequest;
import com.neu.project3.raft.responses.AppendEntryResponse;
import com.neu.project3.raft.responses.VoteResponse;
import com.neu.project3.raft.service.InformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class VoteRequestSender {

    private RestTemplate restTemplate;
    private InformationService informationService;
    private static final String VOTE_REQUEST_PATH = "";

    @Autowired
    public VoteRequestSender(RestTemplate restTemplate, InformationService informationService){
        this.restTemplate = restTemplate;
        this.informationService = informationService;
    }

    public VoteResponse sendVoteRequest(VoteRequest voteRequest, Integer peerId) {
        try {
            HttpEntity payload = HttpEntityFactory.createObjectWithBodyAndHeaders(getHttpHeaderObject(), voteRequest);
            return restTemplate.postForObject(getPathToSend(peerId), payload, VoteResponse.class);
        }catch (Exception e) {
            // todo
            return null;
        }
    }

    private HttpHeaders getHttpHeaderObject(){
        HttpHeaders headers = HttpHeadersFactory.getObject();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("cache-control", "no-cache");
        return headers;
    }

    private String getPathToSend(Integer peerId){
        return informationService.getPeerList().get(peerId - 1).hostname + VOTE_REQUEST_PATH;
    }
}
