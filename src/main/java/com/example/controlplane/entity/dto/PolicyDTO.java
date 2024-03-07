package com.example.controlplane.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 容错策略DTO
 *
 * @author 7bin
 * @date 2024/03/01
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PolicyDTO {


    /**
     * 必选，模型名称
     */
    // @NotBlank(message = "模型名称不能为空")
    String modelName;

    /**
     * 可选，模型部署包文件（file和md5两者必须有其一）
     */
    MultipartFile file;

    /**
     * 可选，模型md5（file和md5两者必须有其一）
     */
    String modelMd5;



    // ========= 容错策略 =========
    /**
     * 可选，容错策略id
     */
    String policyId;

    /**
     * 容错策略名称
     */
    String policyName;


    /**
     * 容错策略模式
     * @see com.example.controlplane.constant.PolicyMode
     */
    // @NotBlank(message = "容错模式不能为空")
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
