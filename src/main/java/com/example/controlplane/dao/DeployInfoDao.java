package com.example.controlplane.dao;

import com.example.controlplane.entity.po.DeployInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author 7bin
 * @date 2024/02/29
 */
public interface DeployInfoDao extends MongoRepository<DeployInfo, String> {

    DeployInfo findFirstById(String id);

}
