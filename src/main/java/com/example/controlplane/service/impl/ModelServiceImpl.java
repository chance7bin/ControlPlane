package com.example.controlplane.service.impl;

import com.example.controlplane.clients.PortalClient;
import com.example.controlplane.entity.dto.FindDTO;
import com.example.controlplane.entity.dto.JsonResult;
import com.example.controlplane.service.IModelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 7bin
 * @date 2024/02/27
 */
@Slf4j
@Service
public class ModelServiceImpl implements IModelService {


    @Autowired
    PortalClient portalClient;

    @Override
    public JsonResult getModelList(FindDTO findDTO) {
        return portalClient.getDeployModel(findDTO);
    }
}
