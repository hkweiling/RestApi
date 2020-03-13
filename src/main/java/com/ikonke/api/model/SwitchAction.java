package com.ikonke.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class SwitchAction {
    private String action;
    private ActionArg actionArg;

    public SwitchAction(boolean on) {
        this.action = "SwitchOpt";
        this.actionArg = new ActionArg(on);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class ActionArg {
        private boolean on;
    }

}
