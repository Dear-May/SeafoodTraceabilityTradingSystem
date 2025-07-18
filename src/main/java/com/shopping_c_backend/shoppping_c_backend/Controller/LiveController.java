package com.shopping_c_backend.shoppping_c_backend.Controller;

import com.shopping_c_backend.shoppping_c_backend.Service.LiveServiceImpl;
import com.shopping_c_backend.shoppping_c_backend.Vo.Result;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/live")
public class LiveController {
    @Lazy
    @Resource
    private LiveServiceImpl liveService;

    @RequestMapping(value = "isLiveVisible", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Result isLiveVisible(@RequestBody Map<String, Object> params) {
        int roomId = Integer.parseInt(params.get("roomId").toString());
        return liveService.isLiveVisible(roomId) ? new Result(200) : new Result(404);
    }

    @RequestMapping(value = "/updateLiveStatus", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Result updateLiveStatus(@RequestBody Map<String, Object> params) {
        int roomId = Integer.parseInt(params.get("roomId").toString());
        if (liveService.checkLiveVisibility(roomId)) {
            String status = params.get("status").toString();
            return liveService.UpdateLiveStatus(roomId, status);
        } else {
            return new Result(404);
        }
    }

    @RequestMapping(value = "/getLiveInfo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Map<String, Object> getLiveInfo(@RequestBody Map<String, Object> params) {
        int roomId = Integer.parseInt(params.get("roomId").toString());
        if (liveService.checkLiveVisibility(roomId)) {
            return liveService.getLiveInfo(roomId);
        } else
            return null;
    }

    @RequestMapping(value = "/getOnlineUserList", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public List<Map<String, Object>> getOnlineUserList(@RequestBody Map<String, Object> params) {
        int roomId = Integer.parseInt(params.get("roomId").toString());
        if (liveService.checkLiveVisibility(roomId)) {
            return liveService.getOnlineUserList(roomId);
        } else {
            return null;
        }
    }

    @RequestMapping(value = "/insertOnlineUser", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Result insertOnlineUser(@RequestBody Map<String, Object> params) {
        int roomId = Integer.parseInt(params.get("roomId").toString());
        int userId = Integer.parseInt(params.get("userId").toString());
        if (liveService.checkLiveVisibility(roomId)) {
            return liveService.InsertOnlineUser(userId, roomId);
        } else {
            return new Result(404);
        }
    }

    @RequestMapping(value = "/deleteOnlineUser", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Result deleteOnlineUser(@RequestBody Map<String, Object> params) {
        int roomId = Integer.parseInt(params.get("roomId").toString());
        int userId = Integer.parseInt(params.get("userId").toString());
        if (liveService.checkLiveVisibility(roomId)) {
            return liveService.DeleteOnlineUser(userId, roomId);
        } else {
            return new Result(404);
        }
    }

    @RequestMapping(value = "/getLiveMessageList", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public List<Map<String, Object>> getLiveMessageList(@RequestBody Map<String, Object> params) {
        int roomId = Integer.parseInt(params.get("roomId").toString());
        if (liveService.checkLiveVisibility(roomId)) {
            return liveService.getLiveMessageList(roomId);
        } else {
            return null;
        }
    }

    @RequestMapping(value = "/insertLiveMessage", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Result insertLiveMessage(@RequestBody Map<String, Object> params) {
        int roomId = Integer.parseInt(params.get("roomId").toString());
        int sendId = Integer.parseInt(params.get("sendId").toString());
        String message = params.get("message").toString();
        String sendType = params.get("sendType").toString();
        if (liveService.checkLiveVisibility(roomId)) {
            return liveService.InsertLiveMessage(sendId, roomId, message, sendType);
        } else {
            return new Result(404);
        }
    }

}
