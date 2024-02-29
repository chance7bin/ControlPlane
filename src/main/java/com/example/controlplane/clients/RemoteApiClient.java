package com.example.controlplane.clients;

import com.example.controlplane.entity.dto.ApiResponse;
import com.example.controlplane.entity.dto.FindDTO;
import com.example.controlplane.entity.dto.PortalResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 7bin
 * @date 2024/02/26
 */
@FeignClient(name = "remote-api", url = "dynamic-url")
public interface RemoteApiClient {

    // ========== 模型服务容器接口 ==========
    @GetMapping("/json/status")
    String getRemoteNodeStatus();

    @PostMapping(value = "/modelser", headers = "content-type=" + MediaType.MULTIPART_FORM_DATA_VALUE)
    String deployModel(@RequestPart("file_model") MultipartFile file);

    @GetMapping("/modelser/info/{pid}")
    String getModelByPid(@PathVariable("pid") String pid);

    @GetMapping("/ping")
    String ping();


    // ========== 容器交互引擎接口 ==========
    @GetMapping("/monitor/server/info")
    ApiResponse getEngineServerInfo();



    // ========== 门户接口 ==========
    @PostMapping("/managementSystem/deployedModel")
    PortalResponse getDeployedModel(@RequestBody FindDTO findDTO);




}
