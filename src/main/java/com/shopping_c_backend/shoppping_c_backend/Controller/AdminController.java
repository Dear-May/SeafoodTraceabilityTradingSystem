package com.shopping_c_backend.Controller;

import com.shopping_c_backend.module.admin.AdminService;
import com.shopping_c_backend.module.search.EsService;
import com.shopping_c_backend.common.web.Result;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Resource
    private AdminServiceImpl adminService;
    @Resource
    private EsServiceImpl esService;
    // Sanitization handled via service layer

    @RequestMapping(value = "/checkUserPermission", method = RequestMethod.POST, produces = "application/json")
    public Result checkUserPermission(@RequestBody Map<String, String> map) {
        String id = map.get("id");
        return adminService.checkUserPermission(id);
    }

    @RequestMapping(value = "/getAllReviewLicenses", method = RequestMethod.POST, produces = "application/json")
    public List<Map<String, Object>> getAllReviewLicense() {
        return adminService.getAllReviewLicenses();
    }

    @RequestMapping(value = "/updateReviewLicenseStatus", method = RequestMethod.POST, produces = "application/json")
    public Result updateReviewLicenseStatus(@RequestBody Map<String, Object> map) {
        String id = map.get("id").toString();
        String status = map.get("status").toString();
        return adminService.updateReviewLicenseStatus(id, status);
    }

    @RequestMapping(value = "/syncGoodData", method = RequestMethod.POST, produces = "application/json")
    public Result syncGoodData(@RequestBody Map<String, Object> requestMap) throws IOException {
        String token = policy.sanitize(requestMap.get("token") == null ? "" : requestMap.get("token").toString());
        Result result = adminService.checkUserPermission(token);
        if (result.getCode() == 200) {
            return esService.syncData() != 0 ? Result.success() : new Result(400);
        } else
            return new Result(400);
    }
}
