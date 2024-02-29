package com.example.controlplane.entity.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 标签
 *
 * @author 7bin
 * @date 2024/02/27
 */
@Data
public class Label {

    String key;

    String value;

    /**
     * 是否可编辑
     */
    @ApiModelProperty(value = "是否可编辑（非空且为False才不可编辑）")
    Boolean editable;

}
