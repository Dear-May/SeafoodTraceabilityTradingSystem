package com.shopping_c_backend.module.user;

import com.shopping_c_backend.module.user.ThirdPartyEntity;
import com.shopping_c_backend.module.user.UserEntity;
import java.util.List;
import java.util.Map;

public interface UserService {
    UserEntity queryAll();
    UserEntity queryById(int id, String name);
    String getUsername(String username);
    boolean selectPhone(String phone);
    boolean selectEmail(String email);
    String getPassword(String username);
    String WriteThirdLogin(ThirdPartyEntity thirdPartyEntity);
    ThirdPartyEntity getThirdPartyEntity(String token);
    String getAvatar(String username, int id);
    int updatePhone(String username, String phone, int id);
    int updateEmail(String username, String email, int id);
    int updateThirdParty(ThirdPartyEntity thirdPartyEntity, String username);
    int updateUserOtherInfo(int id, String username, String gender, String nickname);
    boolean isValidUsername(String username);
}
