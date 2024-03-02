package com.example.controlplane.entity.dto;

import java.util.List;

/**
 * 容错策略DTO
 *
 * @author 7bin
 * @date 2024/03/01
 */
public class PolicyDTO {


    String policyName;

    String haMode;

    Integer count;

    List<String> targetIp;


}
