package com.example.controlplane.dao;

import com.example.controlplane.entity.po.Model;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author 7bin
 * @date 2024/03/04
 */
public interface ModelDao extends MongoRepository<Model, String> {

    Model findFirstById(String id);

    Model findFirstByMd5(String md5);

}
