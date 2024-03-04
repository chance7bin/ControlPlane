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

    // ================ 模型服务容器接口 ================
    /**
     * 获取远程节点状态
     *
     * @return 节点状态
     */
    @GetMapping("/json/status")
    String getRemoteNodeStatus();

    /**
     * 部署模型
     *
     * @param file 模型文件
     * @return 部署结果
     */
    @PostMapping(value = "/modelser", headers = "content-type=" + MediaType.MULTIPART_FORM_DATA_VALUE)
    String deployModel(@RequestPart("file_model") MultipartFile file);

    /**
     * 根据模型部署包md5获取模型信息
     *
     * @param pid 模型部署包md5
     * @return 模型信息
     */
    @GetMapping("/modelser/md5/{pid}")
    String getModelByPid(@PathVariable("pid") String pid);

    // 根据模型msid获取模型信息
    @GetMapping("/modelser/json/{msid}")
    String getModelByMsid(@PathVariable("msid") String msid);


    /**
     * 判断模型是否已部署
     *
     * @return 模型列表
     */
    @GetMapping("/modelser/check/{pid}")
    String checkDeployed(@PathVariable("pid") String pid);

    /**
     * 测试节点是否在线
     *
     * @return 节点状态
     */
    @GetMapping("/ping")
    String pingNode();


    /**
     * 更新模型信息
     *
     * @param msid 模型服务id
     * @param ac  操作 stop: 停止服务 start: 开启服务
     * @return 更新结果
     */
    @PutMapping("/modelser/{msid}")
    String updateModelService(@PathVariable("msid") String msid, @RequestParam("ac") String ac);




    // ================ 容器交互引擎接口 ================

    /**
     * 获取容器交互引擎服务器信息
     *
     * @return 服务器信息
     */
    @GetMapping("/monitor/server/info")
    ApiResponse getEngineServerInfo();

    /**
     * 检查容器交互引擎服务器的docker连接状态
     *
     * @return 服务器状态
     */
    @GetMapping("/monitor/server/docker/check")
    ApiResponse checkDocker();


    // ================ 门户接口 ================

    /**
     * 获取门户中的模型列表
     *
     * @param findDTO 查询条件
     * @return 模型列表
     */
    @PostMapping("/managementSystem/deployedModel")
    PortalResponse getDeployedModel(@RequestBody FindDTO findDTO);


    // ========== 任务服务器接口 ==========

    // 获取部署了指定模型的节点列表
    @GetMapping("/server/modelser/{pid}")
    String getDeployedNodeByPid(@PathVariable("pid") String pid);

}
