package com.example.controlplane.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.controlplane.clients.EngineClient;
import com.example.controlplane.clients.NodeClient;
import com.example.controlplane.constant.NodeConstants;
import com.example.controlplane.dao.NodeDao;
import com.example.controlplane.entity.bo.Label;
import com.example.controlplane.entity.bo.Server;
import com.example.controlplane.entity.dto.FindDTO;
import com.example.controlplane.entity.dto.LabelDTO;
import com.example.controlplane.entity.dto.page.PageInfo;
import com.example.controlplane.entity.po.Node;
import com.example.controlplane.exception.ServiceException;
import com.example.controlplane.manager.ThreadPoolManager;
import com.example.controlplane.service.INodeService;
import com.example.controlplane.service.ITaskServerService;
import com.example.controlplane.utils.Threads;
import com.example.controlplane.utils.file.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

/**
 * @author 7bin
 * @date 2024/02/26
 */
@Slf4j
@Service
public class NodeServiceImpl implements INodeService {

    @Autowired
    private ITaskServerService taskServerService;

    @Autowired
    private NodeClient nodeClient;

    @Autowired
    private NodeDao nodeDao;


    @Autowired
    EngineClient engineClient;

    @Value("${msMinVersion}")
    String msMinVersion;

    @Override
    public List<JSONObject> getTaskNodeList() {
        return taskServerService.getServerList();
    }

    @Override
    public Node getNodeByIp(String ip) {

        return nodeDao.findFirstByIp(ip);

    }

    public Server getRemoteNodeStatus(String ip) {
        JSONObject obj = nodeClient.getRemoteNodeStatus(ip);
        Server server = formatServerInfo(obj);
        return server;
    }

    @Override
    public void updateRemoteNode() {
        List<JSONObject> nodes = getTaskNodeList();
        for (JSONObject node : nodes) {
            String ip = node.getString("ip");
            boolean online = node.getBoolean("status");
            if (online) {

                // 执行异步任务
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        // log.info("update node info: [{}]", ip);
                        try {
                            Server server = getRemoteNodeStatus(ip);
                            Node node1 = getNodeByIp(ip);
                            server.setDeployDocker(engineClient.checkDocker(ip));
                            String status = geVersion(server) ? NodeConstants.ONLINE : NodeConstants.LOW_VERSION;
                            int order = geVersion(server) ? NodeConstants.ONLINE_ORDER : NodeConstants.LOW_VERSION_ORDER;
                            if (node1 != null) {
                                node1.setStatus(status);
                                node1.setServer(server);
                                node1.setOrder(order);
                                updateNodeLabelsByServer(node1, server);
                                node1.setUpdateTime(new Date());
                                nodeDao.save(node1);
                            } else {
                                Node node2 = new Node();
                                node2.setIp(ip);
                                node2.setStatus(status);
                                node2.setServer(server);
                                node2.setOrder(order);
                                updateNodeLabelsByServer(node2, server);
                                nodeDao.insert(node2);
                            }
                        } catch (Exception e) {
                            log.warn("failed to get remote node status : [{}]", ip);
                            updateNodeWhenOffline(ip);

                        }
                    }
                };
                ThreadPoolManager.instance().execute(task);
                // 睡100ms
                Threads.sleep(100);
            } else {
                updateNodeWhenOffline(ip);
            }

        }
    }

    @Override
    public PageInfo<Node> getNodeList(FindDTO findDTO) {

        findDTO.setSortField("order");
        findDTO.setAsc(true);
        Page<Node> nodePage = nodeDao.findAll(findDTO.getPageable());
        PageInfo<Node> res = PageInfo.of(nodePage);
        return res;

    }

    @Override
    public void updateLabel(LabelDTO labelDTO) {
        Node node = getNodeById(labelDTO.getId());
        if (node == null) {
            throw new ServiceException("节点不存在");
        }

        List<Label> oldLabels = node.getLabels();
        if (oldLabels == null) {
            oldLabels = new ArrayList<>();
        }
        Label label = labelDTO.getLabel();

        switch (labelDTO.getType()) {
            case 1:
            case 2:
                boolean exist = false;
                for (Label oldLabel : oldLabels) {
                    if (oldLabel.getKey().equals(label.getKey())) {
                        oldLabel.setValue(label.getValue());
                        exist = true;
                        break;
                    }
                }
                if (!exist) {
                    oldLabels.add(label);
                }
                break;
            case 3:
                oldLabels.removeIf(oldLabel -> oldLabel.getKey().equals(label.getKey()));
                break;
            default:
                throw new ServiceException("操作类型错误");
        }


        node.setLabels(oldLabels);
        node.setUpdateTime(new Date());
        nodeDao.save(node);
    }

    @Override
    public Node getNodeById(String id) {
        return nodeDao.findFirstById(id);
    }

    @Override
    public boolean geVersion(String ip){
        Node node = nodeDao.findFirstByIp(ip);
        if (node == null) {
            // throw new ServiceException("node not found");
            log.warn("node not found");
            return false;
        } else if (NodeConstants.OFFLINE.equals(node.getStatus())){
            return false;
        }
        return geVersion(node.getServer());
    }

    @Override
    public boolean geVersion(Server server) {
        String version = server.getVersion();
        if (version == null) {
            // throw new ServiceException("server info not found");
            // log.warn("version info not found");
            return false;
        }
        return version.compareTo(msMinVersion) >= 0;
    }

    private void updateNodeLabelsByServer(Node node, Server server) {
        List<Label> labels = node.getLabels();
        if (labels == null){
            labels = new ArrayList<>();
        }
        // 遍历Server类的每一个属性，属性名作为key，属性值作为value添加到labels中
        for (java.lang.reflect.Field field : server.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (field.get(server) == null) {
                    continue;
                }
                // 有则更新，无则添加
                boolean exist = false;
                for (Label label : labels) {
                    if (label.getKey().equals(field.getName())) {
                        label.setValue(field.get(server).toString());
                        exist = true;
                        break;
                    }
                }
                if (!exist) {
                    Label label = new Label();
                    label.setKey(field.getName());
                    label.setValue(field.get(server).toString());
                    label.setEditable(false);
                    labels.add(label);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        node.setLabels(labels);
    }

    // 节点下线时更新节点信息
    private void updateNodeWhenOffline(String ip) {
        Node node1 = getNodeByIp(ip);
        if (node1 != null) {
            node1.setStatus(NodeConstants.OFFLINE);
            node1.setOrder(NodeConstants.OFFLINE_ORDER);
            node1.setUpdateTime(new Date());
            nodeDao.save(node1);
        } else {
            Node node2 = new Node();
            node2.setIp(ip);
            node2.setStatus(NodeConstants.OFFLINE);
            node2.setOrder(NodeConstants.OFFLINE_ORDER);
            nodeDao.insert(node2);
        }
    }


    private Server formatServerInfo(JSONObject jsonObject) {

        Server server = new Server();
        server.setHostname(jsonObject.getString("hostname"));
        server.setSystemType(jsonObject.getString("systemtype"));
        server.setPlatform(jsonObject.getString("platform"));
        server.setRelease(jsonObject.getString("release"));
        long totalMem = jsonObject.getLong("totalmem");
        long freeMem = jsonObject.getLong("freemem");
        server.setTotalMem(FileUtils.calcSize(totalMem));
        server.setFreeMem(FileUtils.calcSize(freeMem));
        server.setUsageMem((int) ((totalMem - freeMem) * 100 / totalMem));
        server.setCpuNum(jsonObject.getJSONArray("cpus").size());
        if (jsonObject.containsKey("cpuusage")) {
            server.setCpuUsage(jsonObject.getDouble("cpuusage"));
        }
        if (jsonObject.containsKey("version")) {
            server.setVersion(jsonObject.getString("version"));
        }
        JSONArray disk = jsonObject.getJSONArray("disk");
        server.setProcessDisk(disk.getString(1));
        server.setUsageDisk(disk.getInteger(0));
        JSONArray diskSize = jsonObject.getJSONArray("disksize");
        server.setTotalDisk(diskSize.getInteger(1) + "GB");
        server.setFreeDisk(diskSize.getInteger(0) + "GB");
        return server;
    }

}
