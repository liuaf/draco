package com.nnocpeed.draco;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nnocpeed.draco.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;
import java.util.*;

@Slf4j
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class DracoApplication {
    public static JSONObject cfgJson = null;
    public static List<String> runningModes = new ArrayList<>();

    public static Map<String, JSONObject> vendorDetailMap = new HashMap<>();

    public static void main(String[] args) {
        parseConfigFile();

        SpringApplication.run(DracoApplication.class, args);
    }

    /**
     *
     */
    public static void parseConfigFile(){
        String path = System.getProperty("user.dir");
        path += File.separator;
        path += "config.json";
        cfgJson = FileUtil.parseJsonFile(path);
        if (cfgJson == null) {
            log.error("Parse config.json exception, please check");
            return;
        }

        // 控制系统运行模式
        String runningMode = cfgJson.getString("runningMode");
        if(runningMode != null) {
            if(runningMode.contains(",")) {
                runningModes = Arrays.asList(runningMode.split(","));
            }else{
                runningModes.add(runningMode);
            }
        }

        JSONArray thirdPartyServers = cfgJson.getJSONArray("thirdPartyServer");
        for(int i=0; i<thirdPartyServers.size(); ++i){
            JSONObject server = thirdPartyServers.getJSONObject(i);
            vendorDetailMap.put(server.getString("vendor"), server);
        }
    }
}
