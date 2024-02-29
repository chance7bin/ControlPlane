package com.example.controlplane.service;

import com.example.controlplane.entity.dto.DeployDTO;
import com.example.controlplane.entity.dto.FindDTO;
import com.example.controlplane.entity.dto.MigrateDTO;
import com.example.controlplane.entity.dto.PortalResponse;

/**
 * 模型接口
 *
 * @author 7bin
 * @date 2024/02/27
 */
public interface IModelService {

    PortalResponse getModelList(FindDTO findDTO);

    void deployModel(DeployDTO deployDTO);

    void migrateModel(MigrateDTO migrateDTO);

}
