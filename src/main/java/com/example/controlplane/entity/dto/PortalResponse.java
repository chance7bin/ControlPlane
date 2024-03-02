package com.example.controlplane.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * 门户接口返回格式
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PortalResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer code=0;
    private String msg="success";
    private T data;

    private final static Integer SUC_CODE = 0;

    public static boolean isSuccess(PortalResponse rsp){
        return rsp != null && Objects.equals(rsp.getCode(), SUC_CODE);
    }


}
