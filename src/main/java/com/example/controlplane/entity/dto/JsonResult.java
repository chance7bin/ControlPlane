package com.example.controlplane.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 接口返回格式
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JsonResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer code=0;
    private String msg="success";
    private T data;
}
