package com.example.controlplane.clients;

import com.example.controlplane.entity.dto.FindDTO;
import com.example.controlplane.entity.dto.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 门户接口远程调用
 *
 * @author 7bin
 * @date 2024/02/27
 */
@Component
public class PortalClient {

    @Value("${portalUrl}")
    private String portalUrl;

    @Autowired
    RemoteApiClient remoteApiClient;

    public JsonResult getDeployModel(FindDTO findDTO) {
        DynamicUrlInterceptor.setDynamicUrl(portalUrl);
        JsonResult list = remoteApiClient.getDeployedModel(findDTO);
        return list;
    }

}
