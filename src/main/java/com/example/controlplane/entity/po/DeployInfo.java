package com.example.controlplane.entity.po;

import com.example.controlplane.constant.DeployStatus;
import com.example.controlplane.entity.BaseEntity;
import lombok.Data;

/**
 * 模型部署信息
 *
 * @author 7bin
 * @date 2024/02/29
 */
@Data
public class DeployInfo extends BaseEntity {

    /**
     * 模型容器中的ID
     */
    String modelId;

    /**
     * 模型容器中的模型名称
     */
    String modelName;

    String modelMd5;

    String targetIp;

    String status = DeployStatus.INIT;

    String msg;

}
