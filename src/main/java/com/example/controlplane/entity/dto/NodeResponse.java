package com.example.controlplane.entity.dto;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.util.Objects;

/**
 * 节点DTO
 *
 * @author 7bin
 * @date 2024/02/29
 */
@Data
public class NodeResponse {

    String result;

    Integer code;

    JSONObject data;

    private final static Integer SUC_CODE = 1;

    public static NodeResponse parse(JSONObject rsp){
        return parse(rsp.toJSONString());
    }

    public static NodeResponse parse(String rspStr){
        return JSON.parseObject(rspStr, NodeResponse.class);
    }

    public static boolean isSuccess(NodeResponse rsp){
        return rsp != null && Objects.equals(rsp.getCode(), SUC_CODE);
    }
}
