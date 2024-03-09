package com.example.controlplane.entity.po;

import com.example.controlplane.entity.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * 容错记录
 *
 * @author 7bin
 * @date 2024/03/04
 */
@Data
public class HaRecord extends BaseEntity {

    String modelId;

    // 用modelId关联
    // Model model;
    String modelName;

    List<String> originIp;

    List<String> targetIp;

    List<String> deployId;

}
