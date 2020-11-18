package com.neu.project3.raft.service;

import com.neu.project3.raft.models.Peer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InformationService {

    private List<Peer> peerList;
    private Peer leader;

    @Autowired
    public InformationService(@Value("peer_file_list") String peerFile, @Value("self_id") String selfId){

    }

    public Peer getLeader(){
        return leader;
    }


}
