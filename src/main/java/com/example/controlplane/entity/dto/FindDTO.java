package com.example.controlplane.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;

/**
 * @Author bin
 * @Date 2021/09/02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FindDTO implements Serializable {
    @ApiModelProperty(value = "当前页数，页数从1开始", example = "1")
    private Integer page = 1; //当前页数
    @ApiModelProperty(value = "每页数量", example = "10")
    private Integer pageSize = 10; //每页数量
    @ApiModelProperty(value = "是否顺序，从小到大", example = "false")
    private Boolean asc = false; //是否顺序，从小到大
    @ApiModelProperty(value = "查询内容", example = "")
    private String searchText = ""; //查询内容
    @ApiModelProperty(value = "排序字段", example = "createTime")
    private String sortField = "createTime"; //排序字段

    /**
     * 获取Pageable
     **/
    public Pageable getPageable(){
        return PageRequest.of(page - 1, pageSize, Sort.by(asc ? Sort.Direction.ASC : Sort.Direction.DESC, sortField));
    }

}
