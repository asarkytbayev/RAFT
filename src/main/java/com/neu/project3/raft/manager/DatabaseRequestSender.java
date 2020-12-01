package com.neu.project3.raft.manager;

import com.neu.project3.raft.factory.HttpEntityFactory;
import com.neu.project3.raft.factory.HttpHeadersFactory;
import com.neu.project3.raft.models.DBRequest;
import com.neu.project3.raft.models.DBResponse;
import com.neu.project3.raft.service.InformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DatabaseRequestSender {

    private RestTemplate restTemplate;
    private InformationService informationService;
    private static final String GET_PATH = ":8080/get_key";
    private static final String UPSERT_PATH = ":8080/upsert_key";
    private static final String DELETE_PATH = ":8080/delete_key";

    @Autowired
    public DatabaseRequestSender(RestTemplate restTemplate, InformationService informationService){
        this.restTemplate = restTemplate;
        this.informationService = informationService;
    }

    public DBResponse sendGet(DBRequest request) {
        try {
            HttpEntity payload = HttpEntityFactory.createObjectWithBodyAndHeaders(getHttpHeaderObject(), request);
            return restTemplate.postForObject(getPathToSend(InformationService.leader.hostname), payload, DBResponse.class);
        } catch (Exception e) {
            return null;
        }
    }

    public DBResponse sendUpsert(DBRequest request) {
        try {
            HttpEntity payload = HttpEntityFactory.createObjectWithBodyAndHeaders(getHttpHeaderObject(), request);
            return restTemplate.postForObject(getUpsertPathToSend(InformationService.leader.hostname), payload, DBResponse.class);
        } catch (Exception e) {
            return null;
        }
    }

    public DBResponse sendDelete(DBRequest request) {
        try {
            HttpEntity payload = HttpEntityFactory.createObjectWithBodyAndHeaders(getHttpHeaderObject(), request);
            return restTemplate.postForObject(getDeletePath(InformationService.leader.hostname), payload, DBResponse.class);
        } catch (Exception e) {
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
        return "http://" + hostname + GET_PATH;
    }
    private String getUpsertPathToSend(String hostname){
        return "http://" + hostname + UPSERT_PATH;
    }
    private String getDeletePath(String hostname){
        return "http://" + hostname + DELETE_PATH;
    }
}
