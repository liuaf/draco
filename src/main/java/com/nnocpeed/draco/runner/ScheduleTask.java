package com.nnocpeed.draco.runner;

import com.nnocpeed.draco.components.HiKivisionComponent;
import com.nnocpeed.draco.handler.MqttMessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class ScheduleTask {
    @Autowired
    MqttMessageHandler mqttCfg;

    @Autowired
    MqttMessageHandler.MyGateway mqttgw;

    @Autowired
    HiKivisionComponent hiKivisionComponent;

    @Scheduled(cron="0/3 * * * * ? ")
    public void testHikivisonApi(){
        try{
            String result = hiKivisionComponent.callPostApiGetOrgList();
            System.out.println(result);

            result = hiKivisionComponent.callPostApiGetRegions();
            System.out.println(result);
        }catch(Exception e){
            e.getMessage();
        }
    }
//    @Scheduled(cron="0/3 * * * * ? ")
//    public void uploadMessage2Server(){
//        ExecutorService executor = Executors.newFixedThreadPool(5);
//        for(int i=0; i<100000; ++i) {
//            int finalI = i;
//            executor.submit(() -> {
//                System.out.println(mqttCfg.getTopics() + ", i = " + finalI);
//                mqttgw.sendToMqtt("AddObject", mqttCfg.getTopics() + "/add");
//                mqttgw.sendToMqtt("UpdateObject", mqttCfg.getTopics() + "/update");
//                mqttgw.sendToMqtt("DeleteObject", mqttCfg.getTopics() + "/delete");
//            });
//        }
//        executor.shutdown();
//    }
}
