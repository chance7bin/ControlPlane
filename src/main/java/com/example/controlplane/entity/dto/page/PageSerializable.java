package com.example.controlplane.entity.dto.page;

import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

/**
 * @author 7bin
 * @date 2024/02/27
 */
public class PageSerializable<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    //总记录数
    protected long    total;
    //结果集
    protected List<T> list;

    public PageSerializable() {
    }

    @SuppressWarnings("unchecked")
    public PageSerializable(Page<? extends T> page) {
        this.list = (List<T>) page.getContent();
        this.total = page.getTotalElements();
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "PageSerializable{" +
            "total=" + total +
            ", list=" + list +
            '}';
    }
}
