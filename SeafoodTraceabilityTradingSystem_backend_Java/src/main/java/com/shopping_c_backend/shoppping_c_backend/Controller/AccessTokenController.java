package com.shopping_c_backend.shoppping_c_backend.Controller;

import com.shopping_c_backend.shoppping_c_backend.Component.GitHubProvider;
import com.shopping_c_backend.shoppping_c_backend.Component.GiteeProvider;
import com.shopping_c_backend.shoppping_c_backend.Component.GoogleProvider;
import com.shopping_c_backend.shoppping_c_backend.Constants.GitHubConstants;
import com.shopping_c_backend.shoppping_c_backend.Constants.GiteeConstants;
import com.shopping_c_backend.shoppping_c_backend.Constants.GoogleConstants;
import com.shopping_c_backend.shoppping_c_backend.Entity.*;
import com.shopping_c_backend.shoppping_c_backend.Mapper.UserMapper;
import com.shopping_c_backend.shoppping_c_backend.Service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/callback")
public class AccessTokenController {
    private final GiteeConstants giteeConstants;
    private final GitHubConstants gitHubConstants;
    private final GoogleConstants googleConstants;
    @Resource
    private GiteeProvider giteeProvider;
    @Resource
    private GitHubProvider gitHubProvider;
    @Resource
    private GoogleProvider googleProvider;
    @Resource
    private UserMapper userMapper;
    @Resource
    @Lazy
    private UserServiceImpl userService;
    String uuid = UUID.randomUUID().toString().replaceAll("-", "");

    @Autowired
    public AccessTokenController(GiteeConstants giteeConstants, GitHubConstants gitHubConstants, GoogleConstants googleConstants) {
        this.giteeConstants = giteeConstants;
        this.gitHubConstants = gitHubConstants;
        this.googleConstants = googleConstants;
    }

    @GetMapping("/gitee")
    public String giteeLogin(@RequestParam("code") String code,
                             @RequestParam("state") String state
    ) {
        //用户授权信息
        ProviderToken token = new ProviderToken();
        token.setClientId(giteeConstants.getAppID());
        token.setRedirectUri(giteeConstants.getRedirectURI());
        token.setClientSecret(giteeConstants.getAppKEY());
        token.setCode(code);
        token.setState(state);
        //获取token和登录的用户信息
        String accessToken = giteeProvider.getGiteeToken(token);
        GiteeUserEntity giteeUserEntity = giteeProvider.getGiteeUser(accessToken);
        System.out.println(giteeUserEntity.toString());
        if (userMapper.getUserIdByGiteeid(giteeUserEntity.getId()) == null) {
            ThirdPartyEntity thirdPartyEntity = createThirdPartyEntity(giteeUserEntity);
            String jwtToken = userService.WriteThirdLogin(thirdPartyEntity);
            return "redirect:http://localhost:8081/accessLogin?token=" + jwtToken;
        } else {
            return "redirect:http://localhost:8081/accessResult?id=" + giteeUserEntity.getId() + "&source=gitee";
        }
    }

    @ResponseBody
    @RequestMapping(value = "/gitee-auth", method = RequestMethod.POST, headers = "Accept=application/json")
    public Map<String, String> giteeAuth() {
        String giteeAuthUrl = giteeConstants.getAuthorizeURL() + "?client_id=" + giteeConstants.getAppID() +
                "&redirect_uri=" + giteeConstants.getRedirectURI() +
                "&response_type=code&state=" + uuid;

        Map<String, String> response = new HashMap<>();
        response.put("url", giteeAuthUrl);
        return response;
    }

    @GetMapping("/github")
    public String githubLogin(@RequestParam("code") String code,
                              @RequestParam("state") String state,
                              Model model) {
        //用户授权信息
        ProviderToken token = new ProviderToken();
        token.setClientId(gitHubConstants.getAppID());
        token.setRedirectUri(gitHubConstants.getRedirectURI());
        token.setClientSecret(gitHubConstants.getAppKEY());
        token.setCode(code);
        token.setState(state);
        //获取token和登录的用户信息
        String accessToken = gitHubProvider.getGitHubToken(token);
        GitHubUserEntity gitHubUserEntity = gitHubProvider.getGitHubUser(accessToken);
        System.out.println(gitHubUserEntity.toString());
        if (userMapper.getUserIdByGithubid(gitHubUserEntity.getId()) == null) {
            ThirdPartyEntity thirdPartyEntity = createThirdPartyEntity(gitHubUserEntity);
            String jwtToken = userService.WriteThirdLogin(thirdPartyEntity);
            return "redirect:http://localhost:8081/accessLogin?token=" + jwtToken;
        } else {
            return "redirect:http://localhost:8081/accessResult?id=" + gitHubUserEntity.getId() + "&source=github";
        }
    }

    @ResponseBody
    @RequestMapping(value = "/github-auth", method = RequestMethod.POST, headers = "Accept=application/json")
    public Map<String, String> githubAuth() {
        String gitHubAuthUrl = gitHubConstants.getAuthorizeURL() + "?client_id=" + gitHubConstants.getAppID() +
                "&redirect_uri=" + gitHubConstants.getRedirectURI() +
                "&response_type=code&state=" + uuid;

        Map<String, String> response = new HashMap<>();
        response.put("url", gitHubAuthUrl);
        return response;
    }

    @GetMapping("/google")
    public String googleLogin(@RequestParam("code") String code,
                              @RequestParam("state") String state,
                              Model model) {
        //用户授权信息
        ProviderToken token = new ProviderToken();
        token.setClientId(googleConstants.getAppID());
        token.setRedirectUri(googleConstants.getRedirectURI());
        token.setClientSecret(googleConstants.getAppKEY());
        token.setCode(code);
        token.setState(state);
        //获取token和登录的用户信息
        String accessToken = googleProvider.getGoogleToken(token);
        GoogleUserEntity googleUserEntity = googleProvider.getGoogleUser(accessToken);
        System.out.println(googleUserEntity.toString());
        if (userMapper.getUserIdByGoogleid(googleUserEntity.getId()) == null) {
            ThirdPartyEntity thirdPartyEntity = createThirdPartyEntity(googleUserEntity);
            String jwtToken = userService.WriteThirdLogin(thirdPartyEntity);
            return "redirect:http://localhost:8081/accessLogin?token=" + jwtToken;
        } else {
            return "redirect:http://localhost:8081/accessResult?id=" + googleUserEntity.getId() + "&source=google";
        }
    }

    @ResponseBody
    @RequestMapping(value = "/google-auth", method = RequestMethod.POST, headers = "Accept=application/json")
    public Map<String, String> googleAuth() {
        String googleAuthUrl = googleConstants.getAuthorizeURL() +
                "?client_id=" + googleConstants.getAppID() +
                "&redirect_uri=" + googleConstants.getRedirectURI() +
                "&response_type=code&state=" + uuid +
                "&access_type=offline&prompt=consent&scope=https://www.googleapis.com/auth/userinfo.profile";

        Map<String, String> response = new HashMap<>();
        response.put("url", googleAuthUrl);
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/ThirdLogin/userInfo", method = {RequestMethod.POST, RequestMethod.GET})
    public ThirdPartyEntity thirdLogin(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            System.out.println("Authorization header is missing or invalid");
            return null; // Token 缺失或格式错误
        }
        ThirdPartyEntity thirdPartyEntity = new ThirdPartyEntity();
        try {
            // 提取 Token 并去掉 "Bearer " 前缀
            String token = authorizationHeader.substring(7);
            thirdPartyEntity = userService.getThirdPartyEntity(token);
            if (thirdPartyEntity != null) {
                return thirdPartyEntity;
            }
        } catch (Exception e) {
            System.out.println("Failed to extract token from Authorization header");
            return null; // Token 解析失败
        }
        return null;
    }

    @CrossOrigin
    @ResponseBody
    @RequestMapping(value = "/ThirdLogin/getUserBaseInfo", method = RequestMethod.POST, headers = "Accept=application/json")
    public Map<String, String> userBaseInfo(@RequestBody Map<String, String> requestMap) {
        Map<String, String> response = new HashMap<>();
        String source = requestMap.get("source");
        String id = requestMap.get("id");
        switch (source) {
            case "gitee":
                response.put("username", userMapper.getUserNameByGiteeid(id));
                break;
            case "github":
                response.put("username", userMapper.getUserNameByGithubid(id));
                break;
            case "google":
                response.put("username", userMapper.getUserNameByGoogleid(id));
                break;
            case "wechat":
                response.put("username", userMapper.getUserNameByWechatid(id));
                break;
            default:
                response.put("username", "unknown");
                break;
        }
        return response;
    }

    /**
     * 根据第三方用户对象创建 ThirdPartyEntity。
     * 该方法可复用，避免重复代码。
     */
    private ThirdPartyEntity createThirdPartyEntity(GiteeUserEntity user) {
        ThirdPartyEntity thirdPartyEntity = new ThirdPartyEntity();
        thirdPartyEntity.setSource(user.getSource());
        thirdPartyEntity.setId(user.getId());
        thirdPartyEntity.setName(user.getName());
        return thirdPartyEntity;
    }

    private ThirdPartyEntity createThirdPartyEntity(GitHubUserEntity user) {
        ThirdPartyEntity thirdPartyEntity = new ThirdPartyEntity();
        thirdPartyEntity.setSource(user.getSource());
        thirdPartyEntity.setId(user.getId());
        thirdPartyEntity.setName(user.getLogin());
        return thirdPartyEntity;
    }

    private ThirdPartyEntity createThirdPartyEntity(GoogleUserEntity user) {
        ThirdPartyEntity thirdPartyEntity = new ThirdPartyEntity();
        thirdPartyEntity.setSource(user.getSource());
        thirdPartyEntity.setId(user.getId());
        thirdPartyEntity.setName(user.getName());
        return thirdPartyEntity;
    }


}
