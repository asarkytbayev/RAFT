package com.neu.project3.raft.manager;

import com.neu.project3.raft.factory.HttpEntityFactory;
import com.neu.project3.raft.factory.HttpHeadersFactory;
import com.neu.project3.raft.requests.HeartbeatRequest;
import com.neu.project3.raft.responses.HeartbeatResponse;
import com.neu.project3.raft.service.InformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HeartbeatSender {

    private RestTemplate restTemplate;
    private InformationService informationService;
    private static final String HEARTBEAT_PATH = "";

    @Autowired
    public HeartbeatSender(RestTemplate restTemplate, InformationService informationService){
        this.restTemplate = restTemplate;
        this.informationService = informationService;
    }

    public HeartbeatResponse sendHeartBeat(HeartbeatRequest request, Integer peerId) {
        try {
            HttpEntity payload = HttpEntityFactory.createObjectWithBodyAndHeaders(getHttpHeaderObject(), request);
            return restTemplate.postForObject(getPathToSend(peerId), payload, HeartbeatResponse.class);
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
        return InformationService.peerList.get(peerId - 1).hostname + HEARTBEAT_PATH;
    }
}
