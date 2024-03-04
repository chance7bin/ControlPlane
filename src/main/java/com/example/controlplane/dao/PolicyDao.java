package com.example.controlplane.dao;

import com.example.controlplane.entity.po.Policy;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author 7bin
 * @date 2024/03/03
 */
public interface PolicyDao extends MongoRepository<Policy, String> {

    Policy findFirstById(String id);

}
