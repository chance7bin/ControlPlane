package com.example.controlplane.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 模型部署DTO
 *
 * @author 7bin
 * @date 2024/02/28
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DeployDTO {


    /**
     * 必选，部署模型的容器ID
     */
    List<String> targetIp;

    /**
     * 必选，模型部署包文件
     */
    MultipartFile file;

    /**
     * 非必选，模型部署包文件md5
     */
    String md5;

}
