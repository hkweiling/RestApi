package com.ikonke.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author:
 * @time: 2020/3/11 11:37
 */
@Data
public class CurtainAction {
    private String action;
    private String actionArg;

    public CurtainAction(CurtainOpt opt){
        this.action="MotorOpt";
        this.actionArg=opt.toString();
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class ActionArg {
        private CurtainOpt opt;


    }

    private static enum  CurtainOpt{
        OPEN,
        STOP,
        CLOSE;

        private CurtainOpt(){
        }
    }
}
