package test;

import com.google.gson.reflect.TypeToken;
import com.ikonke.api.model.*;
import com.ikonke.api.util.HttpUtil;
import com.ikonke.api.util.JsonUtil;

import javax.xml.ws.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @description:
 * @author:
 * @time: 2020/3/11 14:48
 */
public class Main {
    private static Map<String, String> header() {
        Map<String, String> header = new HashMap<>();
        header.put("appId", "10001248");
        header.put("appKey", "13b5d886-100a-4dd6-94d1-b073e60e0e31");
        return header;
    }
    public static void main(String[] args) {

    }

}
