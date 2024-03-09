package com.example.controlplane.dao;

import com.example.controlplane.entity.po.DeployInfo;
import com.example.controlplane.entity.po.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * 使用mongoTemplate的dao（MongoRepository不支持只更新记录中的某些属性）
 *
 * @author 7bin
 * @date 2024/02/26
 */
@Repository
public class TemplateDao {

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


    public Model getModelByMd5(String md5) {
        Criteria criteria = Criteria.where("md5").is(md5);
        LookupOperation lookup = Aggregation.lookup("policy", "policyId", "_id", "policy");
        MatchOperation match = Aggregation.match(criteria);
        Aggregation aggregation = Aggregation.newAggregation(lookup, match, Aggregation.unwind("policy"));
        Model model = mongoTemplate.aggregate(aggregation, "model", Model.class).getUniqueMappedResult();
        return model;
    }


}
