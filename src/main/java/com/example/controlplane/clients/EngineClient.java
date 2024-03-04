package com.example.controlplane.clients;

import com.example.controlplane.entity.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author 7bin
 * @date 2024/02/28
 */
@Slf4j
@Component
public class EngineClient {

    @Value("${enginePort}")
    private String enginePort;

    @Autowired
    private RemoteApiClient remoteApiClient;

    public boolean checkDocker(String ip){

        String dynamicUrl = "http://" + ip + ":" + enginePort;
        try {
            // 设置 FeignClient 的 url 属性为动态获取的 IP 地址
            DynamicUrlInterceptor.setDynamicUrl(dynamicUrl);
            // 调用远程接口
            ApiResponse rspStr = remoteApiClient.checkDocker();
            return ApiResponse.reqSuccess(rspStr);
        } catch (Exception e) {
            // throw new ServiceException("docker检测失败: " + e.getMessage());
            return false;
        }

    }

}
