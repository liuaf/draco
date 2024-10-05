package com.nnocpeed.draco.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class FileUtil {
    public static JSONObject parseJsonFile(String filePath) {

        try {
            JSONObject jsonObj = JSONObject.parseObject(readJsonFile(filePath));
            return jsonObj;
        } catch (Exception e) {
            log.error("FileUtil parse file {} exception, errmsg={}", filePath, e.getMessage());
        }
        return null;
    }

    private static String readJsonFile(String fileName) {
        String jsonStr = "";
        try {
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static double base64FileSize( String base64String )  {

        //1.获取base64字符串长度(不含data:audio/wav;base64,文件头)
        int size0 = base64String.length();

        //2.获取字符串的尾巴的最后10个字符，用于判断尾巴是否有等号，正常生成的base64文件'等号'不会超过4个
        String tail = base64String.substring(size0-10);

        //3.找到等号，把等号也去掉,(等号其实是空的意思,不能算在文件大小里面)
        int equalIndex = tail.indexOf("=");
        if(equalIndex > 0) {
            size0 = size0 - (10 - equalIndex);
        }

        //4.计算后得到的文件流大小，单位为字节
        return size0 -( (double)size0 / 8 ) * 2;
    }
}
