package com.example.controlplane.controller;

import com.example.controlplane.entity.dto.ApiResponse;
import com.example.controlplane.entity.dto.FindDTO;
import com.example.controlplane.entity.dto.JsonResult;
import com.example.controlplane.service.IModelService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        JsonResult res = modelService.getModelList(findDTO);
        if (res.getCode() != 0) {
            return ApiResponse.error(res.getMsg());
        }
        return ApiResponse.success(res.getData());
    }

}
