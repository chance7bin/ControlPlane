package com.example.controlplane.service;

import com.alibaba.fastjson2.JSONObject;
import com.example.controlplane.entity.bo.Server;
import com.example.controlplane.entity.dto.FindDTO;
import com.example.controlplane.entity.dto.LabelDTO;
import com.example.controlplane.entity.dto.page.PageInfo;
import com.example.controlplane.entity.po.Node;

import java.util.List;

/**
 * @author 7bin
 * @date 2024/02/26
 */
public interface INodeService {

    List<JSONObject> getTaskNodeList();

    Node getNodeByIp(String ip);

    Server getRemoteNodeStatus(String ip);

    void updateRemoteNode();


    PageInfo<Node> getNodeList(FindDTO findDTO);

    void updateLabel(LabelDTO labelDTO);


    Node getNodeById(String id);
}
