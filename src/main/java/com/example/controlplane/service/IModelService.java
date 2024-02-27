package com.example.controlplane.service;

import com.example.controlplane.entity.dto.FindDTO;
import com.example.controlplane.entity.dto.JsonResult;

/**
 * 模型接口
 *
 * @author 7bin
 * @date 2024/02/27
 */
public interface IModelService {

    JsonResult getModelList(FindDTO findDTO);

}
