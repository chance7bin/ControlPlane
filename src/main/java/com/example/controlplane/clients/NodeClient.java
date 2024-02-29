package com.example.controlplane.clients;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;
import com.example.controlplane.entity.dto.NodeResponse;
import com.example.controlplane.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * 计算节点远程调用
 *
 * @author 7bin
 * @date 2024/02/27
 */
@Slf4j
@Component
public class NodeClient {

    @Autowired
    private RemoteApiClient remoteApiClient;

    /**
     * 获取远程节点状态
     *
     * @param ip   计算节点 IP
     * @param port 计算节点端口
     * @return 节点状态
     */
    public JSONObject getRemoteNodeStatus(String ip, String port) {

        String dynamicUrl = "http://" + ip + ":" + port;
        // 设置 FeignClient 的 url 属性为动态获取的 IP 地址
        JSONObject rsp = null;
        try {
            DynamicUrlInterceptor.setDynamicUrl(dynamicUrl);
            // 调用远程接口
            String rspStr = remoteApiClient.getRemoteNodeStatus();
            // string 转 JSONObject
            rsp = JSONObject.parseObject(rspStr);
            // DynamicUrlInterceptor.clearDynamicUrl();
        } catch (Exception e) {
            // log.error(e.toString());
            throw new ServiceException("获取远程节点信息异常: " + e.getMessage());
        }

        // 使用 HttpUtils 发送 GET 请求
        // String api = "/monitor/server/info";
        // String requestUrl = dynamicUrl + api;
        // try {
        //     String rspStr = HttpUtils.sendGet(requestUrl);
        //     rsp = ApiResponse.parseObject(rspStr);
        // } catch (Exception e) {
        //     log.error(e.toString());
        //     throw new ServiceException("调用远程接口连接超时");
        // }
        // if (!ApiResponse.reqSuccess(rsp)) {
        //     throw new ServiceException("请求失败");
        // }
        // HashMap<String, Object> data = ApiResponse.getRspData(rsp);


        return rsp;
    }

    /**
     * 部署模型
     *
     * @param ip   计算节点 IP
     * @param port 计算节点端口
     * @param file 模型部署包
     * @return 部署结果
     */
    public NodeResponse deployModel(String ip, String port, MultipartFile file) {
        String dynamicUrl = "http://" + ip + ":" + port;

        NodeResponse rsp = null;
        try {
            DynamicUrlInterceptor.setDynamicUrl(dynamicUrl);
            String rspStr = remoteApiClient.deployModel(file);
            rsp = NodeResponse.parse(rspStr);
        } catch (Exception e) {
            // log.error(e.toString());
            throw new ServiceException("部署模型异常: " + e.getMessage());
        }

        return rsp;
    }


    public NodeResponse getModelServiceInfoByPid(String ip, String port, String pid) {
        String dynamicUrl = "http://" + ip + ":" + port;
        NodeResponse rsp = null;
        try {
            DynamicUrlInterceptor.setDynamicUrl(dynamicUrl);
            String rspStr = remoteApiClient.getModelByPid(pid);
            rsp = NodeResponse.parse(rspStr);
        } catch (Exception e) {
            throw new ServiceException("获取模型服务信息异常: " + e.getMessage());
        }

        return rsp;
    }

    public void ping(String ip, String port) {
        String dynamicUrl = "http://" + ip + ":" + port;
        try {
            DynamicUrlInterceptor.setDynamicUrl(dynamicUrl);
            remoteApiClient.ping();
        } catch (Exception e) {
            throw new ServiceException("ping 节点异常: " + e.getMessage());
        }
    }


    /**
     * 下载模型部署包
     *
     * @param ip   计算节点 IP
     * @param port 计算节点端口
     * @param pid  模型部署包 ID
     * @param dest 下载文件保存路径
     */
    public void downloadPackage(String ip, String port, String pid, String dest) {
        String downloadUrl = "http://" + ip + ":" + port + "/modelser/downloadPackage/" + pid;
        HttpUtil.downloadFile(downloadUrl, dest);
    }

}
