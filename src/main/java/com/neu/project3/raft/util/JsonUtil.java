package com.neu.project3.raft.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JsonUtil {

    private ObjectMapper objectMapper;

    @Autowired
    public JsonUtil(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    public  <T> String write(T object) throws IOException {
        return objectMapper.writeValueAsString(object);
    }

    public <T> T getObject(String json, Class<T> type) throws IOException {
        return objectMapper.readValue(json, type);
    }

    public  <T> List<T> getListObject(String json, TypeReference<List<T>> typeReference) throws IOException {
        return objectMapper.readValue(json, typeReference);
    }

    public <T> T getObjectFromBodyByKey(String body, String key, Class<T> type) throws IOException, JSONException {
        String json = new JSONObject(body).get(key).toString();
        return getObject(json,type);
    }

    public boolean keyInBody(String body, String key) throws JSONException {
        JSONObject json = new JSONObject(body);
        return json.has(key);
    }

    public Map<String, Object> convertToMap(Object type){
        return objectMapper.convertValue(type, new TypeReference<Map<String, Object>>() {});
    }

    public JSONObject createJsonObjectFromString(String body) throws JSONException {
        return new JSONObject(body);
    }

    public <T> T getObject(Map<String, Object> map, Class<T> type){
        return objectMapper.convertValue(map, type);
    }

    @SuppressWarnings("unchecked")
    public Map<String,Object> getHashMapFromString(String json) throws IOException {
        return getObject(json, HashMap.class);
    }

    @SuppressWarnings("unchecked")
    public Map<String,String> getStringHashMapFromString(String json) throws IOException {
        return getObject(json, HashMap.class);
    }
}

