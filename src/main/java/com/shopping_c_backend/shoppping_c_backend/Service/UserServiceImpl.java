package com.shopping_c_backend.shoppping_c_backend.Service;

import com.shopping_c_backend.shoppping_c_backend.Entity.ThirdPartyEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.UserEntity;
import com.shopping_c_backend.shoppping_c_backend.Mapper.UserMapper;
import com.shopping_c_backend.shoppping_c_backend.Util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl {
    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisTemplate redisTemplate;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserEntity queryAll() {
        return userMapper.getAllUsers();
    }

    /**
     * 获取用户策略:先从缓存中获取用户，没有则读mysql数据，再将数据写入缓存
     */
    public UserEntity queryById(int id, String name) {
        String key = "user_" + name + "_info";
        ValueOperations<String, UserEntity> operations = redisTemplate.opsForValue();
        //判断redis中是否有键为key的缓存
        boolean hasKey = redisTemplate.hasKey(key);
        UserEntity user;
        if (hasKey) {
            user = operations.get(key);
            logger.info("从缓存中获取数据:{}", user.getUsername());
        } else {
            user = userMapper.getUserById(id);
            logger.info("查询数据库获取数据:{}", user.getUsername());
            //写入缓存
            operations.set(key, user, 5, TimeUnit.HOURS);
        }
        return user;
    }

    public String getUsername(String username) {
        String result = userMapper.getUsername(username);
        return result;
    }

    public boolean selectPhone(String phone) {
        String phone_name = userMapper.getUsernameByPhone(phone);
        return phone_name != null;
    }

    public boolean selectEmail(String email) {
        String email_name = userMapper.getUsernameByEmail(email);
        return email_name != null;
    }

    public String getPassword(String username) {
        String key = "user_" + username;
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        //判断redis中是否有键为key的缓存
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            String password = operations.get(key);
            logger.info("从缓存中获取数据:{}", password);
            return password;
        } else {
            String password = userMapper.getPassword(username);
            logger.info("查询数据库获取数据:{}", password);
            logger.info("------------写入缓存-");
            //写入缓存
            operations.set(key, password, 5, TimeUnit.HOURS);
            return password;
        }
    }

    public String WriteThirdLogin(ThirdPartyEntity thirdPartyEntity) {
        String jwtToken = TokenUtil.token(thirdPartyEntity.getId(), thirdPartyEntity.getSource());
        String key = "third_" + jwtToken;
        ValueOperations<String, ThirdPartyEntity> operations = redisTemplate.opsForValue();
        if (jwtToken != null) {
            operations.set(key, thirdPartyEntity, 5, TimeUnit.HOURS);
            logger.info("写入缓存成功:{}", key);
        }
        return jwtToken;
    }

    public ThirdPartyEntity getThirdPartyEntity(String token) {
        //验证token
        if (TokenUtil.verify(token)) {
            String key = "third_" + token;
            ValueOperations<String, ThirdPartyEntity> operations = redisTemplate.opsForValue();
            boolean hasKey = redisTemplate.hasKey(key);
            if (hasKey) {
                ThirdPartyEntity thirdPartyEntity = operations.get(key);
                logger.info("从缓存中获取数据:{}", thirdPartyEntity.getSource());
                return thirdPartyEntity;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public String getAvatar(String username, int id) {
        String key = "user_" + username + "_avatar";
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        //判断redis中是否有键为key的缓存
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            logger.info("从缓存中获取数据:{}", key);
            return operations.get(key);
        } else {
            String avatar = userMapper.getAvatar(id);
            logger.info("查询数据库获取数据:{}", avatar);
            logger.info("------------写入缓存-");
            //写入缓存
            operations.set(key, avatar, 5, TimeUnit.HOURS);
            return avatar;
        }

    }

    //注册用户策略:将用户信息写入数据表
    public void register(UserEntity user) {
        if (user == null) {
            logger.error("用户信息不能为空");
            return;
        }

        String name = user.getUsername() != null ? user.getUsername() : "未知用户";
        String phone = user.getPhone() != null ? user.getPhone() : null;
        String email = user.getEmail() != null ? user.getEmail() : null;
        String password = user.getPassword() != null ? user.getPassword() : "无密码";
        String avatar = user.getAvatar() != null ? user.getAvatar() : null;

        userMapper.insertUser(name, password, phone, email, avatar);
    }


    //删除用户策略:删除数据表中数据，然后删除缓存中的数据

    /**
     * 更新用户策略：先更新数据表，成功之后，删除原来的缓存，再更新缓存
     */
    public int changPassword(UserEntity user) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        int result = userMapper.updatePassword(user.getUsername(), user.getPassword());
        if (result != 0) {
            String key = "user_" + user.getUsername();
            boolean haskey = redisTemplate.hasKey(key);
            if (haskey) {
                redisTemplate.delete(key);
                logger.info("删除缓存中的key:{}", key);
            }
            // 再将更新后的数据加入缓存
            operations.set(key, user.getPassword(), 5, TimeUnit.HOURS);
            int id = userMapper.getUserId(user.getUsername());
            UserEntity userEntity = userMapper.getUserById(id);
            String key1 = "user_" + user.getUsername() + "_info";
            operations.set(key1, userEntity, 5, TimeUnit.HOURS);
            logger.info("更新缓存中的数据:{}", key1);
        }
        return result;
    }

    public int updatePhone(String username, String phone, int id) {
        ValueOperations<String, UserEntity> operations = redisTemplate.opsForValue();
        int result = userMapper.updatePhoneUser(phone, username);
        if (result != 0) {
            String key = "user_" + username + "_info";
            boolean haskey = redisTemplate.hasKey(key);
            if (haskey) {
                redisTemplate.delete(key);
                logger.info("删除缓存中的key:{}", key);
            }
            // 再将更新后的数据加入缓存
            UserEntity user = userMapper.getUserById(id);
            operations.set(key, user, 5, TimeUnit.HOURS);
            logger.info("更新缓存中的数据:{}", key);
        }
        return result;
    }

    public int updateEmail(String username, String email, int id) {
        ValueOperations<String, UserEntity> operations = redisTemplate.opsForValue();
        int result = userMapper.updateEmailUser(email, username);
        if (result != 0) {
            String key = "user_" + username + "_info";
            boolean haskey = redisTemplate.hasKey(key);
            if (haskey) {
                redisTemplate.delete(key);
                logger.info("删除缓存中的key:{}", key);
            }
            // 再将更新后的数据加入缓存
            UserEntity user = userMapper.getUserById(id);
            operations.set(key, user, 5, TimeUnit.HOURS);
            logger.info("更新缓存中的数据:{}", key);
        }
        return result;
    }

    public int updateThirdParty(ThirdPartyEntity thirdPartyEntity, String username) {
        ValueOperations<String, UserEntity> operations = redisTemplate.opsForValue();
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
            case "微信":
            case "Wechat":
                userMapper.updateWechatUser(thirdPartyEntity.getId(), username);
                result = 1;
                break;
        }
        if (result != 0) {
            username = userMapper.getUsername(username);
            String key = "user_" + username + "_info";
            boolean haskey = redisTemplate.hasKey(key);
            if (haskey) {
                redisTemplate.delete(key);
                logger.info("删除缓存中的key:{}", key);
            }
            // 再将更新后的数据加入缓存
            int id = userMapper.getUserId(username);
            UserEntity user = userMapper.getUserById(id);
            operations.set(key, user, 5, TimeUnit.HOURS);
            logger.info("更新缓存中的数据:{}", key);
            return 1;
        } else {
            return 0;
        }
    }

    public int updateUserOtherInfo(int id, String username, String gender, String nickname) {
        UserEntity user = queryById(id, username);
        int result;
        if (user.getNickname().equals(nickname)) {
            result = userMapper.updateGenderUser(id, gender);
        } else {
            result = userMapper.updateNicknameGenderUser(id, nickname, gender);
        }
        if (result != 0) {
            String key = "user_" + username + "_info";
            boolean haskey = redisTemplate.hasKey(key);
            if (haskey) {
                redisTemplate.delete(key);
                logger.info("删除缓存中的key:{}", key);
            }
            // 再将更新后的数据加入缓存
            UserEntity user1 = userMapper.getUserById(id);
            ValueOperations<String, UserEntity> operations = redisTemplate.opsForValue();
            operations.set(key, user1, 5, TimeUnit.HOURS);
        }
        return result;
    }
}
