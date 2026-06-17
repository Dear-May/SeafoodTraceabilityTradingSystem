package com.shopping_c_backend.module.admin;

import com.shopping_c_backend.common.web.Result;
import java.util.List;
import java.util.Map;

public interface AdminService {
    Result checkUserPermission(String id);
    List<Map<String, Object>> getAllReviewLicenses();
    Result updateReviewLicenseStatus(String id, String status);
}
