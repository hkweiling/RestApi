package com.ikonke.api.model;

import lombok.Data;

@Data
public class CcuItem {
    private String id;
    private String productId;
    private String deviceId;
    private String nickName;
    private boolean online;
}
