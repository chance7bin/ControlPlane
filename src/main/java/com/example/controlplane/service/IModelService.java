package com.example.controlplane.service;

import com.example.controlplane.entity.bo.envconfg.ModelEnv;
import com.example.controlplane.entity.dto.*;
import com.example.controlplane.entity.dto.page.PageInfo;
import com.example.controlplane.entity.po.DeployInfo;
import com.example.controlplane.entity.po.HaRecord;
import com.example.controlplane.entity.po.Model;
import com.example.controlplane.entity.po.Node;

import java.util.List;

/**
 * 模型接口
 *
 * @author 7bin
 * @date 2024/02/27
 */
public interface IModelService {

    PortalResponse getModelList(FindDTO findDTO);

    /**
     * 部署模型
     *
     * @param deployDTO
     * @return 部署记录id
     */
    List<String> deployModel(DeployDTO deployDTO);

    void migrateModel(MigrateDTO migrateDTO);

    ModelEnv getModelEnvConfig(String pid);

    List<Node> getAvailableNodes(String pid);

    void configHa(PolicyDTO policyDTO);

    PageInfo<DeployInfo> getDeployList(FindDTO findDTO);

    PageInfo<HaRecord> getHaRecordList(FindDTO findDTO);

    PageInfo<Model> getHaModelList(FindDTO findDTO);
}
