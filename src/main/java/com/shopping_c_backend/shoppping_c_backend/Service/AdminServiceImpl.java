package com.shopping_c_backend.shoppping_c_backend.Service;

import com.shopping_c_backend.shoppping_c_backend.Entity.AdminTokenEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.ReviewLicenseEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.ShopEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.ShopUserEntity;
import com.shopping_c_backend.shoppping_c_backend.Mapper.AdminMapper;
import com.shopping_c_backend.shoppping_c_backend.Mapper.ShopMapper;
import com.shopping_c_backend.shoppping_c_backend.Util.EmailUtil;
import com.shopping_c_backend.shoppping_c_backend.Vo.Result;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminServiceImpl {
    @Resource
    private AdminMapper adminMapper;
    @Resource
    private ShopMapper shopMapper;
    @Resource
    private EmailUtil emailUtil;

    public Result checkUserPermission(String id) {
        AdminTokenEntity adminTokenEntity = adminMapper.getAdminToken(id);
        if (adminTokenEntity == null)
            return new Result(400);
        return new Result(200);
    }

    public List<Map<String, Object>> getAllReviewLicenses() {
        List<Map<String, Object>> list = new ArrayList<>();
        List<ReviewLicenseEntity> reviewLicenseEntities = adminMapper.getReviewLicenses();
        for (ReviewLicenseEntity reviewLicenseEntity : reviewLicenseEntities) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", reviewLicenseEntity.getShopid());
            ShopEntity shopEntity = shopMapper.findShopById(reviewLicenseEntity.getShopid());
            map.put("name", shopEntity.getShopName());
            map.put("licenseInfo", reviewLicenseEntity);
            map.put("licenseImage", reviewLicenseEntity.getImageurl());
            list.add(map);
        }
        return list;
    }

    public Result updateReviewLicenseStatus(String id, String status) {
        ReviewLicenseEntity reviewLicenseEntity = adminMapper.getReviewLicenseById(id);
        System.out.println(reviewLicenseEntity);
        if (reviewLicenseEntity == null)
            return new Result(400);
        reviewLicenseEntity.setStatus(status);
        reviewLicenseEntity.setMangerid(1);
        ShopEntity shopEntity = shopMapper.findShopById(reviewLicenseEntity.getShopid());
        if (status.equals("通过"))
            shopEntity.setStatus("正常");
        else
            shopEntity.setStatus("审核失败");
        int result;
        result = adminMapper.updateReviewLicense(reviewLicenseEntity);
        if (result == 0)
            return new Result(400);
        result = shopMapper.updateShop(shopEntity);
        if (result == 0)
            return new Result(400);
        ShopUserEntity shopUserEntity = shopMapper.findMerchantByShopId(reviewLicenseEntity.getShopid());
        if (status.equals("通过"))
            emailUtil.sendStoreEmail(shopUserEntity.getEmail(), shopEntity.getShopName(), "店铺通过审核通知", reviewLicenseEntity.getStatus());
        else if (status.equals("未通过"))
            emailUtil.sendStoreEmail(shopUserEntity.getEmail(), shopEntity.getShopName(), "店铺未通过审核通知", reviewLicenseEntity.getStatus());
        return new Result(200);
    }

}
