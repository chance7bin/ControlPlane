package com.example.controlplane.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.example.controlplane.clients.DynamicUrlInterceptor;
import com.example.controlplane.clients.RemoteApiClient;
import com.example.controlplane.service.ITaskServerService;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 7bin
 * @date 2024/02/26
 */
@Slf4j
@Service
public class TaskServerServiceImpl implements ITaskServerService {

    @Resource(name="serverCollection")
    MongoCollection<Document> serverCollection;

    @Autowired
    RemoteApiClient remoteApiClient;

    @Value("${taskServerUrl}")
    private String taskServerUrl;

    @Override
    public List<JSONObject> getServerList() {
        FindIterable<Document> result = serverCollection.find();

        List<JSONObject> list = new ArrayList<>();

        for (Document document : result) {
            JSONObject object = new JSONObject();
            object.put("ip",document.getString("s_ip"));
            object.put("port",document.getInteger("s_port"));
            object.put("type",document.getInteger("s_type"));
            object.put("status",document.getBoolean("s_status"));
            list.add(object);
        }

        return list;
    }


    public List<String> getDeployedNodeByPid(String pid){
        DynamicUrlInterceptor.setDynamicUrl(taskServerUrl);
        String rsp = remoteApiClient.getDeployedNodeByPid(pid);
        JSONObject jsonObject = JSONObject.parseObject(rsp);
        if(jsonObject.getInteger("code") == 1){
            return jsonObject.getJSONArray("data").toList(String.class);
        }
        return null;
    }
}
