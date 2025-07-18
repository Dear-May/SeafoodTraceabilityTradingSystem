package com.shopping_c_backend.shoppping_c_backend.Controller;

import com.alibaba.fastjson.JSONArray;
import com.shopping_c_backend.shoppping_c_backend.Service.TraceServiceImpl;
import com.shopping_c_backend.shoppping_c_backend.Util.AliOSSUtil;
import com.shopping_c_backend.shoppping_c_backend.Vo.Result;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/trace")
public class TraceController {
    @Resource
    @Lazy
    private TraceServiceImpl traceService;
    private static final Logger logger = LoggerFactory.getLogger(TraceController.class);

    PolicyFactory policy = new HtmlPolicyBuilder()
            .allowElements("b", "i", "em", "strong", "a") // 允许的HTML元素
            .allowUrlProtocols("http", "https") // 允许的URL协议
            .toFactory();

    @RequestMapping(value = "/getShopTrace", method = RequestMethod.POST, consumes = "application/json")
    public List<Map<String, Object>> getShopTrace(@RequestBody Map<String, Object> requestmap) {
        int ShopID = (Integer) requestmap.get("ShopID");
        return traceService.getShopTrace(ShopID);
    }

    @RequestMapping(value = "/getGoodTrace", method = RequestMethod.POST, consumes = "application/json")
    public List<Map<String, Object>> getGoodTrace(@RequestBody Map<String, Object> requestmap) {
        String GoodID = (String) requestmap.get("GoodID");
        return traceService.getGoodTrace(GoodID);
    }

    @RequestMapping(value = "/createTrace", method = RequestMethod.POST, consumes = "application/json")
    public Map<String, Object> createTrace(@RequestBody Map<String, Object> requestmap) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<String> goodIDs = (List<String>) requestmap.get("goodIDs");
        String info = policy.sanitize((String) requestmap.get("info"));
        int userID = (Integer) requestmap.get("userID");
        int shopID = (Integer) requestmap.get("shopID");
        List<String> traceID = traceService.createTrace(goodIDs, info, userID, shopID);
        result.put("result", traceID != null ? new Result(200) : new Result(400));
        if (traceID != null)
            result.put("traceID", traceID);
        return result;
    }

    @RequestMapping(value = "/addTrace", method = RequestMethod.POST, consumes = "application/json")
    public Map<String, Object> addTrace(@RequestBody Map<String, Object> requestmap) {
        Map<String, Object> result = new HashMap<String, Object>();
        String traceID = policy.sanitize((String) requestmap.get("traceID"));
        String info = policy.sanitize((String) requestmap.get("info"));
        int userID = (Integer) requestmap.get("userID");
        int shopID = (Integer) requestmap.get("shopID");
        String InfoID = traceService.addTrace(traceID, info, userID, shopID);
        result.put("result", InfoID != null ? new Result(200) : new Result(400));
        if (InfoID != null)
            result.put("InfoID", InfoID);
        return result;
    }

    @RequestMapping(value = "/uploadImg", method = RequestMethod.POST, consumes = "multipart/form-data")
    public Result uploadImages(@RequestParam("images") List<MultipartFile> files, @RequestParam("traceInfoID") String traceInfoID) throws IOException {
        try {
            AliOSSUtil aliOSSUtil = new AliOSSUtil();
            String path = "Trace";
            int index = 0;
            JSONArray jsonArray = new JSONArray();
            if (files != null) {
                for (MultipartFile file : files) {
                    String result = aliOSSUtil.upload(file, path, traceInfoID + "_" + index);
                    if (result != null) {
                        String avatarUrl = "https://oss.yy0313.fit/" + path + "/" + traceInfoID + "_" + index + ".jpg";
                        jsonArray.add(avatarUrl);
                    }
                    index++;
                }
            }
            return traceService.updateTraceImg(traceInfoID, jsonArray.toJSONString()) != 0 ? new Result(200) : new Result(400);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new Result(500);
        }
    }
}
