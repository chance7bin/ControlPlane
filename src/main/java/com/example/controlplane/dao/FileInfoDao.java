package com.example.controlplane.dao;


import com.example.controlplane.entity.po.FileInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author 7bin
 * @date 2023/12/06
 */
public interface FileInfoDao extends MongoRepository<FileInfo, String> {

    FileInfo findFirstByMd5(String md5);

    FileInfo findFirstById(String id);

}
