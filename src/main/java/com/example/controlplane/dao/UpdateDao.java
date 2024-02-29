package com.example.controlplane.dao;

import com.example.controlplane.entity.po.DeployInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * 用于更新字段的dao（MongoRepository不支持只更新记录中的某些属性）
 *
 * @author 7bin
 * @date 2024/02/26
 */
@Repository
public class UpdateDao {

    @Autowired
    MongoTemplate mongoTemplate;

    public void updatedDeployStatusById(String id, String status) {
        Query query = new Query(Criteria.where("id").is(id));
        Update update = new Update()
            .set("status", status)
            .set("updateTime", new Date());
        mongoTemplate.updateFirst(query, update, DeployInfo.class);
    }

    public void updatedDeployStatusAndMsgById(String id, String status, String msg) {
        Query query = new Query(Criteria.where("id").is(id));
        Update update = new Update()
            .set("status", status)
            .set("msg", msg)
            .set("updateTime", new Date());
        mongoTemplate.updateFirst(query, update, DeployInfo.class);
    }

}
