package com.nnocpeed.draco.components;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nnocpeed.draco.DracoApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import com.hikvision.artemis.sdk.config.ArtemisConfig;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class HiKivisionComponent {

    JSONObject srv = null;

    String host = null;
    String ak = null;
    String sk = null;
    private static final String ARTEMIS_PATH = "/artemis";

    @PostConstruct
    public void init() {
        srv = DracoApplication.vendorDetailMap.get("HiKivision");
        host = srv.getString("host");
        ak = srv.getString("ak");
        sk = srv.getString("sk");
    }

    public String callPostApiGetOrgList() throws Exception {
        /**
         * https://ip:port/artemis/api/resource/v1/org/orgList
         * 通过查阅AI Cloud开放平台文档或网关门户的文档可以看到获取组织列表的接口定义,该接口为POST请求的Rest接口, 入参为JSON字符串，接口协议为https。
         * ArtemisHttpUtil工具类提供了doPostStringArtemis调用POST请求的方法，入参可传JSON字符串, 请阅读开发指南了解方法入参，没有的参数可传null
         */
        ArtemisConfig config = this.initConfig();
        final String getCamsApi = ARTEMIS_PATH + "/api/resource/v1/org/orgList";
        Map<String, String> paramMap = new HashMap<String, String>();// post请求Form表单参数
        paramMap.put("pageNo", "1");
        paramMap.put("pageSize", "2");
        String body = JSON.toJSON(paramMap).toString();
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", getCamsApi);
            }
        };
        return ArtemisHttpUtil.doPostStringArtemis(config,path, body, null, null, "application/json");
    }
    public String callPostApiGetRegions() throws Exception {
        /**
         * https://ip:port/artemis/api/resource/v1/regions
         * 过查阅AI Cloud开放平台文档或网关门户的文档可以看到分页获取区域列表的定义,这是一个POST请求的Rest接口, 入参为JSON字符串，接口协议为https。
         * ArtemisHttpUtil工具类提供了doPostStringArtemis调用POST请求的方法，入参可传JSON字符串, 请阅读开发指南了解方法入参，没有的参数可传null
         */
        ArtemisConfig config = this.initConfig();
        final String getCamsApi = ARTEMIS_PATH + "/api/resource/v1/regions";
        Map<String, String> paramMap = new HashMap<String, String>();// post请求Form表单参数
        paramMap.put("pageNo", "1");
        paramMap.put("pageSize", "2");
        paramMap.put("treeCode", "0");
        String body = JSON.toJSON(paramMap).toString();
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", getCamsApi);
            }
        };
        return ArtemisHttpUtil.doPostStringArtemis(config,path, body, null, null, "application/json");
    }

    private ArtemisConfig initConfig(){
        ArtemisConfig config = new ArtemisConfig();
        config.setHost(host); // 代理API网关nginx服务器ip端口
        config.setAppKey(ak);  // 秘钥appkey
        config.setAppSecret("sk");// 秘钥appSecret

        return config;
    }
}
