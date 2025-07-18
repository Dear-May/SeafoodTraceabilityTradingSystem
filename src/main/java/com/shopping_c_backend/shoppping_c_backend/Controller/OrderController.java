package com.shopping_c_backend.shoppping_c_backend.Controller;

import com.alibaba.fastjson.JSONArray;
import com.shopping_c_backend.shoppping_c_backend.Entity.OrderRequest;
import com.shopping_c_backend.shoppping_c_backend.Manager.OrderStatusManager;
import com.shopping_c_backend.shoppping_c_backend.Manager.OrderTimeoutManager;
import com.shopping_c_backend.shoppping_c_backend.Service.OrderServiceImpl;
import com.shopping_c_backend.shoppping_c_backend.Util.AliOSSUtil;
import com.shopping_c_backend.shoppping_c_backend.Vo.Result;
import com.shopping_c_backend.shoppping_c_backend.Websocket.SessionManager;
import lombok.Getter;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/pay")
public class OrderController {
    @Resource
    @Lazy
    private OrderServiceImpl orderService;
    private final static Logger logger = LoggerFactory.getLogger(OrderController.class);
    PolicyFactory policy = new HtmlPolicyBuilder()
            .allowElements("b", "i", "em", "strong", "a") // 允许的HTML元素
            .allowUrlProtocols("http", "https") // 允许的URL协议
            .toFactory();

    @RequestMapping(value = "storePayGoods", method = RequestMethod.POST, headers = "Content-Type=application/json")
    public String storePayGoods(@RequestBody Map<String, Object> requestMap) {
        int userId = Integer.parseInt(requestMap.get("user_id").toString() == null ? "0" : requestMap.get("user_id").toString());
        Map<String, Object> buy_now;
        List<Map<String, Object>> bucket_buy;
        if (requestMap.containsKey("buy_now") && requestMap.get("buy_now") instanceof Map) {
            buy_now = (Map<String, Object>) requestMap.get("buy_now");
            return orderService.storeGoodsInRedis(buy_now, userId);
        } else if (requestMap.containsKey("bucket_buy") && requestMap.get("bucket_buy") instanceof List) {
            bucket_buy = (List<Map<String, Object>>) requestMap.get("bucket_buy");
            return orderService.storeGoodsInRedis(bucket_buy, userId);
        }
        return "fail";
    }

    @RequestMapping(value = "getPayGoods", method = RequestMethod.POST, headers = "Content-Type=application/json")
    public List<Map<String, Object>> getPayGoods(@RequestBody Map<String, Object> requestMap) {
        String token = policy.sanitize(requestMap.get("token") == null ? "" : requestMap.get("token").toString());
        return orderService.getGoodsFromRedis(token);
    }

    @RequestMapping(value = "changGoodNum", method = RequestMethod.POST, headers = "Content-Type=application/json")
    public Result changGoodNum(@RequestBody Map<String, Object> requestMap) {
        String token = policy.sanitize(requestMap.get("token") == null ? "" : requestMap.get("token").toString());
        int num = Integer.parseInt(requestMap.get("num").toString() == null ? "0" : requestMap.get("num").toString());
        String specId = policy.sanitize(requestMap.get("specId") == null ? "" : requestMap.get("specId").toString());
        int result = orderService.changeGoodNumber(token, num, specId);
        if (result == 1) {
            return new Result(200);
        } else if (result == 0)
            return new Result(404);
        else
            return new Result(400);
    }

    @RequestMapping(value = "submitOrder", method = RequestMethod.POST, headers = "Content-Type=application/json")
    public String submitOrder(@RequestBody Map<String, Object> requestMap) {
        int userId = Integer.parseInt(requestMap.get("userId").toString() == null ? "1" : requestMap.get("userId").toString());
        String token = policy.sanitize(requestMap.get("token") == null ? "" : requestMap.get("token").toString());
        int addressId = Integer.parseInt(requestMap.get("addressId").toString() == null ? "1" : requestMap.get("addressId").toString());
        String result = orderService.submitOrder(userId, token, addressId);
        return Objects.requireNonNullElse(result, "fail");
    }

    @RequestMapping(value = "/submitOrderByView", method = RequestMethod.POST, headers = "Content-Type=application/json")
    public String submitOrderByView(@RequestBody Map<String, Object> requestMap) {
        int userId = Integer.parseInt(requestMap.get("userId").toString() == null ? "1" : requestMap.get("userId").toString());
        String orderId = policy.sanitize(requestMap.get("orderId") == null ? "" : requestMap.get("orderId").toString());
        String result = orderService.submitOrder(userId, orderId);
        return Objects.requireNonNullElse(result, "fail");
    }

    @RequestMapping(value = "/generate_qr", method = RequestMethod.POST, headers = "Content-Type=application/json")
    public ResponseEntity<?> generateQRCode(@RequestBody OrderRequest orderRequest) {
        if (orderRequest.getOrderId() == null) {
            return ResponseEntity.badRequest().body("Invalid order request");
        }
        String qrCode = orderService.generatePaymentQRCode(orderRequest.getOrderId());
        return ResponseEntity.ok().body(new Response("success", qrCode));
    }

    @RequestMapping(value = "/notify_payment", method = RequestMethod.POST)
    public void notifyPaymentStatus(@RequestParam String status, @RequestParam String orderId) {
        String message = String.format("{\"orderId\": \"%s\", \"status\": \"%s\"}", orderId, status);
        SessionManager.sendMessage("payment", orderId, message);
    }

    @RequestMapping(value = "/scan_qr", method = RequestMethod.POST)
    public ResponseEntity<String> scanQRCode(@RequestParam String orderId) {
        if (!OrderStatusManager.canScan(orderId)) {
            System.out.println("二维码已被使用");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("二维码已被使用");
        }
        OrderStatusManager.setOrderStatus(orderId, "已扫码");
        OrderTimeoutManager.setOrderTimeout(orderId, 5 * 60 * 1000); // 5 分钟超时
        SessionManager.sendMessage("payment", orderId, "{\"orderId\":\"" + orderId + "\",\"status\":\"已扫码\"}");
        return ResponseEntity.ok("扫码成功");
    }

    @RequestMapping(value = "/changOrderStatus", method = RequestMethod.POST, headers = "Content-Type=application/json")
    public Result changOrderStatus(@RequestBody Map<String, Object> requestMap) {
        String token = policy.sanitize(requestMap.get("token") == null ? "" : requestMap.get("token").toString());
        String status = policy.sanitize(requestMap.get("status") == null ? "" : requestMap.get("status").toString());
        return orderService.changeOrderStatus(token, status) == 1 ? new Result(200) : new Result(400);
    }

    @RequestMapping(value = "/changOrderStatusView", method = RequestMethod.POST, headers = "Content-Type=application/json")
    public Result changOrderStatusView(@RequestBody Map<String, Object> requestMap) {
        int userId = Integer.parseInt(requestMap.get("userId").toString() == null ? "1" : requestMap.get("userId").toString());
        String orderId = policy.sanitize(requestMap.get("orderId") == null ? "" : requestMap.get("orderId").toString());
        String status = policy.sanitize(requestMap.get("status") == null ? "" : requestMap.get("status").toString());
        return orderService.changeOrderStatus(userId, orderId, status) == 1 ? new Result(200) : new Result(400);
    }

    @RequestMapping(value = "/order/getOrderCount", method = RequestMethod.POST, headers = "Content-Type=application/json")
    public Map<String, Integer> getOrderCount(@RequestBody Map<String, Object> requestMap) {
        int userId = Integer.parseInt(requestMap.get("userId").toString() == null ? "1" : requestMap.get("userId").toString());
        return orderService.getOrderCountByUserId(userId);
    }

    @RequestMapping(value = "/order/getOrders", method = RequestMethod.POST, headers = "Content-Type=application/json")
    public List<Map<String, Object>> getOrders(@RequestBody Map<String, Object> requestMap) {
        int userId = Integer.parseInt(requestMap.get("userId").toString() == null ? "1" : requestMap.get("userId").toString());
        return orderService.getOrderByUserId(userId);
    }

    // 退货部分
    @RequestMapping(value = "/return/addReturnInfo", method = RequestMethod.POST, headers = "Content-Type=application/json")
    public Map<String, Object> addReturnInfo(@RequestBody Map<String, Object> requestMap) {
        String orderId = policy.sanitize(requestMap.get("orderId") == null ? "" : requestMap.get("orderId").toString());
        String info = requestMap.get("info") == null ? "" : requestMap.get("info").toString();
        String result = orderService.addReturnInfo(orderId, info);
        Map<String, Object> map = new HashMap<>();
        if (!result.isEmpty()) {
            map.put("result", new Result(200));
            map.put("returnId", result);
        } else
            map.put("result", new Result(400));
        return map;
    }

    @RequestMapping(value = "/return/uploadImg", method = RequestMethod.POST, consumes = "multipart/form-data")
    public Result uploadImages(@RequestParam("images") List<MultipartFile> files, @RequestParam("returnId") String returnId) throws IOException {
        try {
            AliOSSUtil aliOSSUtil = new AliOSSUtil();
            String path = "Return";
            int index = 0;
            JSONArray jsonArray = new JSONArray();
            if (files != null) {
                for (MultipartFile file : files) {
                    String result = aliOSSUtil.upload(file, path, returnId + "_" + index);
                    if (result != null) {
                        String avatarUrl = "https://oss.yy0313.fit/" + path + "/" + returnId + "_" + index + ".jpg";
                        jsonArray.add(avatarUrl);
                    }
                    index++;
                }
            }
            return orderService.updateReturnImg(returnId, jsonArray.toJSONString()) != 0 ? new Result(200) : new Result(400);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new Result(500);
        }
    }

    // 评价部分
    @RequestMapping(value = "/comment/getComment", method = RequestMethod.POST, headers = "Accept=application/json")
    public List<Map<String, Object>> getComment(@RequestBody Map<String, Object> requestMap) {
        int userId = Integer.parseInt(requestMap.get("userId").toString() == null ? "1" : requestMap.get("userId").toString());
        return orderService.getCommentByUserId(userId);
    }

    @RequestMapping(value = "/comment/getCommentByGoodId", method = RequestMethod.POST, headers = "Accept=application/json")
    public List<Map<String, Object>> getCommentByGoodId(@RequestBody Map<String, Object> requestMap) {
        String goodId = requestMap.get("goodId").toString() == null ? "1" : requestMap.get("goodId").toString();
        return orderService.getCommentByGoodId(goodId);
    }

    @RequestMapping(value = "/comment/addComment", method = RequestMethod.POST, headers = "Accept=application/json")
    public Result addComment(
            @RequestParam("userId") int userId,
            @RequestParam("orderId") String orderId,
            @RequestParam("content") String content,
            @RequestParam("rate") Integer rate,
            @RequestParam("files") List<MultipartFile> files) {
        try {
            int commentId = orderService.insertComment(orderId, content, rate);
            AliOSSUtil aliOSSUtil = new AliOSSUtil();
            String path = "CommentShow";
            int index = 0;
            JSONArray jsonArray = new JSONArray();
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String result = aliOSSUtil.upload(file, path, commentId + "_" + index);
                    if (result != null) {
                        String avatarUrl = "https://oss.yy0313.fit/CommentShow/" + commentId + "_" + index + ".jpg";
                        jsonArray.add(avatarUrl);
                    }
                    index++;
                }
            }
            if (jsonArray.isEmpty()) {
                return orderService.changeOrderStatus(userId, orderId, "已评价") == 1 ? new Result(200) : new Result(400);
            }
            int result = orderService.updateCommentImages(orderId, jsonArray.toJSONString());
            if (result == 1) {
                orderService.deleteCommentInRedis(userId);
                orderService.getCommentByUserId(userId);
                return orderService.changeOrderStatus(userId, orderId, "已评价") == 1 ? new Result(200) : new Result(400);
            } else
                return new Result(400);
        } catch (Exception e) {
            logger.error("addComment error", e);
            return new Result(400);
        }
    }

    @RequestMapping(value = "/comment/deleteComment", method = RequestMethod.POST, headers = "Accept=application/json")
    public Result deleteComment(@RequestBody Map<String, Object> requestMap) {
        int userId = Integer.parseInt(requestMap.get("userId").toString() == null ? "1" : requestMap.get("userId").toString());
        int commentId = Integer.parseInt(requestMap.get("commentId").toString() == null ? "1" : requestMap.get("commentId").toString());
        return orderService.deleteComment(userId, commentId) == 1 ? new Result(200) : new Result(400);
    }

    @RequestMapping(value = "/chat/getChatOrderList", method = RequestMethod.POST, headers = "Accept=application/json")
    public List<Map<String, Object>> getChatOrderList(@RequestBody Map<String, Object> requestMap) {
        int userId = Integer.parseInt(requestMap.get("userId").toString() == null ? "1" : requestMap.get("userId").toString());
        int shopId = Integer.parseInt(requestMap.get("shopId").toString() == null ? "1" : requestMap.get("shopId").toString());
        return orderService.getChatOrderList(userId, shopId);
    }

    @Getter
    private static class Response {
        private String status;
        private String qrCode;

        public Response(String status, String qrCode) {
            this.status = status;
            this.qrCode = qrCode;
        }

    }
}
