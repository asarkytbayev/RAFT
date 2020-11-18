package com.neu.project3.raft.manager;

import com.neu.project3.raft.factory.HttpEntityFactory;
import com.neu.project3.raft.factory.HttpHeadersFactory;
import com.neu.project3.raft.requests.AppendEntryRequest;
import com.neu.project3.raft.responses.AppendEntryResponse;
import com.neu.project3.raft.service.InformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AppendRequestSender {

    private RestTemplate restTemplate;
    private InformationService informationService;
    private static final String APPEND_PATH = "";

    @Autowired
    public AppendRequestSender(RestTemplate restTemplate, InformationService informationService){
        this.restTemplate = restTemplate;
        this.informationService = informationService;
    }

    public AppendEntryResponse sendAppendResponse(AppendEntryRequest appendEntryRequest) {
        try {
            HttpEntity payload = HttpEntityFactory.createObjectWithBodyAndHeaders(getHttpHeaderObject(), appendEntryRequest);
            return restTemplate.postForObject(getPathToSend(appendEntryRequest), payload, AppendEntryResponse.class);
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

    private String getPathToSend(AppendEntryRequest appendEntryRequest){
        return informationService.getLeader().hostname + APPEND_PATH;
    }

}
