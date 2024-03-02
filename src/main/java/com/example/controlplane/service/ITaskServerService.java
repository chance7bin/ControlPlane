package com.example.controlplane.service;

import com.alibaba.fastjson2.JSONObject;

import java.util.List;

/**
 * 任务服务器信息
 *
 * @author 7bin
 * @date 2024/02/26
 */
public interface ITaskServerService {

    List<JSONObject> getServerList();

    List<String> getDeployedNodeByPid(String pid);

}
