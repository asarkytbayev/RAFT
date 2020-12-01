package com.neu.project3.raft;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neu.project3.raft.models.DBRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RaftApplicationTests {

    @Test
    void contextLoads() {
    }

    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        return mapper;
    }

    @Test
    void obTest() throws JsonProcessingException {
        DBRequest r = new DBRequest("1", "get", "2");
        ObjectMapper ob = objectMapper();
        ob.readValue(r.toString(), DBRequest.class);
    }

}
