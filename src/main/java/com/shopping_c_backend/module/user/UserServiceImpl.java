package com.shopping_c_backend.module.user;

import com.shopping_c_backend.common.cache.CacheService;
import com.shopping_c_backend.common.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Refactored: Eliminates duplicated cache hasKey+get+set pattern.
 * Uses CacheService.getOrSet() and CacheService.refresh() instead.
 */
@Service
public class UserServiceImpl implements UserService {

    private static final long CACHE_HOURS = 5;
    private static final String CACHE_PREFIX = "user:";

    @Resource
    private UserMapper userMapper;
    @Resource
    private CacheService cacheService;

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public UserEntity queryAll() {
        return userMapper.getAllUsers();
    }

    @Override
    public UserEntity queryById(int id, String username) {
        String key = CACHE_PREFIX + username + ":info";
        return cacheService.getOrSet(key, UserEntity.class,
                () -> userMapper.getUserById(id),
                CACHE_HOURS, TimeUnit.HOURS);
    }

    @Override
    public String getUsername(String username) {
        return userMapper.getUsername(username);
    }

    @Override
    public boolean selectPhone(String phone) {
        return userMapper.getUsernameByPhone(phone) != null;
    }

    @Override
    public boolean selectEmail(String email) {
        return userMapper.getUsernameByEmail(email) != null;
    }

    @Override
    public String getPassword(String username) {
        String key = CACHE_PREFIX + username + ":pwd";
        return cacheService.getOrSet(key, String.class,
                () -> userMapper.getPassword(username),
                CACHE_HOURS, TimeUnit.HOURS);
    }

    @Override
    public String WriteThirdLogin(ThirdPartyEntity thirdPartyEntity) {
        String jwtToken = TokenUtil.token(thirdPartyEntity.getId(), thirdPartyEntity.getSource());
        String key = "third_" + jwtToken;
        cacheService.set(key, thirdPartyEntity, CACHE_HOURS, TimeUnit.HOURS);
        return jwtToken;
    }

    @Override
    public ThirdPartyEntity getThirdPartyEntity(String token) {
        if (TokenUtil.verify(token)) {
            String key = "third_" + token;
            return cacheService.get(key, ThirdPartyEntity.class);
        }
        return null;
    }

    @Override
    public String getAvatar(String username, int id) {
        String key = CACHE_PREFIX + username + ":avatar";
        return cacheService.getOrSet(key, String.class,
                () -> userMapper.getUserById(id).getAvatar(),
                CACHE_HOURS, TimeUnit.HOURS);
    }

    @Override
    public int register(String username, String password, String phone, String email) {
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(password);
        user.setPhone(phone);
        user.setEmail(email);
        return userMapper.register(user);
    }

    @Override
    public int updatePassword(String username, String newPassword) {
        int result = userMapper.updatePassword(newPassword, username);
        if (result > 0) {
            cacheService.delete(CACHE_PREFIX + username + ":pwd");
        }
        return result;
    }

    @Override
    public int updatePhone(String username, String phone, int id) {
        int result = userMapper.updatePhoneUser(phone, username);
        if (result > 0) {
            cacheService.delete(CACHE_PREFIX + username + ":info");
            cacheService.refresh(CACHE_PREFIX + username + ":info",
                    userMapper.getUserById(id), CACHE_HOURS, TimeUnit.HOURS);
        }
        return result;
    }

    @Override
    public int updateEmail(String username, String email, int id) {
        int result = userMapper.updateEmailUser(email, username);
        if (result > 0) {
            cacheService.delete(CACHE_PREFIX + username + ":info");
            cacheService.refresh(CACHE_PREFIX + username + ":info",
                    userMapper.getUserById(id), CACHE_HOURS, TimeUnit.HOURS);
        }
        return result;
    }

    @Override
    public int updateThirdParty(ThirdPartyEntity thirdPartyEntity, String username) {
        int result = 0;
        switch (thirdPartyEntity.getSource()) {
            case "Gitee":
                userMapper.updateGiteeUser(thirdPartyEntity.getId(), thirdPartyEntity.getName(), username);
                result = 1;
                break;
            case "Github":
                userMapper.updateGitHubUser(thirdPartyEntity.getId(), thirdPartyEntity.getName(), username);
                result = 1;
                break;
            case "Google":
                userMapper.updateGoogleUser(thirdPartyEntity.getId(), thirdPartyEntity.getName(), username);
                result = 1;
                break;
            case "Wechat":
                userMapper.updateWechatUser(thirdPartyEntity.getId(), username);
                result = 1;
                break;
        }
        if (result > 0) {
            String dbUsername = userMapper.getUsername(username);
            String key = CACHE_PREFIX + dbUsername + ":info";
            cacheService.delete(key);
            int id = userMapper.getUserId(dbUsername);
            cacheService.refresh(key, userMapper.getUserById(id), CACHE_HOURS, TimeUnit.HOURS);
        }
        return result;
    }

    @Override
    public int updateUserOtherInfo(int id, String username, String gender, String nickname) {
        UserEntity user = queryById(id, username);
        int result;
        if (user.getNickname() != null && user.getNickname().equals(nickname)) {
            result = userMapper.updateGenderUser(id, gender);
        } else {
            result = userMapper.updateNicknameGenderUser(id, nickname, gender);
        }
        if (result > 0) {
            String key = CACHE_PREFIX + username + ":info";
            cacheService.delete(key);
            cacheService.refresh(key, userMapper.getUserById(id), CACHE_HOURS, TimeUnit.HOURS);
        }
        return result;
    }

    @Override
    public boolean isValidUsername(String username) {
        String regex = "^[a-zA-Z][a-zA-Z0-9_]{2,14}$";
        return username != null && username.matches(regex);
    }
}
