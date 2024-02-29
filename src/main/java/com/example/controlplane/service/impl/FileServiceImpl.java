package com.example.controlplane.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.example.controlplane.constant.FileConstants;
import com.example.controlplane.dao.FileInfoDao;
import com.example.controlplane.entity.dto.FileDTO;
import com.example.controlplane.entity.po.FileInfo;
import com.example.controlplane.exception.ServiceException;
import com.example.controlplane.service.IFileService;
import com.example.controlplane.utils.StringUtils;
import com.example.controlplane.utils.file.FileTypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * @author 7bin
 * @date 2024/02/29
 */
@Slf4j
@Service
public class FileServiceImpl implements IFileService {

    @Value("${file.save-path}")
    private String savePath;

    @Autowired
    FileInfoDao fileInfoDao;

    @Override
    public String uploadFiles(FileDTO fileDTO) {
        String path = savePath + fileDTO.getPath();
        File file = new File(path);

        // 保存文件信息
        FileInfo fileInfo = new FileInfo();
        // 获取文件名
        String fileName = fileDTO.getFile().getOriginalFilename();
        fileInfo.setFileName(fileName);
        // 如果md5值为空则计算md5值
        String md5 = StringUtils.isEmpty(fileDTO.getMd5()) ? SecureUtil.md5(file) : fileDTO.getMd5();
        fileInfo.setMd5(md5);
        // fileInfo.setSize(String.valueOf(FileUtil.size(file)));
        fileInfo.setSize(String.valueOf(fileDTO.getFile().getSize()));
        fileInfo.setSuffix(FileTypeUtils.getFileType(fileName));

        // 判断文件是否已存在
        FileInfo f = fileInfoDao.findFirstByMd5(md5);
        if (f != null) {
            fileInfo.setFilePath(f.getFilePath());
        } else {

            // 判断文件是否允许覆盖
            if (file.exists() && FileConstants.UNCOVER.equals(fileDTO.getCover())) {
                throw new ServiceException("file already exists");
            }

            // 如果该存储路径不存在则新建存储路径
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            // 文件写入
            try {
                fileDTO.getFile().transferTo(file);
            } catch (IOException e) {
                // log.error("文件写入异常");
                throw new ServiceException("文件写入异常");
            }

            // 根据savePath截取文件路径
            String filePath = path.substring(savePath.length());
            fileInfo.setFilePath(filePath);
        }

        fileInfoDao.insert(fileInfo);
        return fileInfo.getId();

    }

    @Override
    public FileInfo getFileById(String fileId) {
        return fileInfoDao.findFirstById(fileId);
    }

    @Override
    public FileInfo getFileByMd5(String md5) {
        return fileInfoDao.findFirstByMd5(md5);
    }


}
