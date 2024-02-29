package com.example.controlplane.entity.dto;

import com.example.controlplane.constant.FileConstants;
import com.example.controlplane.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件传输DTO
 *
 * @author 7bin
 * @date 2024/02/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDTO extends BaseEntity {

    /**
     * 必选，文件相对路径
     */
    String path;

    /**
     * 非必选；参数值：cover:强制覆盖，uncover:不覆盖，默认：cover
     */
    String cover = FileConstants.COVER;


    /**
     * 必选，文件对象
     */
    MultipartFile file;

    /**
     * 非必选，md5
     */
    String md5;



}
