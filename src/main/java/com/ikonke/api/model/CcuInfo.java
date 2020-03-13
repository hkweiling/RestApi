package com.ikonke.api.model;

import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author:
 * @time: 2020/3/12 15:52
 */
@Data
public class CcuInfo {
    private String localIp;
    private String curVersion;
    private String downloadVersion;
    private List<Gw> gW;
}
