package com.ikonke.api.model;

import java.util.List;

import lombok.Data;

@Data
public class PageResp<T> {

    private int pageSize;
    private int pageNo;
    private int total;
    private List<T> data;

}
