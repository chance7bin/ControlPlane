package com.example.controlplane.controller;

import com.example.controlplane.entity.dto.ApiResponse;
import com.example.controlplane.entity.dto.FindDTO;
import com.example.controlplane.entity.dto.LabelDTO;
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



    @ApiOperation("根据ip获取节点信息")
    @GetMapping("/info/ip/{ip}")
    public ApiResponse getNodeInfoByIp(@PathVariable("ip") String ip) {
        Node node = nodeService.getNodeByIp(ip);
        if (node == null) {
            return ApiResponse.error("节点不存在");
        }
        return ApiResponse.success(node);
    }

    @ApiOperation("根据id获取节点信息")
    @GetMapping("/info/id/{id}")
    public ApiResponse getNodeInfoById(@PathVariable("id") String id) {
        Node node = nodeService.getNodeById(id);
        if (node == null) {
            return ApiResponse.error("节点不存在");
        }
        return ApiResponse.success(node);
    }

    @ApiOperation("获取节点列表")
    @PostMapping("/list")
    public ApiResponse getNodeList(@RequestBody FindDTO findDTO) {

        PageInfo<Node> res = nodeService.getNodeList(findDTO);

        return ApiResponse.success(res);
    }


    @ApiOperation("修改节点的标签信息")
    @PostMapping("/label/update")
    public ApiResponse updateLabel(@RequestBody LabelDTO labelDTO) {
        nodeService.updateLabel(labelDTO);
        return ApiResponse.success();
    }


    @ApiOperation("根据部署包md5获取可用的节点信息")
    @GetMapping("/available/{md5}")
    public ApiResponse getAvailableNode(@PathVariable("md5") String md5) {
        // return ApiResponse.success(nodeService.getAvailableNode(md5));
        return null;
    }

    @ApiOperation("刷新节点信息")
    @GetMapping("/refresh")
    public ApiResponse refreshNodeList() {
        nodeService.updateRemoteNode();
        return ApiResponse.success();
    }

}
