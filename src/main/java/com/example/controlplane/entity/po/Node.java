package com.example.controlplane.entity.po;

import com.example.controlplane.entity.BaseEntity;
import com.example.controlplane.entity.bo.Label;
import com.example.controlplane.entity.bo.Server;
import lombok.Data;

import java.util.List;

/**
 * 节点信息
 *
 * @author 7bin
 * @date 2024/02/27
 */
@Data
public class Node extends BaseEntity {

    String ip;

    String status;

    /**
     * 节点列表排序规则
     */
    int order;

    Server server;

    List<Label> labels;

    /**
     * 优先级函数计算后的节点得分
     */
    int score;

}
