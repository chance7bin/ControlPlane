package com.example.controlplane.dao;

import com.example.controlplane.entity.po.Node;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author 7bin
 * @date 2024/02/27
 */
public interface NodeDao extends MongoRepository<Node, String> {

    Node findFirstByIp(String ip);

    Node findFirstById(String id);



    List<Node> findAllByStatus(String status);

    List<Node> findAllByStatus(String status, Pageable pageable);
}
