package com.ikonke.api.model;

import lombok.Data;

/**
 * @description:
 * @author:
 * @time: 2020/3/12 15:56
 */
@Data
public class Gw {
    private int gwId;
    private String gwName;
    private String gwMac;
    private String gwIp;
    private Boolean gwOnline;
    private int gwType;
    private String gwCurVersion;
    private String gwDownloadVersion;
}
