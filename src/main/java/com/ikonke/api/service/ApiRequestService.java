package com.ikonke.api.service;

import com.google.gson.reflect.TypeToken;
import com.ikonke.api.model.*;
import com.ikonke.api.util.HttpUtil;
import com.ikonke.api.util.JsonUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ApiRequestService {

    @Value("${restore.file}")
    private String restorePath;
    @Value("${api.cculist}")
    private String apiCcuList;
    @Value("${api.nodelist}")
    private String apiNodeList;
    @Value("${api.devicelist}")
    private String apiDeviceList;
    @Value("${api.devicestatus}")
    private String apiDeviceStatus;
    @Value("${api.state}")
    private String apiState;
    @Value("${api.armingstate}")
    private String apiArmingState;
    @Value("${api.opt}")
    private String apiOpt;
    @Value("${api.ccuinfo}")
    private String apiCcuInfo;

    private Map<String, String> header(App app) {
        Map<String, String> header = new HashMap<>();
        header.put("appId", app.getAppId());
        header.put("appKey", app.getAppKey());
        return header;
    }

    public List<CcuItem> getCcuList(App app, int size) {
        PageReq req = new PageReq(0, size);
        Map<String, String> header = header(app);
        Map<Integer,String> response = HttpUtil.post(HttpUtil.getUnsafeOkHttpClient(), apiCcuList, req, header);
        int statusCode=0;
        for (Integer code:response.keySet()){
            statusCode=code;
        }
        if (statusCode!=200){
            log.error("call {} with header {} error, code={}, resp={}", apiCcuList,header, statusCode,response.get(statusCode));
        }else{
            String resp=response.get(statusCode);
            try {
                PageResp<CcuItem> ccuListResp = JsonUtil.fromJson(resp, new TypeToken<PageResp<CcuItem>>() {
                }.getType());
                if (ccuListResp == null) {
                    log.error("call {} with header {} error, resp={}", apiCcuList, header, resp);
                } else {
                    log.info("call {} with header {} success, total {} ccu", apiCcuList, header, ccuListResp.getTotal());
                    return ccuListResp.getData();
                }
            } catch (Exception e) {
                log.error("call {} with header {} error, resp={}, e=", apiCcuList, header, resp, e);
            }
        }
        return null;
    }

    public List<CcuItem> getCcuList(App app) {
        return getCcuList(app, 10);
    }

    public NodeListResp getNodeList(App app, CcuItem ccu) {
        Map<String, String> header = header(app);
        String url = apiNodeList.replace("CCU_ID", ccu.getId());
        Map<Integer,String> response = HttpUtil.get(HttpUtil.getUnsafeOkHttpClient(), url, header);
        int statusCode=0;
        for (Integer code:response.keySet()){
            statusCode=code;
        }
        if (statusCode!=200){
            log.error("call {} with header {} error, code={}, resp={}", url, header, statusCode,response.get(statusCode));
        }else{
            String resp=response.get(statusCode);
            try {
                NodeListResp nodeList = JsonUtil.fromJson(resp, NodeListResp.class);
                if (nodeList == null) {
                    log.error("call {} with header {} error, resp={}", url, header, resp);
                } else {
                    log.info("call {} with header {} success, {}", url, header, nodeList.size());
                    return nodeList;
                }
            } catch (Exception e) {
                log.error("call {} with header {} error, resp={}, e=", url, header, resp, e);
            }
        }
        return null;

    }

    public List<Device> getDevList(App app, CcuItem ccu) {
        Map<String, String> header = header(app);
        String url = apiDeviceList.replace("CCU_ID", ccu.getId());
        Map<Integer,String> response = HttpUtil.get(HttpUtil.getUnsafeOkHttpClient(), url, header);
        int statusCode=0;
        for (Integer code:response.keySet()){
            statusCode=code;
        }
        if (statusCode!=200){
            log.error("call {} with header {} error, code={}, resp={}", url, header, statusCode,response.get(statusCode));
        }else{
            String resp=response.get(statusCode);
            try {
                List<Device> deviceList = JsonUtil.fromJson(resp, new TypeToken<List<Device>>() {
                }.getType());
                if (deviceList == null) {
                    log.error("call {} with header {} error, resp={}", url, header, resp);
                } else {
                    log.info("call {} with header {} success, total {} devices", url, header, deviceList.size());
                    return deviceList;
                }
            } catch (Exception e) {
                log.error("call {} with header {} error, resp={}, e=", url, header, resp, e);
            }
        }
        return null;
    }

    public DeviceStatus getDevStatus(App app, CcuItem ccu, Device dev) {
        Map<String, String> header = header(app);
        String url = apiDeviceStatus.replace("CCU_ID", ccu.getId());
        Device req = new Device(dev.getId(), dev.getType());
        Map<Integer,String> response = HttpUtil.post(HttpUtil.getUnsafeOkHttpClient(), url, req, header);
        int statusCode=0;
        for (Integer code:response.keySet()){
            statusCode=code;
        }
        if (statusCode!=200){
            log.error("call {} with header {} error, code={}, resp={}, req={}", url, header, statusCode,response.get(statusCode),req);
        }else{
            String resp=response.get(statusCode);
            try {
                DeviceStatus deviceStatus = JsonUtil.fromJson(resp, DeviceStatus.class);
                if (deviceStatus == null) {
                    log.error("call {} with header {} error, resp={}", url, header, resp);
                } else {
                    log.info("call {} with header {} success, resp={}", url, header, resp);
                    return deviceStatus;
                }
            } catch (Exception e) {
                log.error("call {} with header {} error, resp={}, e=", url, header, resp, e);
            }
        }
        return null;
    }

    public CcuState getCcuState(App app, CcuItem ccu) {
        Map<String, String> header = header(app);
        String url = apiState.replace("CCU_ID", ccu.getId());
        Map<Integer,String> response = HttpUtil.get(HttpUtil.getUnsafeOkHttpClient(), url, header);
        int statusCode=0;
        for (Integer code:response.keySet()){
            statusCode=code;
        }
        if (statusCode!=200){
            log.error("call {} with header {} error, code={}, resp={}", url, header, statusCode,response.get(statusCode));
        }else{
            String resp=response.get(statusCode);
            try {
                CcuState ccuState = JsonUtil.fromJson(resp, CcuState.class);
                if (ccuState == null) {
                    log.error("call {} with header {} error, resp={}", url, header, resp);
                } else {
                    log.info("call {} with header {} success, state={}", url, header, ccuState.getState());
                    return ccuState;
                }
            } catch (Exception e) {
                log.error("call {} with header {} error, resp={}, e=", url, header, resp, e);
            }
        }
        return null;
    }

    public CcuState getArmingState(App app, CcuItem ccu) {
        Map<String, String> header = header(app);
        String url = apiArmingState.replace("CCU_ID", ccu.getId());
        Map<Integer,String> response = HttpUtil.get(HttpUtil.getUnsafeOkHttpClient(), url, header);
        int statusCode=0;
        for (Integer code:response.keySet()){
            statusCode=code;
        }
        if (statusCode!=200){
            log.error("call {} with header {} error, code={}, resp={}", url, header, statusCode,response.get(statusCode));
        }else{
            String resp=response.get(statusCode);
            try {
                CcuState ccuState = JsonUtil.fromJson(resp, CcuState.class);
                if (ccuState == null) {
                    log.error("call {} with header {} error, resp={}", url, header, resp);
                } else {
                    log.info("call {} with header {} success, armingState={}", url, header, ccuState.getState());
                    return ccuState;
                }
            } catch (Exception e) {
                log.error("call {} with header {} error, resp={}, e=", url, header, resp, e);
        }
        }
        return null;
    }

    public OptResp opt(App app, CcuItem ccu, Device dev) {
        Map<String, String> header = header(app);
        String url = apiOpt.replace("CCU_ID", ccu.getId()).replace("DEV_ID", dev.getId() + "");
        SwitchAction switchAction = new SwitchAction(new Random().nextBoolean());
        Map<Integer,String> response = HttpUtil.post(HttpUtil.getUnsafeOkHttpClient(), url, switchAction, header);
        int statusCode=0;
        for (Integer code:response.keySet()){
            statusCode=code;
        }
        if (statusCode!=200){
            log.error("call {} with header {} error, code={}, resp={}", url, header, statusCode,response.get(statusCode));
        }else {
            String resp=response.get(statusCode);
            try {
                OptResp optResp = JsonUtil.fromJson(resp, OptResp.class);
                if (optResp == null) {
                    log.error("call {} with header {} error, resp={}", url, header, resp);
                } else {
                    log.info("call {} with header {} success, success={}", url, header, optResp.isSuccess());
                    return optResp;
                }
            } catch (Exception e) {
                log.error("call {} with header {} error, resp={}, e=", url, header, resp, e);
            }
        }
        return null;
    }

    public CcuInfo getCcuInfo(App app,CcuItem ccu){
        Map<String,String> header=header(app);
        String url=apiCcuInfo.replace("CCU_ID", ccu.getId());
        Map<Integer,String> response = HttpUtil.get(HttpUtil.getUnsafeOkHttpClient(), url, header);
        int statusCode=0;
        for (Integer code:response.keySet()){
            statusCode=code;
        }
        if (statusCode!=200){
            log.error("call {} with header {} error, code={}, resp={}", url, header, statusCode,response.get(statusCode));
        }else{
            String resp=response.get(statusCode);
            try {
                CcuInfo ccuInfo=JsonUtil.fromJson(resp,CcuInfo.class);
                if (ccuInfo==null){
                    log.error("call {} with header {} error, resp={}", url, header, resp);
                }else {
                    log.info("call {} with header {} success", url, header);
                    return ccuInfo;
                }
            }catch (Exception e){
                log.error("call {} with header {} error, resp={}, e=", url, header, resp, e);
            }
        }
        return null;
    }

}
