package com.example.controlplane.controller;

import com.example.controlplane.entity.dto.*;
import com.example.controlplane.service.IModelService;
import com.example.controlplane.utils.file.FileUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * 模型资源接口
 *
 * @author 7bin
 * @date 2024/02/26
 */
@Slf4j
@RestController
@RequestMapping("/model")
public class ModelController {


    @Autowired
    IModelService modelService;

    @ApiOperation("获取模型列表")
    @PostMapping("/list")
    public ApiResponse getModelList(@RequestBody FindDTO findDTO) {
        PortalResponse res = modelService.getModelList(findDTO);
        if (res.getCode() != 0) {
            return ApiResponse.error(res.getMsg());
        }
        return ApiResponse.success(res.getData());
    }

    @ApiOperation("部署模型")
    @PostMapping("/deploy")
    public ApiResponse deployModel(@RequestBody DeployDTO deployDTO) {
        MultipartFile file = FileUtils.file2MultipartFile(new File("E:\\ModelServiceContainer\\createWordCloud.zip"));
        deployDTO.setFile(file);
        modelService.deployModel(deployDTO);
        return ApiResponse.success();
    }

    @ApiOperation("迁移模型")
    @PostMapping("/migrate")
    public ApiResponse migrateModel(@RequestBody MigrateDTO migrateDTO) {
        modelService.migrateModel(migrateDTO);
        return ApiResponse.success();
    }

}
