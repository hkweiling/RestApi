package com.ikonke.api.model;

import java.util.List;

import com.ikonke.api.model.ZigbeeDevice;
import com.ikonke.api.model.Device;
import lombok.Data;

@Data
public class NodeListResp {

    private List<ZigbeeDevice> zigbeeDevices;
    private List<Device> nonZigbeeDevices;

    public String size() {
        return "zigbee " + (zigbeeDevices == null ? 0 : zigbeeDevices.size())
                + ", nonZigbee " + (nonZigbeeDevices == null ? 0 : nonZigbeeDevices.size());
    }
}
