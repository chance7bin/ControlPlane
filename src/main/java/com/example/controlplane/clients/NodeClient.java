package com.example.controlplane.clients;

import com.alibaba.fastjson2.JSONObject;
import com.example.controlplane.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
            log.error(e.toString());
            throw new ServiceException("调用远程接口发生异常");
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

}
