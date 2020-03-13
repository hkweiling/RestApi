package com.ikonke.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChannelDevice extends Device {
    private int channel;
}
