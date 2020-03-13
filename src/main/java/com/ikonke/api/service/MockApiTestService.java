package com.ikonke.api.service;

import com.ikonke.api.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


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

    @Autowired
    private ApiRequestService apiRequestService;

    private App mockApp=new App();

    //调度任务
    private NioEventLoopGroup monitorGroup = new NioEventLoopGroup(2, new DefaultThreadFactory("POOL_MONITOR"));
    //http请求
    private NioEventLoopGroup requestGroup = new NioEventLoopGroup(10, new DefaultThreadFactory("POOL_REQUEST"));

    @PostConstruct
    public void init() {
        //加载数据
        loadData();
        //开始请求
        startReq();
    }

    private void startReq() {
        //每分钟执行一次
        monitorGroup.scheduleAtFixedRate(() -> {
            log.info("schedule start req");
            randomReq();
        }, 1, 60, TimeUnit.SECONDS);
    }

    //随机操作
    private void randomReq() {
        //随机选择一个开发者
//        String userId = userIds.get(new Random().nextInt(userIds.size()));
        App app = mockApp;
        log.info("choose user with app {}",  app.getAppId());
        requestGroup.next().submit(() -> {
            //调用获取主机列表接口
            List<CcuItem> ccuList = apiRequestService.getCcuList(app);
            if (CollectionUtils.isEmpty(ccuList)) {
                log.info("no ccu with app {}, won't handle", app.getAppId());
                return;
            }
            List<CcuItem> optccuList=ccuList.stream().filter(ccuItem -> ccuItem.isOnline()).collect(Collectors.toList());
            for (int i = 0; i < optccuList.size() && i < 3; i++) {
                CcuItem ccu = optccuList.get(new Random().nextInt(optccuList.size()));
                CcuInfo ccuInfo=apiRequestService.getCcuInfo(app,ccu);
                CcuState ccuState = apiRequestService.getCcuState(app, ccu);
                if (ccuState == null || !"CLIENT_WORKING".equals(ccuState.getState())) {
                    log.info("ccu {} state is not working but {}, won't handle", ccu.getDeviceId(), ccuState == null ? "null" : ccuState.getState());
                    continue;
                }
                CcuState armingState = apiRequestService.getArmingState(app, ccu);
                NodeListResp nodeList = apiRequestService.getNodeList(app, ccu);
                List<Device> deviceList = apiRequestService.getDevList(app, ccu).stream().filter(device -> !device.getType().equals("UNKOWN")).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(deviceList)) {
                    log.info("ccu {} has no device, won't get status and opt", ccu.getDeviceId());
                    continue;
                }
                for (int j = 0; j < deviceList.size() && j < 4; j++) {
                    Device dev = deviceList.get(new Random().nextInt(deviceList.size()));
                    DeviceStatus devStatus = apiRequestService.getDevStatus(app, ccu, dev);
                }
                for (Device dev:deviceList){
                    if (dev.getType().contains("LIGHT")|| dev.getType().contains("SOCKET")){
                        OptResp optResp = apiRequestService.opt(app, ccu, dev);
                    }
                }
            }
        });
    }


    //初始化header
    private void loadData() {
        mockApp.setAppId(mockAppId);
        mockApp.setAppKey(mockAppKey);
    }

}
