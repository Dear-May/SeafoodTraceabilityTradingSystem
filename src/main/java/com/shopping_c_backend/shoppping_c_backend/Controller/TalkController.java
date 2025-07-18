package com.shopping_c_backend.shoppping_c_backend.Controller;

import com.shopping_c_backend.shoppping_c_backend.Service.ChatMessageServiceImpl;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/chat")
public class TalkController {
    @Resource
    @Lazy
    private ChatMessageServiceImpl chatMessageService;
    private final static Logger logger = LoggerFactory.getLogger(TalkController.class);

    PolicyFactory policy = new HtmlPolicyBuilder()
            .allowElements("b", "i", "em", "strong", "a") // 允许的HTML元素
            .allowUrlProtocols("http", "https") // 允许的URL协议
            .toFactory();

    @RequestMapping(value = "/getUnreadMessageCount", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> getUnreadMessageCount(@RequestBody Map<String, String> params) {
        int userId = Integer.parseInt(params.get("userId"));
        return chatMessageService.getUnreadMessageCount(userId);
    }

    @RequestMapping(value = "/getChatSession", method = RequestMethod.POST, produces = "application/json")
    public List<Map<String, Object>> getChatSession(@RequestBody Map<String, String> request) {
        int userId = Integer.parseInt(request.get("userId"));
        return chatMessageService.getUserChatSession(userId);
    }

    @RequestMapping(value = "/createChatSession", method = RequestMethod.POST, produces = "application/json")
    public Result createChatSession(@RequestBody Map<String, String> request) {
        int userId = Integer.parseInt(request.get("userId"));
        int shoppingId = Integer.parseInt(request.get("shopId"));
        return chatMessageService.createChatSession(userId, shoppingId) == 1 ? new Result(200) : new Result(400);
    }

    @RequestMapping(value = "/deleteChatSession", method = RequestMethod.POST, produces = "application/json")
    public Result deleteChatSession(@RequestBody Map<String, String> request) {
        int userId = Integer.parseInt(request.get("userId"));
        int shopId = Integer.parseInt(request.get("shopId"));
        return chatMessageService.deleteChatSession(userId, shopId) == 1 ? new Result(200) : new Result(400);
    }

    @RequestMapping(value = "/getChatDetail", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> getChatDetail(@RequestBody Map<String, String> request) {
        int userId = Integer.parseInt(request.get("userId"));
        int shopId = Integer.parseInt(request.get("shopId"));
        return chatMessageService.getChatDetail(userId, shopId);
    }

    @RequestMapping(value = "/sendMessage", method = RequestMethod.POST, produces = "application/json")
    public Result sendMessage(@RequestBody Map<String, String> request) {
        int userId = Integer.parseInt(request.get("userId"));
        int shopId = Integer.parseInt(request.get("shopId"));
        String message = policy.sanitize(request.get("message"));
        return chatMessageService.sendUserMessage(userId, shopId, message, "text") == 1 ? new Result(200) : new Result(400);
    }

    @RequestMapping(value = "/uploadImage", method = RequestMethod.POST, produces = "application/json")
    public Result uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("userId") int userId, @RequestParam("shopId") int shopId) throws IOException {
        // 检查文件是否为空
        if (file.isEmpty())
            return new Result(400);
        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType != null && !contentType.startsWith("image/")) return new Result(400);
        // 检查文件大小
        long size = file.getSize();
        if (size > 1024 * 1024 * 10)
            return new Result(400);
        // 保存文件
        try {
            AliOSSUtil aliOSSUtil = new AliOSSUtil();
            String path = "Message";
            String fileName = UUID.randomUUID().toString().substring(0, 10);
            String result = aliOSSUtil.upload(file, path, fileName);
            if (result != null) {
                String avatarUrl = "https://oss.yy0313.fit/" + path + "/" + fileName + ".jpg";
                return chatMessageService.sendUserMessage(userId, shopId, avatarUrl, "image") == 1 ? new Result(200) : new Result(400);
            }
        } catch (Exception e) {
            logger.error("上传图片失败", e);
            return new Result(500);
        }
        return new Result(400);
    }

    @RequestMapping(value = "/sendOrderLink", method = RequestMethod.POST, produces = "application/json")
    public Result sendOrderLink(@RequestBody Map<String, String> request) {
        int userId = Integer.parseInt(request.get("userId"));
        int shopId = Integer.parseInt(request.get("shopId"));
        String link = request.get("link");
        return chatMessageService.sendUserMessage(userId, shopId, link, "link") == 1 ? new Result(200) : new Result(400);
    }
}
