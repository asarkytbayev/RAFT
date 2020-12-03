package com.neu.project3.raft.manager;

import com.neu.project3.raft.factory.HttpEntityFactory;
import com.neu.project3.raft.factory.HttpHeadersFactory;
import com.neu.project3.raft.requests.AppendEntryRequest;
import com.neu.project3.raft.responses.AppendEntryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AppendRequestSender {

    private final RestTemplate restTemplate;
    private static final String APPEND_PATH = ":8080/append_entry";

    @Autowired
    public AppendRequestSender(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }


    public AppendEntryResponse sendAppendRequest(AppendEntryRequest appendEntryRequest, String receiver) {
        try {
            HttpEntity payload = HttpEntityFactory.createObjectWithBodyAndHeaders(getHttpHeaderObject(), appendEntryRequest);
            return restTemplate.postForObject(getPathToSend(receiver), payload, AppendEntryResponse.class);
        } catch (Exception e) {
            // TODO: long wait after failure to send - why?
//           System.err.println("Error sending append request: " + receiver);
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
        return "http://" + hostname + APPEND_PATH;
    }

}
