package com.example.controlplane.entity.po;

import com.example.controlplane.entity.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * 容错规则
 *
 * @author 7bin
 * @date 2024/03/03
 */
@Data
public class Policy extends BaseEntity {

    /**
     * 容错策略名称
     */
    String policyName;


    /**
     * 容错策略模式
     * @see com.example.controlplane.constant.PolicyMode
     */
    String haMode;

    /**
     * 可选，容错节点数量，当容错模式为exactly时必选
     */
    Integer count;

    /**
     * 可选，容错目标节点，当容错模式为nodes时必选
     */
    List<String> targetIp;


}
