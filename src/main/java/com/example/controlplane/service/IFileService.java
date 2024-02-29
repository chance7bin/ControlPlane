package com.example.controlplane.service;

import com.example.controlplane.entity.dto.FileDTO;
import com.example.controlplane.entity.po.FileInfo;

/**
 * @author 7bin
 * @date 2024/02/28
 */
public interface IFileService {

    /**
     * 小文件上传
     *
     * @param fileDTO 文件传输对象
     * @return {@link String} 上传后返回的fileId
     */
    String uploadFiles(FileDTO fileDTO);


    /**
     * 根据文件ID获取文件信息
     *
     * @param fileId 文件ID
     * @return {@link FileInfo} 文件信息
     */
    FileInfo getFileById(String fileId);

    /**
     * 根据md5获取文件信息
     *
     * @param md5 文件md5
     * @return {@link FileInfo} 文件信息
     */
    FileInfo getFileByMd5(String md5);

}
