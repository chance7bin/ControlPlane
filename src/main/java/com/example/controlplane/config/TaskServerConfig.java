package com.example.controlplane.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;

/**
 * 任务服务器配置
 *
 * @author 7bin
 * @date 2024/02/26
 */
@Slf4j
@Configuration
public class TaskServerConfig {

    private static String taskServerAddress = "172.21.213.105:27017";

    private static MongoClient mongoClient = connectMongoClient();

    @Bean(name = "serverCollection")
    public MongoCollection<Document> serverCollection() throws SQLException {
        return getTaskServerCollection("server");
    }

    // 饿汉式获取单例mongoclient
    private MongoClient getMongoClient(){
        return mongoClient;
    }

    private static MongoClient connectMongoClient(){
        // MongoClient mongoClient = new MongoClient(MONGO_HOST, MONGO_PORT);
        // new MongoClient新版本中已废弃了 ！
        MongoClient mongoClient = MongoClients.create("mongodb://" + taskServerAddress);
        return mongoClient;
    }

    /**
     * 连TaskServer服务器，得到其中的collection
     * @param  table 数据库中的表名
     * @return com.mongodb.client.MongoCollection<org.bson.Document>
     * @author bin
     **/
    public MongoCollection<Document> getTaskServerCollection(String table){

        String MONGO_DB_NAME = "GeoTaskServerDB";
        String MONGO_COLLECTION_NAME = table;
        //
        // // MongoClient mongoClient = new MongoClient(MONGO_HOST, MONGO_PORT);
        // // new MongoClient新版本中已废弃了 ！
        // MongoClient mongoClient = MongoClients.create("mongodb://" + taskServerAddr);
        MongoDatabase db = getMongoClient().getDatabase(MONGO_DB_NAME);
        MongoCollection<Document> collection = db.getCollection(MONGO_COLLECTION_NAME);

        //查数据库的写法
        // Document filter = new Document();
        // filter.append("_id", new ObjectId(taskId));
        // FindIterable<Document> data = collection
        //     .find(filter)
        //     .skip(0)
        //     .limit(1)
        //     .sort(new Document("t_datetime", -1));

        return collection;
    }

}
