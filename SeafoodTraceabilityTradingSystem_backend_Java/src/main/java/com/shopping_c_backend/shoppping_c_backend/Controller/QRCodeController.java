package com.shopping_c_backend.shoppping_c_backend.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping_c_backend.shoppping_c_backend.Component.WechatProvider;
import com.shopping_c_backend.shoppping_c_backend.Entity.ThirdPartyEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.WechatUserEntity;
import com.shopping_c_backend.shoppping_c_backend.Mapper.UserMapper;
import com.shopping_c_backend.shoppping_c_backend.Service.UserServiceImpl;
import com.shopping_c_backend.shoppping_c_backend.Vo.WechatStatusResult;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/wx")
public class QRCodeController {

    private WebDriver driver; // 用于模拟浏览器的 WebDriver
    private String sceneValue; // 保存生成二维码时的 scene 值
    private String qrCodeUrl;  // 保存生成的二维码 URL
    @Resource
    private WechatProvider wechatProvider;
    @Resource
    private UserMapper userMapper;
    @Resource
    @Lazy
    private UserServiceImpl userService;
    private static final Logger logger = LoggerFactory.getLogger(QRCodeController.class);

    // 启动无界面模式的 ChromeDriver
    private WebDriver initChromeDriver() {
        WebDriverManager.chromedriver().setup();
        // 设置 ChromeOptions 以启用无界面模式
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // 启用无头模式
        options.addArguments("--disable-gpu"); // 禁用 GPU
        options.addArguments("--window-size=1920x1080"); // 设置窗口大小（解决一些网页布局问题）

        // 创建 WebDriver 实例
        return new ChromeDriver(options);
    }

    @PostMapping("/startQRCode")
    public ResponseEntity<Map<String, String>> startQRCode() {
        Map<String, String> response = new HashMap<>();

        // 启动模拟浏览器
        driver = initChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        try {
            // 访问二维码生成页面
            driver.get("https://likeyunkeji.likeyunba.com/liKeYunKaiFaZhe/Xcx_ScanLogin/createQrcode/");

            // 等待页面加载并点击生成二维码按钮
            WebDriverWait wait = new WebDriverWait(driver, 10);
            WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.id("createQrcode")));
            button.click();

            // 等待二维码加载并获取二维码 URL
            WebElement qrCodeImg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("miniproQrCode")));
            qrCodeUrl = qrCodeImg.getAttribute("src");

            // 获取 scene 值
            WebElement sceneElement = driver.findElement(By.id("scene"));
            sceneValue = sceneElement.getAttribute("value");

            // 将二维码 URL 和 scene 值返回给前端
            response.put("qrCodeUrl", qrCodeUrl);
            response.put("sceneValue", sceneValue);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("发生异常：", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/pollQRCodeStatus")
    public ResponseEntity<Map<String, String>> pollQRCodeStatus() {
        Map<String, String> response = new HashMap<>();

        if (driver == null || sceneValue == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("status", "模拟未启动或二维码未生成"));
        }

        String statusUrl = "https://likeyunkeji.likeyunba.com/liKeYunKaiFaZhe/Xcx_ScanLogin/createQrcode/checkScanStatus.php?scene=" + sceneValue;
        String openid = null;
        long startTime = System.currentTimeMillis(); // 记录开始时间
        int maxPollingCount = 500;   // 最大轮询次数

        // 持续轮询二维码状态，直到状态改变
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            for (int i = 0; i < maxPollingCount; i++) {
                // 检查是否超过40秒
                if ((System.currentTimeMillis() - startTime) > 60000) { // 40秒 = 40000毫秒
                    response.put("status", "二维码已过期，请重新生成二维码。");
                    break; // 或者根据需要返回特定值
                }

                HttpGet request = new HttpGet(statusUrl);
                try (CloseableHttpResponse responseEntity = httpClient.execute(request)) {
                    String json = EntityUtils.toString(responseEntity.getEntity());
                    ObjectMapper objectMapper = new ObjectMapper();
                    WechatStatusResult statusResponse = objectMapper.readValue(json, WechatStatusResult.class);

                    // 检查状态码
                    if (statusResponse.getCode() == 203) {
                        response.put("status", "二维码已扫描，请在手机端确认登录。");
                        TimeUnit.SECONDS.sleep(1);
                        continue;
                    }
                    if (statusResponse.getCode() == 200) {
                        WechatUserEntity wechatUser = wechatProvider.getWechatUser(statusResponse.getOpenid());
                        if (userMapper.getUserIdByWechatid(statusResponse.getOpenid()) == null) {
                            ThirdPartyEntity thirdPartyEntity = createThirdPartyEntity(wechatUser);
                            String jwtToken = userService.WriteThirdLogin(thirdPartyEntity);
                            response.put("status", "扫码成功，token：" + jwtToken);
                        } else {
                            response.put("status", "登录成功，欢迎回来！openid：" + statusResponse.getOpenid());
                        }
                        break;
                    } else {
                        if (statusResponse.getCode() == 204) {
                            response.put("status", "用户取消扫码，请重新生成二维码。");
                            break;
                        }
                    }

                    // 暂停一段时间再进行下一次请求
                    TimeUnit.SECONDS.sleep(2); // 可以根据需要调整轮询间隔
                }
            }
        } catch (IOException | InterruptedException e) {
            logger.error("发生异常：", e);
        }

        // 结束轮询时关闭模拟
        driver.quit();
        return ResponseEntity.ok(response);
    }

    private ThirdPartyEntity createThirdPartyEntity(WechatUserEntity user) {
        ThirdPartyEntity thirdPartyEntity = new ThirdPartyEntity();
        thirdPartyEntity.setSource(user.getSource());
        thirdPartyEntity.setId(user.getId());
        thirdPartyEntity.setName(user.getName());
        return thirdPartyEntity;
    }
}
