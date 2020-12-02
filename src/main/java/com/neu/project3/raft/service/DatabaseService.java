package com.neu.project3.raft.service;

import com.neu.project3.raft.models.DBRequest;
import com.neu.project3.raft.models.DBResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class DatabaseService {

    private final HashMap<String, String> db;

    @Autowired
    public DatabaseService(){
        this.db = new HashMap<>();
    }

    public synchronized DBResponse getKey(DBRequest request){
        if (db.containsKey(request.getKey())){
            return new DBResponse(request.getKey(), request.getOp(), db.get(request.getKey()));
        }
        return new DBResponse(request.getKey(), request.getOp(), "NOT FOUND");
    }

    public synchronized DBResponse upsertKey(DBRequest request){
        db.put(request.getKey(), request.getValue());
        return new DBResponse(request.getKey(), request.getOp(), db.get(request.getKey()));
    }

    public synchronized DBResponse deleteKey(DBRequest request){
        if (db.containsKey(request.getKey())){
            db.remove(request.getKey());
            return new DBResponse(request.getKey(), request.getOp(), "DELETED");
        }
        return new DBResponse(request.getKey(), request.getOp(), "NOT FOUND");
    }
}
