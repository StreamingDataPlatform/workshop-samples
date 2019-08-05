package com.dellemc.oe.db;

import com.dellemc.oe.model.JSONData;
import com.dellemc.oe.util.MongoUtils;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;


public class MongoDBSink extends RichSinkFunction<JSONData> {
    private static final long serialVersionUID = 1L;
    MongoClient mongoClient = null;

    public void invoke(JSONData value) {
        try {
            if (mongoClient != null) {
                mongoClient = MongoUtils.getConnect();
                MongoDatabase db = mongoClient.getDatabase("test");
                MongoCollection collection = db.getCollection("junk");
                List<Document> list = new ArrayList<>();
                //  {"id":0.6748568994039477,"name":"DELL EMC","building":3,"location":"India"}
                Document doc = new Document();
                doc.append("ID", value.id);
                doc.append("NAME", value.name);
                doc.append("BUILDING", value.building);
                doc.append("LOCATION", value.location);
                list.add(doc);
                System.out.println("Insert Starting");
                collection.insertMany(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void open(Configuration parms) throws Exception {
        super.open(parms);
        mongoClient = MongoUtils.getConnect();
    }

    public void close() throws Exception {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}