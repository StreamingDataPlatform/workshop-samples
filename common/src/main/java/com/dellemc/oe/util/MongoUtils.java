/**
 * Copyright (c) Dell Inc., or its subsidiaries. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

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