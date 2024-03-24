package com.nowcoder.community.community.util;


import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommunityUtil {

    // 生成随机字符串
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    // MD5加密
    // hello -> abc123456
    // heelo + 3e4a0 -> ab12hi1h3iu12
    // key = 随机字符串 + salt
    public static String md5(String key) {
        // string 判空
        if (StringUtils.isBlank(key)) {
            return null;
        }
        // 将传入的字符串  加密成  16进制的密文  返回
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

//    public static String getJSONString(int code, String msg, Map<String, Object> map) {
//        JSONObject json = new JSONObject();
//        json.put("code", code);
//        json.put("msg", msg);
//        if (map != null) {
//            for (String key : map.keySet()) {
//                json.put(key, map.get(key));
//            }
//        }
//        return json.toJSONString();
//    }
//
//    public static String getJSONString(int code, String msg) {
//        return getJSONString(code, msg, null);
//    }
//
//    public static String getJSONString(int code) {
//        return getJSONString(code, null, null);
//    }
//
//    public static void main(String[] args) {
//        Map<String, Object> map = new HashMap<>();
//        map.put("name", "zhangsan");
//        map.put("age", 25);
//        System.out.println(getJSONString(0, "ok", map));
//    }

}