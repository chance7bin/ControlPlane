package com.example.controlplane.controller;

import com.example.controlplane.entity.dto.ApiResponse;
import com.example.controlplane.entity.dto.FindDTO;
import com.example.controlplane.entity.dto.page.PageInfo;
import com.example.controlplane.entity.po.Node;
import com.example.controlplane.service.INodeService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 计算资源接口
 *
 * @author 7bin
 * @date 2024/02/26
 */
@Slf4j
@RestController
@RequestMapping("/node")
public class NodeController {

    @Autowired
    INodeService nodeService;

    @ApiOperation("获取任务服务器节点信息")
    @GetMapping("/taskNodeList")
    public ApiResponse getTaskNodeList() {
        return ApiResponse.success(nodeService.getTaskNodeList());
    }

    @ApiOperation("获取节点状态")
    @GetMapping("/status")
    public ApiResponse getStatus(@RequestParam("ip") String ip) {
        Node node = nodeService.getNodeByIp(ip);
        return ApiResponse.success(node);
    }

    @ApiOperation("获取节点列表")
    @PostMapping("/list")
    public ApiResponse getNodeList(@RequestBody FindDTO findDTO) {

        PageInfo<Node> res = nodeService.getNodeList(findDTO);

        return ApiResponse.success(res);
    }


}
