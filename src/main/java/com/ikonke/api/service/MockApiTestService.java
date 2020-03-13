package com.ikonke.api.service;

import com.ikonke.api.model.*;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Component
public class MockApiTestService {

    @Value("${mock.appid}")
    private String mockAppId;
    @Value("${mock.appkey}")
    private String mockAppKey;
    @Value("${api.prefix}")
    private String prefix;
    @Value("${api.prefix.old}")
    private String prefixold;

    @Autowired
    private ApiRequestService apiRequestService;

    private NioEventLoopGroup monitorGroup = new NioEventLoopGroup(2, new DefaultThreadFactory("POOL_MONITOR"));

    @PostConstruct
    public void init() {
        //开始请求
        startReq();
    }

    private void startReq() {
        //每分钟执行一次
        monitorGroup.scheduleAtFixedRate(() -> {
            log.info("schedule start req");
            compareResp();
        }, 1, 60, TimeUnit.SECONDS);
    }


    //随机操作
    private void compareResp() {
        //随机选择一个开发者
//        String userId = userIds.get(new Random().nextInt(userIds.size()));
        App app = new App();
        app.setAppId(mockAppId);
        app.setAppKey(mockAppKey);
        log.info("choose user with app {}",  app.getAppId());
        List<CcuItem> ccuList = apiRequestService.getCcuList(app,prefix);
        List<CcuItem> ccuListold=apiRequestService.getCcuList(app,prefixold);
        if (ccuList.size()==ccuListold.size()){
           log.info("ccu list is same ");
        }else {
            List<CcuItem> difCcuList=new ArrayList<>();
            if (ccuList.size()>ccuListold.size()){
                difCcuList=ccuList.stream().filter(ccuItem -> !ccuListold.contains(ccuItem)).collect(Collectors.toList());
            }else {
                difCcuList=ccuListold.stream().filter(ccuItem -> !ccuList.contains(ccuItem)).collect(Collectors.toList());
            }
            log.error("ccu list is different, dif={}",difCcuList.toString());
        }
        if (CollectionUtils.isEmpty(ccuList)|| CollectionUtils.isEmpty(ccuList)) {
            log.info("no ccu with app {}, won't handle", app.getAppId());
            return;
        }
        List<CcuItem> optccuList=ccuList.stream().filter(ccuItem -> ccuItem.isOnline()).collect(Collectors.toList());
        CcuItem ccu = optccuList.get(new Random().nextInt(optccuList.size()));
        CcuInfo ccuInfo=apiRequestService.getCcuInfo(app,ccu,prefix);
        CcuInfo ccuInfo1=apiRequestService.getCcuInfo(app,ccu,prefixold);
        if (ccuInfo.equals(ccuInfo1)){
            log.info("ccu info is same, ccuId={}",ccu.getDeviceId());
        }else {
            log.error("ccu info is different, ccuId={}, new={}, old={}",ccu.getDeviceId(),ccuInfo,ccuInfo1);
        }
        CcuState ccuState = apiRequestService.getCcuState(app, ccu,prefix);
        CcuState ccuState1=apiRequestService.getCcuState(app,ccu,prefixold);
        if (ccuState == null ||!"CLIENT_WORKING".equals(ccuState.getState())) {
            log.info("ccu {} state is not working but {}, won't handle", ccu.getDeviceId(), ccuState == null ? "null" : ccuState.getState());
        }
        if (ccuState!=null && ccuState.equals(ccuState1)) {
            log.info("ccu state is same, ccuId={}",ccu.getDeviceId());
        }else {
            log.error("ccu state is different, ccuId={}, new={}, old={}",ccu.getDeviceId(),ccuState,ccuState1);
        }
        CcuState armingState = apiRequestService.getArmingState(app, ccu,prefix);
        CcuState armingState1=apiRequestService.getArmingState(app,ccu,prefixold);
        if (armingState.equals(armingState1)){
            log.info("ccu arming state is same, ccuId={}",ccu.getDeviceId());
        }else {
            log.error("ccu arming state is different, ccuId={}, new={}, old={}",ccu.getDeviceId(),armingState,armingState1);
        }
        NodeListResp nodeList = apiRequestService.getNodeList(app, ccu,prefix);
        NodeListResp nodeList1=apiRequestService.getNodeList(app,ccu,prefixold);
        if (nodeList.getZigbeeDevices().size()==nodeList1.getZigbeeDevices().size()){
            log.info("ccu node list is same, ccuId={}",ccu.getDeviceId());
        }else {
            List<ZigbeeDevice> difZigbeeDevices=new ArrayList<>();
            if ((nodeList.getZigbeeDevices().size()>nodeList1.getZigbeeDevices().size())){
                difZigbeeDevices=nodeList.getZigbeeDevices().stream().filter(zigbeeDevice -> !nodeList1.getZigbeeDevices().contains(zigbeeDevice)).collect(Collectors.toList());
            }else {
                difZigbeeDevices=nodeList1.getZigbeeDevices().stream().filter(zigbeeDevice -> !nodeList.getZigbeeDevices().contains(zigbeeDevice)).collect(Collectors.toList());
            }
            log.error("ccu node list is diffferent, ccuId={}, dif={}",ccu.getDeviceId(),difZigbeeDevices);

        }

        List<Device> deviceList = apiRequestService.getDevList(app, ccu,prefix);
        if (CollectionUtils.isEmpty(deviceList)) {
            log.info("ccu {} has no device, won't get status and opt", ccu.getDeviceId());
        }
        List<Device> deviceList1=apiRequestService.getDevList(app,ccu,prefixold);
        if (deviceList.size()==deviceList1.size()){
            log.info("ccu devices list is same, ccuId={}",ccu.getDeviceId());
        }else {
                List<Device> difDevices=new ArrayList<>();
                if (deviceList.size()>deviceList1.size()){
                    difDevices=deviceList.stream().filter(device -> !deviceList1.contains(device)).collect(Collectors.toList());
                }else {
                    difDevices=deviceList1.stream().filter(device -> !deviceList.contains(device)).collect(Collectors.toList());
                }
                log.error("ccu device list is different, ccuId={}, new={}, old={}",ccu.getDeviceId(),deviceList,deviceList1);
        }
        List<Device> optdeviceList=deviceList.stream().filter(device -> !device.getType().equals("UNKNOW")).collect(Collectors.toList());
        for (int j = 0; j < optdeviceList.size() ; j++) {
            Device dev = deviceList.get(new Random().nextInt(deviceList.size()));
            DeviceStatus devStatus = apiRequestService.getDevStatus(app, ccu, dev,prefix);
            DeviceStatus devStatus1=apiRequestService.getDevStatus(app,ccu,dev,prefixold);
            if (devStatus.equals(devStatus1)){
                log.info("device status is same, ccuId={}, deviceId={}",ccu.getDeviceId(),dev.getId());
            }else {
                log.error("device status is different, ccuId={}, deviceId={}, new={}, old={}",ccu.getDeviceId(),dev.getId(),devStatus,devStatus1);
            }
        }

        List<SceneInfo> scenes=apiRequestService.getApiScenes(app,ccu,prefix);
        List<SceneInfo> scenes1=apiRequestService.getApiScenes(app,ccu,prefixold);
        if (scenes.size()==scenes1.size()){
            log.info("ccu scenes is same, ccuId={}, scenesCount={}",ccu.getDeviceId(),scenes.size());
        }else {
            List<SceneInfo> difscene=new ArrayList<>();
            if (scenes.size()>scenes1.size()){
                difscene=scenes.stream().filter(sceneInfo -> !scenes1.contains(sceneInfo)).collect(Collectors.toList());
            }else {
                difscene=scenes1.stream().filter(sceneInfo -> !scenes.contains(sceneInfo)).collect(Collectors.toList());
            }
            log.error("ccu scenes is different, ccuId={}, difscene={}",ccu.getDeviceId(),difscene);
        }

        List<GroupInfo> groups=apiRequestService.getApiGroups(app,ccu,prefix);
        List<GroupInfo> groups1=apiRequestService.getApiGroups(app,ccu,prefixold);
        if (groups.size()==groups1.size()){
            log.info("ccu groups is same, ccuId={}, scenesCount={}",ccu.getDeviceId(),groups.size());
        }else {
            List<GroupInfo> difgroup=new ArrayList<>();
            if (groups.size()>groups1.size()){
                difgroup=groups.stream().filter(groupInfo -> !groups1.contains(groupInfo)).collect(Collectors.toList());
            }else {
                difgroup=groups1.stream().filter(groupInfo -> !groups.contains(groupInfo)).collect(Collectors.toList());
            }
            log.error("ccu groups is different, ccuId={}, difgroup={}",ccu.getDeviceId(),difgroup);
        }
    }


}
