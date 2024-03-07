package com.example.controlplane.entity.po;

import com.example.controlplane.entity.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * @author 7bin
 * @date 2024/02/27
 */
@Data
public class Model extends BaseEntity {

    String name;

    String md5;

    /**
     * 关联策略id
     */
    String policyId;

    /**
     * 关联策略(lookup 查出来的，数据库不记录)
     */
    Policy policy;

    /**
     * 部署节点
     */
    List<String> deployedNodes;

}
