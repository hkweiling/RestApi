package com.ikonke.api.model;

import java.util.List;

import com.ikonke.api.model.ChannelDevice;
import lombok.Data;

@Data
public class ZigbeeDevice {

    private String mac;
    private String gwMac;
    private String version;
    private String onlineState;
    private int productId;
    private List<ChannelDevice> channelDevices;

}
