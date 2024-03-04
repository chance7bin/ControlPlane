package com.example.controlplane.dao;

import com.example.controlplane.entity.po.HaRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author 7bin
 * @date 2024/03/04
 */
public interface HaRecordDao extends MongoRepository<HaRecord, String> {
}
