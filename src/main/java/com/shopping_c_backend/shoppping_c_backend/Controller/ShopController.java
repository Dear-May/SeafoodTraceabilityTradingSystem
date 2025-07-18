package com.shopping_c_backend.shoppping_c_backend.Controller;

import com.shopping_c_backend.shoppping_c_backend.Constants.BaiduOCRConstants;
import com.shopping_c_backend.shoppping_c_backend.Entity.*;
import com.shopping_c_backend.shoppping_c_backend.Service.ShopServiceImpl;
import com.shopping_c_backend.shoppping_c_backend.Service.ShopUserServiceImpl;
import com.shopping_c_backend.shoppping_c_backend.Util.AliOSSUtil;
import com.shopping_c_backend.shoppping_c_backend.Util.ByteArrayMultipartFileUtil;
import com.shopping_c_backend.shoppping_c_backend.Util.DateUtil;
import com.shopping_c_backend.shoppping_c_backend.Vo.Result;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/shop")
public class ShopController {
    @Resource
    @Lazy
    private ShopServiceImpl shopService;
    @Resource
    @Lazy
    private ShopUserServiceImpl shopUserService;
    @Resource
    private BaiduOCRConstants baiduOCRConstants;
    private final static Logger logger = LoggerFactory.getLogger(ShopController.class);
    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();

    PolicyFactory policy = new HtmlPolicyBuilder()
            .allowElements("b", "i", "em", "strong", "a") // 允许的HTML元素
            .allowUrlProtocols("http", "https") // 允许的URL协议
            .toFactory();

    @RequestMapping(value = "/findShopById", method = RequestMethod.POST, produces = "application/json")
    public ShopEntity findShopById(@RequestBody Map<String, Object> requestMap) {
        int shopId = Integer.parseInt(requestMap.get("shopId").toString());
        return shopService.findById(shopId);
    }

    @RequestMapping(value = "/findFollowShop", method = RequestMethod.POST, produces = "application/json")
    public Result findFollowShop(@RequestBody Map<String, Object> requestMap) {
        int userId = Integer.parseInt(requestMap.get("userId").toString());
        int shopId = Integer.parseInt(requestMap.get("shopId").toString());
        System.out.println(shopService.findFollowShop(shopId, userId));
        return shopService.findFollowShop(shopId, userId) ? new Result(200) : new Result(400);
    }

    @RequestMapping(value = "/followShop", method = RequestMethod.POST, produces = "application/json")
    public Result followShop(@RequestBody Map<String, Object> requestMap) {
        int shopId = Integer.parseInt(requestMap.get("shopId").toString());
        int userId = Integer.parseInt(requestMap.get("userId").toString());
        return shopService.followShop(shopId, userId) == 1 ? new Result(200) : new Result(400);
    }

    @RequestMapping(value = "/unfollowShop", method = RequestMethod.POST, produces = "application/json")
    public Result unfollowShop(@RequestBody Map<String, Object> requestMap) {
        int shopId = Integer.parseInt(requestMap.get("shopId").toString());
        int userId = Integer.parseInt(requestMap.get("userId").toString());
        return shopService.unfollowShop(shopId, userId) == 1 ? new Result(200) : new Result(400);
    }

    // 登录，注册部分
    @RequestMapping(value = "/user/login", method = RequestMethod.POST, produces = "application/json")
    public Result userLogin(@RequestBody Map<String, Object> requestMap) {
        String phone = policy.sanitize(requestMap.get("phone").toString());
        if (!phone.isEmpty()) {
            boolean isResister = shopUserService.isResister(phone);
            if (!isResister)
                return new Result(403);
            else {
                ShopUserEntity shopUserEntity = shopUserService.login(phone);
                return shopUserEntity != null ? new Result(200) : new Result(400);
            }
        }
        return new Result(400);
    }

    @RequestMapping(value = "/user/getShopUser", method = RequestMethod.POST, produces = "application/json")
    public ShopUserEntity getShopUser(@RequestBody Map<String, Object> requestMap) {
        String phone = policy.sanitize(requestMap.get("phone").toString());
        return shopUserService.findUserByPhone(phone);
    }

    @RequestMapping(value = "/user/isAccessShop", method = RequestMethod.POST, produces = "application/json")
    public Result isAccessShop(@RequestBody Map<String, Object> requestMap) {
        int shopId = Integer.parseInt(requestMap.get("shopId").toString());
        if (shopService.isAccessShop(shopId))
            return new Result(200);
        else
            return new Result(404);
    }

    @RequestMapping(value = "/user/register", method = RequestMethod.POST, produces = "application/json")
    public Result registerUser(@RequestBody Map<String, Object> requestMap) throws IOException {
        String role = policy.sanitize(requestMap.get("role").toString());
        String phone = policy.sanitize(requestMap.get("phone").toString());
        String userName = policy.sanitize(requestMap.get("userName").toString());
        String shopName = policy.sanitize(requestMap.get("shopName") == null ? null : requestMap.get("shopName").toString());
        String email = requestMap.get("email").toString();
        String description = policy.sanitize(requestMap.get("description") == null ? null : requestMap.get("description").toString());
        Object shopIdObj = requestMap.get("shopId");
        int shopId = 0;
        if (shopIdObj != null) {
            String shopIdStr = shopIdObj.toString().trim();
            if (!shopIdStr.isEmpty()) {
                try {
                    shopId = Integer.parseInt(shopIdStr);
                } catch (NumberFormatException ignored) {
                }
            }
        }
        String base64Image = requestMap.get("avatarUrl").toString();
        MultipartFile multipartFile = null;
        if (base64Image.startsWith("data:image/png;base64,")) {
            base64Image = base64Image.replace("data:image/png;base64,", "");
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            multipartFile = new ByteArrayMultipartFileUtil("avatar", imageBytes, "avatar.png", "image/png");
        }
        if (role.isEmpty() || phone.isEmpty() || userName.isEmpty() || email.isEmpty()) {
            return new Result(400); // 角色、电话、用户名或电子邮件为空
        }
        if ("merchant".equals(role) && shopName.isEmpty()) {
            return new Result(400); // 商家角色但商店名为空
        }
        if ("staff".equals(role) && shopId <= 0) {
            return new Result(400); // 员工角色但商店ID无效
        }
        if ("merchant".equals(role)) {
            return addShopAndUser(shopName, phone, userName, email, description, multipartFile, role);
        } else if ("staff".equals(role)) {
            return AddStaff(shopId, phone, userName, email, role);
        }
        return new Result(400);
    }

    // 商家资质认证
    @RequestMapping(value = "/ocr", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> recognizeBusinessLicense(@RequestParam("image") MultipartFile file) throws IOException, JSONException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\": \"No image uploaded\"}");
        }
        byte[] fileData = file.getBytes();
        String API_KEY = baiduOCRConstants.getAppID();
        String SECRET_KEY = baiduOCRConstants.getAPIKey();
        String imageBase64 = Base64.getEncoder().encodeToString(fileData);
        String accessToken = getAccessToken(API_KEY, SECRET_KEY);
        String bodyContent = "image=" + URLEncoder.encode(imageBase64, StandardCharsets.UTF_8)
                + "&detect_quality=false";
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, bodyContent);

        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/business_license?access_token=" + accessToken;

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Accept", "application/json")
                .build();

        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        return ResponseEntity.ok(responseBody);
    }

    @RequestMapping(value = "/submitShopInfo", method = RequestMethod.POST, produces = "application/json")
    public Result submitShopInfo(@RequestBody Map<String, Object> requestMap) {
        ReviewLicenseEntity reviewLicenseEntity = new ReviewLicenseEntity();
        int shopId = Integer.parseInt(policy.sanitize(requestMap.get("shopID").toString()));
        String id = policy.sanitize(requestMap.get("id").toString() == null ? null : requestMap.get("id").toString().trim());
        String SocialCreditCode = policy.sanitize(requestMap.get("SocialCreditCode").toString() == null ? null : requestMap.get("SocialCreditCode").toString().trim());
        String name = policy.sanitize(requestMap.get("name").toString() == null ? null : requestMap.get("name").toString().trim());
        String location = policy.sanitize(requestMap.get("location").toString() == null ? null : requestMap.get("location").toString().trim());
        String registeredCapital = policy.sanitize(requestMap.get("registeredCapital").toString() == null ? null : requestMap.get("registeredCapital").toString().trim());
        String paidInCapital = policy.sanitize(requestMap.get("paidInCapital").toString() == null ? null : requestMap.get("paidInCapital").toString().trim());
        String dateOfEstablishment = policy.sanitize(requestMap.get("dateOfEstablishment").toString() == null ? null : requestMap.get("dateOfEstablishment").toString().trim());
        String businessScope = policy.sanitize(requestMap.get("businessScope").toString() == null ? null : requestMap.get("businessScope").toString().trim());
        String legalRepresentative = policy.sanitize(requestMap.get("legalRepresentative").toString() == null ? null : requestMap.get("legalRepresentative").toString().trim());
        String type = policy.sanitize(requestMap.get("type").toString() == null ? null : requestMap.get("type").toString().trim());
        String registrationAuthority = policy.sanitize(requestMap.get("registrationAuthority").toString() == null ? null : requestMap.get("registrationAuthority").toString().trim());
        String CompositionForm = policy.sanitize(requestMap.get("CompositionForm").toString() == null ? null : requestMap.get("CompositionForm").toString().trim());
        String IDNumber = policy.sanitize(requestMap.get("IDNumber").toString() == null ? null : requestMap.get("IDNumber").toString().trim());
        String periodOfValidity = policy.sanitize(requestMap.get("periodOfValidity").toString() == null ? null : requestMap.get("periodOfValidity").toString().trim());
        String StartingDateOfValidityPeriod = policy.sanitize(requestMap.get("StartingDateOfValidityPeriod").toString() == null ? null : requestMap.get("StartingDateOfValidityPeriod").toString().trim());
        String DateOfApproval = policy.sanitize(requestMap.get("DateOfApproval").toString() == null ? null : requestMap.get("DateOfApproval").toString().trim());
        String TaxRegistrationNumber = policy.sanitize(requestMap.get("TaxRegistrationNumber").toString() == null ? null : requestMap.get("TaxRegistrationNumber").toString().trim());
        reviewLicenseEntity.setInserttime(new Date());
        reviewLicenseEntity.setUpdatetime(new Date());
        reviewLicenseEntity.setShopid(shopId);
        reviewLicenseEntity.setId(id);
        reviewLicenseEntity.setSocialcreditcode(SocialCreditCode);
        reviewLicenseEntity.setName(name);
        reviewLicenseEntity.setLocation(location);
        reviewLicenseEntity.setRegisteredcapital(registeredCapital);
        reviewLicenseEntity.setPaidincapital(paidInCapital);
        reviewLicenseEntity.setDateofestablishment(dateOfEstablishment);
        reviewLicenseEntity.setBusinessscope(businessScope);
        reviewLicenseEntity.setLegalrepresentative(legalRepresentative);
        reviewLicenseEntity.setType(type);
        reviewLicenseEntity.setRegistrationauthority(registrationAuthority);
        reviewLicenseEntity.setCompositionform(CompositionForm);
        reviewLicenseEntity.setIdnumber(IDNumber);
        reviewLicenseEntity.setPeriodOfValidity(periodOfValidity);
        reviewLicenseEntity.setStartingdateofvalidityperiod(StartingDateOfValidityPeriod);
        reviewLicenseEntity.setDateofapproval(DateOfApproval);
        reviewLicenseEntity.setTaxregistrationnumber(TaxRegistrationNumber);
        return shopService.addReviewLincese(reviewLicenseEntity) == 1 ? new Result(200) : new Result(400);
    }

    @RequestMapping(value = "/submitLicense", method = RequestMethod.POST, produces = "application/json")
    public Result submitLicense(@RequestParam("image") MultipartFile file, @RequestParam("id") String Id, @RequestParam("shopID") int shopId) throws IOException {
        if (file.isEmpty()) {
            return new Result(400);
        }
        AliOSSUtil aliOSSUtil = new AliOSSUtil();
        String path = "ReviewLicense";
        String result = aliOSSUtil.upload(file, path, Id);
        if (result != null) {
            String avatarUrl = "https://oss.yy0313.fit/" + path + "/" + Id + ".jpg";
            ReviewLicenseEntity reviewLicenseEntity = shopService.findReviewLicenseById(Id);
            reviewLicenseEntity.setImageurl(avatarUrl);
            reviewLicenseEntity.setStatus("审核中");
            ShopEntity shopEntity = shopService.findById(shopId);
            shopEntity.setStatus("审核中");
            shopService.updateShopInfo(shopEntity);
            return shopService.updateReviewLicense(reviewLicenseEntity) == 1 ? new Result(200) : new Result(400);
        }
        return new Result(400);
    }

    // 首页信息部分
    @RequestMapping(value = "/info/getCardInfo", method = RequestMethod.POST, produces = "application/json")
    public List<Map<String, Object>> getCardInfo(@RequestBody Map<String, Object> requestMap) {
        int shopId = Integer.parseInt(requestMap.get("shopID").toString());
        return shopService.getCardInfo(shopId);
    }

    @RequestMapping(value = "/info/getProductBuyInfo", method = RequestMethod.POST, produces = "application/json")
    public List<Map<String, Object>> getProductBuyInfo(@RequestBody Map<String, Object> requestMap) {
        int shopId = Integer.parseInt(requestMap.get("shopID").toString());
        return shopService.getProductInfo(shopId);
    }

    @RequestMapping(value = "/info/getProductTrendInfo", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> getProductTrendInfo(@RequestBody Map<String, Object> requestMap) {
        int shopId = Integer.parseInt(requestMap.get("shopID").toString());
        return shopService.getProductTrendInfo(shopId);
    }

    @RequestMapping(value = "/info/getUserInfo", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> getUserInfo(@RequestBody Map<String, Object> requestMap) {
        int shopId = Integer.parseInt(requestMap.get("shopID").toString());
        return shopService.getUserInfo(shopId);
    }

    @RequestMapping(value = "/info/getOrderStatusInfo", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> getOrderStatusInfo(@RequestBody Map<String, Object> requestMap) {
        int shopId = Integer.parseInt(requestMap.get("shopID").toString());
        return shopService.getOrderStatusInfo(shopId);
    }

    // 商品管理
    @RequestMapping(value = "/getAllGoodInfo", method = RequestMethod.POST, produces = "application/json")
    public List<Map<String, Object>> getAllGoodInfo(@RequestBody Map<String, Object> requestMap) {
        int shopId = Integer.parseInt(requestMap.get("shopID").toString());
        return shopService.getAllGoodInfo(shopId);
    }

    @RequestMapping(value = "/insertGoodInfo", method = RequestMethod.POST, produces = "application/json")
    public Result insertGoodInfo(@RequestBody Map<String, Object> requestMap) throws IOException {
        int shopId = Integer.parseInt(requestMap.get("shopID").toString());
        String name = policy.sanitize(requestMap.get("name").toString());
        String origin = policy.sanitize(requestMap.get("origin").toString());
        String base64Image = requestMap.get("imageUrl").toString();
        MultipartFile multipartFile = null;
        if (base64Image.startsWith("data:image/jpeg;base64,")) {
            base64Image = base64Image.replace("data:image/jpeg;base64,", "");
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            multipartFile = new ByteArrayMultipartFileUtil("avatar", imageBytes, "goodShow.png", "image/png");
        }
        GoodEntity goodEntity = new GoodEntity();
        goodEntity.setGoodName(name);
        goodEntity.setLocation(origin);
        goodEntity.setComments(0);
        goodEntity.setStatus(true);
        goodEntity.setShopID(shopId);
        goodEntity.setUplodatime(new Date());
        Object specs = requestMap.get("specs");
        double minprice = Double.MAX_VALUE;
        List<SpecificationEntity> specificationEntities = new ArrayList<>();
        List<MultipartFile> files = new ArrayList<>();
        if (specs instanceof ArrayList) {
            ArrayList<Map<String, Object>> goodInfoMap = (ArrayList<Map<String, Object>>) specs;
            for (Map<String, Object> goodInfo : goodInfoMap) {
                String specName = policy.sanitize(goodInfo.get("name").toString());
                double price = Double.parseDouble(policy.sanitize(goodInfo.get("price").toString()));
                if (price < minprice)
                    minprice = price;
                int stock = Integer.parseInt(policy.sanitize(goodInfo.get("stock").toString()));
                SpecificationEntity specificationEntity = new SpecificationEntity();
                specificationEntity.setSpecName(specName);
                specificationEntity.setPrice(price);
                specificationEntity.setNumber(stock);
                specificationEntity.setStatus(true);
                specificationEntity.setUpdateTime(new Date());
                specificationEntities.add(specificationEntity);
                String SpecImageUrl = goodInfo.get("imageUrl").toString();
                MultipartFile SpecImage = null;
                if (SpecImageUrl.startsWith("data:image/jpeg;base64,")) {
                    SpecImageUrl = SpecImageUrl.replace("data:image/jpeg;base64,", "");
                    byte[] imageBytes = Base64.getDecoder().decode(SpecImageUrl);
                    SpecImage = new ByteArrayMultipartFileUtil("avatar", imageBytes, "goodShow.png", "image/png");
                }
                files.add(SpecImage);
            }
        } else
            return new Result(400);
        goodEntity.setPrice(minprice);
        return shopService.insertGoodInfo(goodEntity, specificationEntities, multipartFile, files) == 1 ? new Result(200) : new Result(400);
    }

    @RequestMapping(value = "/updateGoodInfo", method = RequestMethod.POST, produces = "application/json")
    public Result updateGoodInfo(@RequestBody Map<String, Object> requestMap) {
        String goodId = requestMap.get("goodID").toString();
        String name = policy.sanitize(requestMap.get("name").toString());
        int shopId = Integer.parseInt(requestMap.get("shopID").toString());
        String origin = policy.sanitize(requestMap.get("origin").toString());
        String updateStatus = null;
        if (requestMap.containsKey("updateStatus")) {
            updateStatus = (String) requestMap.get("updateStatus").toString();
        }
        String Image = requestMap.get("imageUrl").toString() == null ? null : requestMap.get("imageUrl").toString().trim();
        String type = requestMap.get("type").toString();
        GoodEntity goodEntity = new GoodEntity();
        goodEntity.setGoodID(goodId);
        goodEntity.setLocation(origin);
        goodEntity.setGoodName(name);
        goodEntity.setShopID(shopId);
        goodEntity.setShowURL(Image);
        goodEntity.setUplodatime(new Date());
        if (updateStatus != null) {
            goodEntity.setStatus(updateStatus.equals("true"));
        }
        return shopService.updateGoodInfo(goodEntity, type) >= 1 ? new Result(200) : new Result(400);
    }

    @RequestMapping(value = "/updateSpecInfo", method = RequestMethod.POST, produces = "application/json")
    public Result updateSpecInfo(@RequestBody Map<String, Object> requestMap) {
        String goodId = requestMap.get("goodID").toString();
        String specId = requestMap.get("specID").toString();
        String specName = policy.sanitize(requestMap.get("specName").toString());
        double price = Double.parseDouble(policy.sanitize(requestMap.get("specPrice").toString()));
        int stock = Integer.parseInt(policy.sanitize(requestMap.get("specStock").toString()));
        String updateStatus = null;
        if (requestMap.containsKey("updateStatus")) {
            updateStatus = requestMap.get("updateStatus").toString();
        }
        String Image = requestMap.get("imageUrl").toString() == null ? null : requestMap.get("imageUrl").toString().trim();
        String type = requestMap.get("type").toString();
        SpecificationEntity specificationEntity = new SpecificationEntity();
        specificationEntity.setSpecName(specName);
        specificationEntity.setPrice(price);
        specificationEntity.setNumber(stock);
        specificationEntity.setMainProductID(goodId);
        specificationEntity.setShowurl(Image);
        specificationEntity.setSpecificationID(specId);
        if (updateStatus != null) {
            specificationEntity.setStatus(updateStatus.equals("true"));
        }
        specificationEntity.setUpdateTime(new Date());
        return shopService.updateSpecificationInfo(specificationEntity, type) >= 1 ? new Result(200) : new Result(400);
    }

    @RequestMapping(value = "updateGoodImage", method = RequestMethod.POST, produces = "application/json")
    public Result updateGoodImage(@RequestParam("image") MultipartFile file, @RequestParam("goodID") String goodID) throws IOException {
        if (file.isEmpty()) {
            return new Result(400);
        }
        AliOSSUtil aliOSSUtil = new AliOSSUtil();
        String path = "GoodFirstShow";
        String result = aliOSSUtil.upload(file, path, goodID);
        return result != null ? new Result(200) : new Result(400);
    }

    @RequestMapping(value = "updateSpecImage", method = RequestMethod.POST, produces = "application/json")
    public Result updateSpecImage(@RequestParam("image") MultipartFile file, @RequestParam("specID") String specID) throws IOException {
        if (file.isEmpty()) {
            return new Result(400);
        }
        AliOSSUtil aliOSSUtil = new AliOSSUtil();
        String path = "GoodFirstShow";
        String result = aliOSSUtil.upload(file, path, specID);
        return result != null ? new Result(200) : new Result(400);
    }

    // 订单管理
    @RequestMapping(value = "getShopOrderCount", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Integer> getShopOrderCount(@RequestBody Map<String, Object> requestMap) {
        int shopId = Integer.parseInt(requestMap.get("shopID").toString());
        return shopService.getOrderCount(shopId);
    }

    @RequestMapping(value = "getShopOrderInfo", method = RequestMethod.POST, produces = "application/json")
    public List<Map<String, Object>> getShopOrderInfo(@RequestBody Map<String, Object> requestMap) {
        int shopId = Integer.parseInt(requestMap.get("shopID").toString());
        return shopService.getShopOrderInfo(shopId);
    }

    @RequestMapping(value = "/return/getShopReturnInfo", method = RequestMethod.POST, produces = "application/json")
    public List<Map<String, Object>> getShopReturnInfo(@RequestBody Map<String, Object> requestMap) {
        int shopId = Integer.parseInt(requestMap.get("shopID").toString());
        return shopService.getShopReturnOrderInfo(shopId);
    }

    @RequestMapping(value = "/return/changeReturnStatus", method = RequestMethod.POST, produces = "application/json")
    public Result changeReturnStatus(@RequestBody Map<String, Object> requestMap) {
        int workUserID = Integer.parseInt(requestMap.get("workUserID").toString());
        String orderId = policy.sanitize(requestMap.get("orderId").toString());
        String status = policy.sanitize(requestMap.get("status").toString());
        return shopService.changeReturnStatus(orderId, status, workUserID) == 1 ? new Result(200) : new Result(400);
    }

    @RequestMapping(value = "updateOrderStatus", method = RequestMethod.POST, produces = "application/json")
    public Result updateOrderStatus(@RequestBody Map<String, Object> requestMap) {
        System.out.println(requestMap);
        int userId = Integer.parseInt(requestMap.get("userId").toString());
        int shopId = Integer.parseInt(requestMap.get("shopId").toString());
        String orderId = policy.sanitize(requestMap.get("orderId").toString() == null ? "" : requestMap.get("orderId").toString());
        String status = policy.sanitize(requestMap.get("status").toString() == null ? "" : requestMap.get("status").toString());
        return shopService.updateOrderStatus(orderId, status, shopId, userId) == 1 ? new Result(200) : new Result(400);
    }

    // 员工管理
    @RequestMapping(value = "/staff/getShopStaffInfo", method = RequestMethod.POST, produces = "application/json")
    public List<ShopUserEntity> getShopStaffInfo(@RequestBody Map<String, Object> requestMap) {
        int shopId = Integer.parseInt(requestMap.get("shopID").toString());
        return shopUserService.getShopStaffInfo(shopId);
    }

    @RequestMapping(value = "/staff/getAuditShopStaffInfo", method = RequestMethod.POST, produces = "application/json")
    public List<ShopUserEntity> getAuditShopStaffInfo(@RequestBody Map<String, Object> requestMap) {
        int shopId = Integer.parseInt(requestMap.get("shopID").toString());
        return shopUserService.getAuditShopStaffInfo(shopId);
    }

    @RequestMapping(value = "/staff/addStaff", method = RequestMethod.POST, produces = "application/json")
    public Result addStaff(@RequestBody Map<String, Object> requestMap) {
        int shopId = Integer.parseInt(requestMap.get("shopID").toString());
        int id = Integer.parseInt(requestMap.get("id").toString());
        Boolean result = Boolean.parseBoolean(policy.sanitize(requestMap.get("result").toString()));
        return shopUserService.addStaff(shopId, id, result) == 1 ? new Result(200) : new Result(400);
    }

    @RequestMapping(value = "/staff/dismissStaff", method = RequestMethod.POST, produces = "application/json")
    public Result dismissStaff(@RequestBody Map<String, Object> requestMap) {
        int shopId = Integer.parseInt(requestMap.get("shopID").toString());
        int id = Integer.parseInt(requestMap.get("id").toString());
        return shopUserService.dismissStaff(shopId, id) == 1 ? new Result(200) : new Result(400);
    }

    @RequestMapping(value = "/staff/attendance/getShopStaffAttendance")
    public List<Map<String, Object>> getShopStaffAttendance(@RequestBody Map<String, Object> requestMap) {
        int userId = Integer.parseInt(requestMap.get("userId").toString());
        return shopUserService.getAttendanceByUserId(userId);
    }

    @RequestMapping(value = "/staff/attendance/signIn", method = RequestMethod.POST, produces = "application/json")
    public Result signIn(@RequestBody Map<String, Object> requestMap) throws Exception {
        int userId = Integer.parseInt(requestMap.get("userId").toString());
        String date = policy.sanitize(requestMap.get("date").toString());
        int shopId = Integer.parseInt(requestMap.get("shopID").toString());
        Date signInTime = DateUtil.string2Date(date, "yyyy-MM-dd");
        Calendar signInCalendar = Calendar.getInstance();
        signInCalendar.setTime(signInTime);
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(new Date());
        boolean isSameDay = signInCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
                signInCalendar.get(Calendar.DAY_OF_YEAR) == currentCalendar.get(Calendar.DAY_OF_YEAR);
        if (!isSameDay) {
            return new Result(401);
        }
        return shopUserService.signIn(userId, shopId) == 1 ? new Result(200) : new Result(400);
    }

    @RequestMapping(value = "/chat/getTalkList", method = RequestMethod.POST, produces = "application/json")
    public List<Map<String, Object>> getTalkList(@RequestBody Map<String, Object> requestMap) {
        int shopId = Integer.parseInt(requestMap.get("shopID").toString());
        return shopService.getTalkList(shopId);
    }

    @RequestMapping(value = "/chat/deleteChatSession", method = RequestMethod.POST, produces = "application/json")
    public Result deleteChatSession(@RequestBody Map<String, Object> requestMap) {
        int userId = Integer.parseInt(requestMap.get("userId").toString());
        int shopId = Integer.parseInt(requestMap.get("shopID").toString());
        return shopService.deleteChatSession(userId, shopId) == 1 ? new Result(200) : new Result(400);
    }

    @RequestMapping(value = "/chat/getTalkDetail", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> getTalkDetail(@RequestBody Map<String, Object> requestMap) {
        int userId = Integer.parseInt(requestMap.get("userId").toString());
        int shopId = Integer.parseInt(requestMap.get("shopID").toString());
        return shopService.getTalkDetail(userId, shopId);
    }

    @RequestMapping(value = "/chat/sendMessage", method = RequestMethod.POST, produces = "application/json")
    public Result sendMessage(@RequestBody Map<String, Object> requestMap) {
        int userId = Integer.parseInt(requestMap.get("userId").toString());
        int shopId = Integer.parseInt(requestMap.get("shopID").toString());
        String message = policy.sanitize(requestMap.get("message").toString());
        return shopService.sendMessage(userId, shopId, message, "text") == 1 ? new Result(200) : new Result(400);
    }

    @RequestMapping(value = "/chat/uploadImage", method = RequestMethod.POST, produces = "application/json")
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
                return shopService.sendMessage(userId, shopId, avatarUrl, "image") == 1 ? new Result(200) : new Result(400);
            }
        } catch (Exception e) {
            logger.error("上传图片失败", e);
            return new Result(500);
        }
        return new Result(400);
    }

    static String getAccessToken(String API_KEY, String SECRET_KEY) throws IOException, JSONException {
        MediaType mediaType = MediaType.parse("application/json");
        okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=" + API_KEY + "&client_secret=" + SECRET_KEY)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        String responseBody = response.body().string();
        JSONObject jsonObject = new JSONObject(responseBody);
        return jsonObject.getString("access_token");
    }

    private Result addShopAndUser(String shopName, String phone, String userName, String email, String description, MultipartFile file, String role) throws IOException {
        ShopEntity shopEntity = new ShopEntity();
        shopEntity.setShopName(shopName);
        shopEntity.setShopDesc(description);
        shopEntity.setStatus("未审核");
        shopEntity.setShopAvatar("https://oss.yy0313.fit/ShopAvatar/test.jpg");
        shopEntity = shopService.createShop(shopEntity);
        AliOSSUtil aliOSSUtil = new AliOSSUtil();
        String path = "ShopAvatar";
        int tag = 0;
        if (file != null) {
            String result = aliOSSUtil.upload(file, path, String.valueOf(shopEntity.getShopID()));
            if (result != null) {
                String avatarUrl = "https://oss.yy0313.fit/" + path + "/" + shopEntity.getShopID() + ".jpg";
                shopEntity.setShopAvatar(avatarUrl);
                tag = shopService.updateShopInfo(shopEntity);
            }
        }
        if (tag == 1) {
            ShopUserEntity shopUserEntity = new ShopUserEntity();
            shopUserEntity.setRole(role);
            shopUserEntity.setPhone(phone);
            shopUserEntity.setAvatar("https://oss.yy0313.fit/UserAvatar/test.jpg");
            shopUserEntity.setNickname(userName);
            shopUserEntity.setEmail(email);
            shopUserEntity.setShopid(shopEntity.getShopID());
            shopUserEntity.setStatus("正常");
            tag = shopUserService.insertShopUser(shopUserEntity);
        }
        return new Result(tag == 1 ? 200 : 400);
    }

    private Result AddStaff(int shopId, String phone, String userName, String email, String role) throws IOException {
        ShopUserEntity shopUserEntity = new ShopUserEntity();
        shopUserEntity.setRole(role);
        shopUserEntity.setPhone(phone);
        shopUserEntity.setAvatar("https://oss.yy0313.fit/UserAvatar/test.jpg");
        shopUserEntity.setNickname(userName);
        shopUserEntity.setEmail(email);
        shopUserEntity.setShopid(shopId);
        shopUserEntity.setStatus("未审核");
        return shopUserService.insertShopUser(shopUserEntity) == 1 ? new Result(200) : new Result(400);
    }
}
