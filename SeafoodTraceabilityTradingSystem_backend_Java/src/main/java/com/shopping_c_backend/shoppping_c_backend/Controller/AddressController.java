package com.shopping_c_backend.shoppping_c_backend.Controller;

import com.shopping_c_backend.shoppping_c_backend.Entity.AddressEntity;
import com.shopping_c_backend.shoppping_c_backend.Service.AddressImpl;
import com.shopping_c_backend.shoppping_c_backend.Util.AddressUtil;
import com.shopping_c_backend.shoppping_c_backend.Vo.Result;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

@RestController
@RequestMapping("/addresses")
public class AddressController {
    @Resource
    AddressImpl addressService;
    PolicyFactory policy = new HtmlPolicyBuilder()
            .allowElements("b", "i", "em", "strong", "a") // 允许的HTML元素
            .allowUrlProtocols("http", "https") // 允许的URL协议
            .toFactory();

    @CrossOrigin
    @RequestMapping(value = "/getAllAddresses", method = RequestMethod.POST, headers = "Content-Type=application/json")
    public List<AddressEntity> getAllAddresses(@RequestBody Map<String, Object> requestMap) {
        int userId = Integer.parseInt(policy.sanitize(requestMap.get("userId").toString()));
        return addressService.getUserAddresses(userId);
    }

    private String getAddressString(String addressId) {
        List<String> areaList = Arrays.asList(addressId.split(" "));
        int provinceId = Integer.parseInt(areaList.get(0));
        int cityId = Integer.parseInt(areaList.get(1));
        int areaId = Integer.parseInt(areaList.get(2));
        int streetId = 0;
        if (areaList.size() == 4)
            streetId = Integer.parseInt(areaList.get(3));
        return addressService.getAddressInfo(provinceId, cityId, areaId, streetId);
    }

    @CrossOrigin
    @RequestMapping(value = "/addAddress", method = RequestMethod.POST, headers = "Content-Type=application/json")
    public Result addAddress(@RequestBody Map<String, Object> requestMap) {
        int userId = Integer.parseInt(policy.sanitize(requestMap.get("userId").toString()));
        String consignee = policy.sanitize(requestMap.get("consignee").toString());
        String phone = policy.sanitize(requestMap.get("phone").toString());
        String areaIds = policy.sanitize(requestMap.get("area").toString());
        String area = getAddressString(areaIds);
        String detailAddress = policy.sanitize(requestMap.get("detailed_address").toString());
        boolean defaultAddress = policy.sanitize(requestMap.get("is_default").toString()).equals("true");
        String status = defaultAddress ? "default" : "";
        AddressEntity addressEntity = new AddressEntity(userId, consignee, phone, "china", area, detailAddress, status);
        return addressService.addAddress(addressEntity) == 1 ? new Result(200) : new Result(400);
    }

    @CrossOrigin
    @RequestMapping(value = "addressToId", method = RequestMethod.POST, headers = "Content-Type=application/json")
    public Map<String, Object> addressToId(@RequestBody Map<String, Object> requestMap) {
        String address = policy.sanitize(requestMap.get("address").toString());
        List<String> areaList = AddressUtil.extractAddress(address);
        List<Integer> ids = addressService.addressToId(address);
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> options = new ArrayList<>();

        Map<String, Object> province = new HashMap<>();
        province.put("value", ids.get(1));
        province.put("label", areaList.get(0));

        Map<String, Object> city = new HashMap<>();
        city.put("value", ids.get(2));
        city.put("label", areaList.get(1));

        Map<String, Object> area = new HashMap<>();
        area.put("value", ids.get(3));
        area.put("label", areaList.get(2));

        // 如果街道ID存在，则添加街道信息
        if (ids.size() == 5) {
            Map<String, Object> street = new HashMap<>();
            street.put("value", ids.get(4));
            street.put("label", areaList.get(3));
            area.put("children", Collections.singletonList(street));
        }

        city.put("children", Collections.singletonList(area));
        province.put("children", Collections.singletonList(city));

        options.add(province);
        result.put("options", options);
        return result;
    }

    @CrossOrigin
    @RequestMapping(value = "/updateAddress", method = RequestMethod.POST, headers = "Content-Type=application/json")
    public Result updateAddress(@RequestBody Map<String, Object> requestMap) {
        int addressId = Integer.parseInt(policy.sanitize(requestMap.get("addressId").toString()));
        int userId = Integer.parseInt(policy.sanitize(requestMap.get("userId").toString()));
        String consignee = policy.sanitize(requestMap.get("consignee").toString());
        String phone = policy.sanitize(requestMap.get("phone").toString());
        String areaIds = policy.sanitize(requestMap.get("areaIds").toString());
        String area = getAddressString(areaIds);
        String detailAddress = policy.sanitize(requestMap.get("detailed_address").toString());
        boolean defaultAddress = policy.sanitize(requestMap.get("is_default").toString()).equals("true");
        String status = defaultAddress ? "default" : "";
        AddressEntity addressEntity = new AddressEntity(addressId, userId, consignee, phone, area, detailAddress, status);
        return addressService.updateAddress(addressEntity) == 1 ? new Result(200) : new Result(400);
    }

    @CrossOrigin
    @RequestMapping(value = "/updateDefault", method = RequestMethod.POST, headers = "Content-Type=application/json")
    public Result updateDefaultAddress(@RequestBody Map<String, Object> requestMap) {
        int userId = Integer.parseInt(policy.sanitize(requestMap.get("userId").toString()));
        int addressId = Integer.parseInt(policy.sanitize(requestMap.get("addressId").toString()));
        return addressService.updateDefaultAddress(addressId, userId) == 1 ? new Result(200) : new Result(400);
    }

    @CrossOrigin
    @RequestMapping(value = "/deleteAddress", method = RequestMethod.POST, headers = "Content-Type=application/json")
    public Result deleteAddress(@RequestBody Map<String, Object> requestMap) {
        int userId = Integer.parseInt(policy.sanitize(requestMap.get("userId").toString()));
        int addressId = Integer.parseInt(policy.sanitize(requestMap.get("addressId").toString()));
        return addressService.deleteAddress(addressId, userId) == 1 ? new Result(200) : new Result(400);
    }
}
