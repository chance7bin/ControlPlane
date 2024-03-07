package com.example.controlplane.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 模型迁移DTO
 *
 * @author 7bin
 * @date 2024/02/29
 */
@Data
public class MigrateDTO {

    /**
     * 必选，门户中计算模型ID
     */
    // String modelId;


    /**
     * 必选，模型名称
     */
    @NotBlank(message = "模型名称不能为空")
    String modelName;

    /**
     * 必选，模型md5
     */
    @NotBlank(message = "模型md5不能为空")
    String modelMd5;

    /**
     * 必选，迁移目标节点
     */
    @NotNull(message = "迁移目标节点不能为空")
    List<String> targetIp;

    /**
     * 必选，服务部署节点ip列表
     */
    // @NotNull(message = "服务部署节点ip列表不能为空")
    List<String> deployedMSR;

}
