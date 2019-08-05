package com.dellemc.oe.util;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import java.util.ArrayList;
import java.util.List;

public class MongoUtils {

    public static MongoClient getConnect(){
        ServerAddress serverAddress = new ServerAddress("localhost", 27017);
        List<MongoCredential> credential = new ArrayList<>();
        //MongoCredential mongoCredential1 = MongoCredential.createScramSha1Credential("nautilus", "test", "nautilus@123".toCharArray());
        //credential.add(mongoCredential1);
        MongoClient mongoClient = new MongoClient(serverAddress);
        return mongoClient;
    }

}