package com.shopping_c_backend.shoppping_c_backend.Controller;

import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import com.shopping_c_backend.shoppping_c_backend.Entity.ThirdPartyEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.UserEntity;
import com.shopping_c_backend.shoppping_c_backend.Mapper.UserMapper;
import com.shopping_c_backend.shoppping_c_backend.Service.UserServiceImpl;
import com.shopping_c_backend.shoppping_c_backend.Util.AliOSSUtil;
import com.shopping_c_backend.shoppping_c_backend.Util.MD5Util;
import com.shopping_c_backend.shoppping_c_backend.Vo.Result;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {
    private MD5Util md5Util = new MD5Util();
    @Resource
    @Lazy
    private UserServiceImpl userService;
    @Resource
    private UserMapper userMapper;

    @CrossOrigin
    @ResponseBody
    @RequestMapping(value = "/login", method = RequestMethod.POST, headers = "Accept=application/json")
    public Result login(@RequestBody UserEntity requestUserEntity) {
        // 获取用户名和密码
        String username = requestUserEntity.getUsername();
        String password = requestUserEntity.getPassword();
        if (username.isEmpty() || password.isEmpty()) {
            return new Result(400);
        }
        // 对html标签进行转义，防止XSS攻击
        username = HtmlUtils.htmlEscape(username);
        username = userService.getUsername(username);
        String passwordMd5 = md5Util.passwordMD5(username, password);
        // 判断登录是否成功
        if (username == null) {
            return new Result(401);
        } else {
            if (!userService.getPassword(username).isEmpty() && userService.getPassword(username).equals(passwordMd5)) {
                return new Result(200);
            } else {
                return new Result(400);
            }
        }
    }

    @CrossOrigin
    @ResponseBody
    @RequestMapping(value = "/getUserBaseInfo", method = RequestMethod.POST, headers = "Accept=application/json")
    public Map<String, Object> getUserBaseInfo(@RequestBody Map<String, String> requestMap) {
        String username = requestMap.get("username") != null ? requestMap.get("username") : "";
        Map<String, Object> userMap = new HashMap<>();
        UserEntity userEntity = userMapper.getUser(username);
        userMap.put("id", userEntity.getId());
        userMap.put("username", userEntity.getUsername());
        userMap.put("avatar", userEntity.getAvatar());
        userMap.put("nickname", userEntity.getNickname());
        return userMap;
    }

    public static boolean isValidUsername(String username) {
        String regex = "^[a-zA-Z][a-zA-Z0-9_]{2,14}$";
        return username != null && username.matches(regex);
    }

    @CrossOrigin
    @ResponseBody
    @RequestMapping(value = "/register/check-username", method = RequestMethod.POST, headers = "Accept=application/json")
    public Result checkUsername(@RequestBody UserEntity requestUserEntity) {
        String username = requestUserEntity.getUsername();
        if (username == null || username.isEmpty()) {
            return new Result(402); // 用户名无效
        }
        // 对html标签进行转义，防止XSS攻击
        username = HtmlUtils.htmlEscape(username);

        if (isValidUsername(username)) {
            String existingUsername = userService.getUsername(username);
            if (existingUsername == null) {
                return new Result(200); // 用户名可用
            } else {
                return new Result(400); // 用户名已存在
            }
        } else {
            return new Result(402); // 用户名格式不正确
        }
    }

    @ResponseBody
    @RequestMapping(value = "/register/selectPhone", method = RequestMethod.POST, headers = "Accept=application/json")
    public Result selectPhone(@RequestBody UserEntity requestUserEntity) {
        String phone = requestUserEntity.getPhone();
        if (phone.isEmpty()) {
            return new Result(400);
        }
        // 对html标签进行转义，防止XSS攻击
        phone = HtmlUtils.htmlEscape(phone);
        return userService.selectPhone(phone) ? new Result(200) : new Result(400);
    }

    @ResponseBody
    @RequestMapping(value = "/register/selectEmail", method = RequestMethod.POST, headers = "Accept=application/json")
    public Result selectEmail(@RequestBody UserEntity requestUserEntity) {
        String email = requestUserEntity.getEmail();
        if (email.isEmpty()) {
            return new Result(400);
        }
        // 对html标签进行转义，防止XSS攻击
        email = HtmlUtils.htmlEscape(email);
        return userService.selectEmail(email) ? new Result(200) : new Result(400);
    }

    @CrossOrigin
    @ResponseBody
    @RequestMapping(value = "/register", method = RequestMethod.POST, headers = "Accept=application/json")
    public Result registerUser(@RequestBody UserEntity requestUserEntity) {
        // 获取用户名和密码
        String username = requestUserEntity.getUsername() != null ? requestUserEntity.getUsername() : "";
        String password = requestUserEntity.getPassword() != null ? requestUserEntity.getPassword() : "";
        String phone = requestUserEntity.getPhone() != null ? requestUserEntity.getPhone() : "";
        String email = requestUserEntity.getEmail() != null ? requestUserEntity.getEmail() : "";
        String defult_user_avatar = "https://oceanwave.oss-cn-beijing.aliyuncs.com/UserAvatar/defult.png?Expires=1731998110&OSSAccessKeyId=TMP.3KgkCe27FzPafbYtaUBrqR6MZmVonHa6mh4x2hmjmm4BETcJhYx58SHmjzWzccbkNxNK6cLp4XTkY1HPakSs2LrE2DdFV6&Signature=3Fgo5FiDBl9B9RfW5VqBBsgmpNI%3D";
        if (username.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            return new Result(400);
        }
        // 对html标签进行转义，防止XSS攻击
        username = HtmlUtils.htmlEscape(username);
        password = HtmlUtils.htmlEscape(password);
        phone = HtmlUtils.htmlEscape(phone);
        phone = HtmlUtils.htmlEscape(phone);
        // 判断用户名是否已存在
        String existingUsername = userService.getUsername(username);
        if (existingUsername != null) {
            return new Result(400);
        }
        // 判断手机号是否已存在
        boolean isPhone = userService.selectPhone(phone);
        if (isPhone) {
            return new Result(400);
        }
        // 判断邮箱是否已存在
        boolean isEmail = userService.selectEmail(email);
        if (isEmail) {
            return new Result(400);
        }
        String passwordMd5 = md5Util.passwordMD5(username, password);
        // 判断注册是否成功
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword(passwordMd5);
        userEntity.setPhone(phone);
        userEntity.setEmail(email);
        userEntity.setAvatar(defult_user_avatar);
        userService.register(userEntity);

        return new Result(200);
    }

    @CrossOrigin
    @ResponseBody
    @RequestMapping(value = "/forgetPassword", method = RequestMethod.POST, headers = "Accept=application/json")
    public Result forgetPassword(@RequestBody UserEntity requestUserEntity) {
        int id = 0;
        String password = requestUserEntity.getPassword() != null ? requestUserEntity.getPassword() : "";
        String phone = requestUserEntity.getPhone() != null ? requestUserEntity.getPhone() : "";
        String email = requestUserEntity.getEmail() != null ? requestUserEntity.getEmail() : "";
        String username = "";
        System.out.println("666" + password + " " + phone + " " + email);
        if (!password.isEmpty()) {
            password = HtmlUtils.htmlEscape(password);
            if (phone.isEmpty()) {
                email = HtmlUtils.htmlEscape(email);
                username = userMapper.getUsernameByEmail(email);
            } else if (email.isEmpty()) {
                phone = HtmlUtils.htmlEscape(phone);
                username = userMapper.getUsernameByPhone(phone);
            } else {
                return new Result(400);
            }
            if (username != null) {
                String passwordMd5 = md5Util.passwordMD5(username, password);
                String oldPassword = userService.getPassword(username);
                if (oldPassword.equals(passwordMd5)) {
                    return new Result(401);
                }
                UserEntity userEntity = new UserEntity();
                userEntity.setUsername(username);
                userEntity.setPassword(passwordMd5);
                return userService.changPassword(userEntity) == 1 ? new Result(200) : new Result(400);
            } else {
                return new Result(400);
            }
        }
        return new Result(400);
    }

    @CrossOrigin
    @ResponseBody
    @RequestMapping(value = "/bindAccount", method = RequestMethod.POST, headers = "Accept=application/json")
    public Result bindAccount(@RequestBody Map<String, Object> requestMap) {
        String username = requestMap.get("username") != null ? (String) requestMap.get("username") : "";
        String source = requestMap.get("source") != null ? (String) requestMap.get("source") : "";
        String thirdID = requestMap.get("thirdID") != null ? (String) requestMap.get("thirdID") : "";
        String thirdName = requestMap.get("thirdName") != null ? (String) requestMap.get("thirdName") : "";
        ThirdPartyEntity thirdPartyEntity = new ThirdPartyEntity();
        thirdPartyEntity.setSource(source);
        thirdPartyEntity.setId(thirdID);
        thirdPartyEntity.setName(thirdName);
        return userService.updateThirdParty(thirdPartyEntity, username) == 1 ? new Result(200) : new Result(400);
    }

    @CrossOrigin
    @ResponseBody
    @RequestMapping(value = "/upload-avatar", method = RequestMethod.POST, headers = "Accept=application/json")
    public Result uploadAvatar(@RequestParam("avatar") MultipartFile file, @RequestParam("username") String username) throws IOException {
        if (file.isEmpty()) {
            return new Result(400);
        }

        if (!file.getContentType().startsWith("image/")) {
            return new Result(400);
        }

        if (file.getSize() > 3 * 1024 * 1024) { // Limit to 3MB
            return new Result(400);
        }
        AliOSSUtil aliOSSUtil = new AliOSSUtil();
        String path = "UserAvatar";
        String result = aliOSSUtil.upload(file, path, username);
        if (result != null) {
            String avatarUrl = "https://oss.yy0313.fit/UserAvatar/" + username + ".jpg";
            return userMapper.updateAvatarUser(username, avatarUrl) == 1 ? new Result(200) : new Result(400);
        } else
            return new Result(400);
    }

    @CrossOrigin
    @ResponseBody
    @RequestMapping(value = "/findUserById", method = RequestMethod.POST, headers = "Accept=application/json")
    public Map<String, Object> findUserById(@RequestBody Map<String, Object> requestMap) {
        int id = (int) requestMap.get("id");
        String username = (String) requestMap.get("name");
        UserEntity user = userService.queryById(id, username);
        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("userName", user.getUsername());
        result.put("password", user.getPassword());
        result.put("phone", user.getPhone());
        result.put("email", user.getEmail());
        result.put("giteeid", user.getGiteeID());
        result.put("giteename", user.getGiteeName());
        result.put("githubid", user.getGitHubID());
        result.put("githubname", user.getGitHubName());
        result.put("googleid", user.getGoogleID());
        result.put("googlename", user.getGoogleName());
        result.put("wechatid", user.getWechatID());
        result.put("wechatname", user.getWechatName());
        return result;
    }

    @CrossOrigin
    @ResponseBody
    @RequestMapping(value = "/updateUserPhone", method = RequestMethod.POST, headers = "Accept=application/json")
    public Result updateUserPhone(@RequestBody Map<String, Object> requestMap) {
        int id = (int) requestMap.get("id");
        String name = (String) requestMap.get("name");
        String phone = (String) requestMap.get("phone");
        if (phone.isEmpty()) {
            return new Result(400);
        }
        // 对html标签进行转义，防止XSS攻击
        phone = HtmlUtils.htmlEscape(phone);
        boolean isPhone = userService.selectPhone(phone);
        if (isPhone) {
            String name_use = userMapper.getUsernameByPhone(phone);
            userService.updatePhone(name_use, null, id);
        }
        return userService.updatePhone(name, phone, id) == 1 ? new Result(200) : new Result(400);
    }

    @CrossOrigin
    @ResponseBody
    @RequestMapping(value = "/updateUserEmail", method = RequestMethod.POST, headers = "Accept=application/json")
    public Result updateUserEmail(@RequestBody Map<String, Object> requestMap) {
        int id = (int) requestMap.get("id");
        String name = (String) requestMap.get("name");
        String email = (String) requestMap.get("email");
        if (email.isEmpty()) {
            return new Result(400);
        }
        // 对html标签进行转义，防止XSS攻击
        email = HtmlUtils.htmlEscape(email);
        boolean isEmail = userService.selectEmail(email);
        if (isEmail) {
            String name_use = userMapper.getUsernameByEmail(email);
            userService.updateEmail(name_use, null, id);
        }
        return userService.updateEmail(name, email, id) == 1 ? new Result(200) : new Result(400);
    }

    @CrossOrigin
    @ResponseBody
    @RequestMapping(value = "/unbindAccount", method = RequestMethod.POST, headers = "Accept=application/json")
    public Result unbindAccount(@RequestBody Map<String, Object> requestMap) {
        String source = (String) requestMap.get("platform");
        String username = (String) requestMap.get("username");
        ThirdPartyEntity thirdPartyEntity = new ThirdPartyEntity();
        thirdPartyEntity.setSource(source);
        return userService.updateThirdParty(thirdPartyEntity, username) == 1 ? new Result(200) : new Result(400);
    }

    @CrossOrigin
    @ResponseBody
    @RequestMapping(value = "/getUserOtherInfo", method = RequestMethod.POST, headers = "Accept=application/json")
    public Map<String, Object> getUserOtherInfo(@RequestBody Map<String, Object> requestMap) {
        int id = (int) requestMap.get("id");
        String username = (String) requestMap.get("name");
        UserEntity user = userService.queryById(id, username);
        Map<String, Object> result = new HashMap<>();
        result.put("changTime", user.getNicknamechangtime());
        result.put("gender", user.getGender());
        return result;
    }

    @CrossOrigin
    @ResponseBody
    @RequestMapping(value = "/updateUserOtherInfo", method = RequestMethod.POST, headers = "Accept=application/json")
    public Result updateUserOtherInfo(@RequestBody Map<String, Object> requestMap) {
        int id = (int) requestMap.get("id");
        String username = (String) requestMap.get("name");
        String gender = (String) requestMap.get("gender");
        String nickname = (String) requestMap.get("nickname");
        boolean isTrue = SensitiveWordHelper.contains(nickname);
        if (isTrue) {
            return new Result(401);
        }
        return userService.updateUserOtherInfo(id, username, gender, nickname) == 1 ? new Result(200) : new Result(400);
    }
}
