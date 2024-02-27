package com.example.controlplane.clients;

import com.example.controlplane.entity.dto.ApiResponse;
import com.example.controlplane.entity.dto.FindDTO;
import com.example.controlplane.entity.dto.JsonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 7bin
 * @date 2024/02/26
 */
@FeignClient(name = "remote-api", url = "dynamic-url")
public interface RemoteApiClient {

    // ========== 模型服务容器接口 ==========
    @GetMapping("/json/status")
    String getRemoteNodeStatus();



    // ========== 容器交互引擎接口 ==========
    @GetMapping("/monitor/server/info")
    ApiResponse getServerInfo();



    // ========== 门户接口 ==========
    @PostMapping("/managementSystem/deployedModel")
    JsonResult getDeployedModel(@RequestBody FindDTO findDTO);

}
