package com.example.controlplane.entity.dto.page;

import org.springframework.data.domain.Page;

/**
 * @author 7bin
 * @date 2024/02/27
 */
public class PageInfo<T> extends PageSerializable<T> {

    public PageInfo(Page<? extends T> page) {
        super(page);
    }

    public static <T> PageInfo<T> of(Page<? extends T> page) {
        return new PageInfo<>(page);
    }

}
