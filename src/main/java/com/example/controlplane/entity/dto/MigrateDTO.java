package com.example.controlplane.entity.dto;

import lombok.Data;

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
    String modelId;

    /**
     * 必选，模型md5
     */
    String modelMd5;

    /**
     * 必选，迁移目标节点
     */
    List<String> targetIp;

    /**
     * 必选，服务部署节点ip列表
     */
    List<String> deployedMSR;

}
