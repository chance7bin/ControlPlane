package com.example.controlplane.entity.dto;

import com.example.controlplane.entity.bo.Label;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 更新节点label的DTO
 *
 * @author 7bin
 * @date 2024/02/28
 */
@Data
public class LabelDTO {

    /**
     * 节点id
     */
    @ApiModelProperty(value = "节点id")
    String id;

    /**
     * 节点标签
     */
    @ApiModelProperty(value = "节点标签")
    Label label;

    /**
     * 操作类型
     * 1:新增
     * 2:修改
     * 3:删除
     */
    @ApiModelProperty(value = "操作类型 1:新增 2:修改 3:删除")
    Integer type;


}
