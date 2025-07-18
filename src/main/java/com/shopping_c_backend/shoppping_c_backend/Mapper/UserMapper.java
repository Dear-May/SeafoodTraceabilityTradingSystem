package com.shopping_c_backend.shoppping_c_backend.Mapper;

import com.shopping_c_backend.shoppping_c_backend.Entity.UserEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface UserMapper {

    // 查询
    @Select("select * from test")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "username", column = "name"),
    })
    UserEntity getAllUsers();

    @Select("select * from userinfo where id = #{id}")
    UserEntity getUserById(int id);

    @Select("select * from userinfo where name = #{username} or phone=#{username} or email=#{username}")
    @Results({
            @Result(property = "username", column = "name")
    })
    UserEntity getUser(String username);

    @Select("select id from userinfo where name = #{username} or phone=#{username} or email=#{username}")
    int getUserId(String username);

    @Select("select id from userinfo where giteeid = #{giteeid}")
    String getUserIdByGiteeid(String giteeid);

    @Select("select id from userinfo where githubid = #{githubid}")
    String getUserIdByGithubid(String githubid);

    @Select("select id from userinfo where googleid = #{googleid}")
    String getUserIdByGoogleid(String googleid);

    @Select("select id from userinfo where wechatid = #{wechatid}")
    String getUserIdByWechatid(String wechatid);

    @Select("select name from userinfo where id = #{id}")
    String getUserNameById(int id);

    @Select("select name from userinfo where name=#{username} or phone=#{username} or email=#{username}")
    String getUsername(String username);

    @Select("select name from userinfo where phone = #{phone}")
    String getUsernameByPhone(String phone);

    @Select("select name from userinfo where email = #{email}")
    String getUsernameByEmail(String email);

    @Select("select name from userinfo where giteeid = #{giteeid}")
    String getUserNameByGiteeid(String giteeid);

    @Select("select name from userinfo where githubid = #{githubid}")
    String getUserNameByGithubid(String githubid);

    @Select("select name from userinfo where googleid = #{googleid}")
    String getUserNameByGoogleid(String googleid);

    @Select("select name from userinfo where wechatid = #{wechatid}")
    String getUserNameByWechatid(String wechatid);

    @Select("select password from userinfo where name = #{username} or phone=#{username} or email=#{username}")
    String getPassword(String username);

    @Select("select avatar from userinfo where id = #{id}")
    String getAvatar(int id);

    // 新增
    @Insert("insert into userinfo(name, password, phone, email, nickname,avatar) values(#{username}, #{password}, #{phone}, #{email},#{username},#{avatar})")
    void insertUser(@Param("username") String username, @Param("password") String password, @Param("phone") String phone, @Param("email") String email, @Param("avatar") String default_user_avatar);

    // 更新
    @Update("update userinfo set password = #{password} where name = #{username}")
    int updatePassword(@Param("username") String username, @Param("password") String password);

    @Update("update userinfo set giteeid = #{giteeid},giteename=#{giteename} where name = #{username} or email=#{username} or phone=#{username}")
    int updateGiteeUser(@Param("giteeid") String giteeid, @Param("giteename") String giteename, @Param("username") String username);

    @Update("update userinfo set githubid = #{githubid},githubname=#{githubname} where name = #{username} or email=#{username} or phone=#{username}")
    int updateGitHubUser(@Param("githubid") String githubid, @Param("githubname") String githubname, @Param("username") String username);

    @Update("update userinfo set googleid = #{googleid},googlename=#{googlename} where name = #{username} or email=#{username} or phone=#{username}")
    int updateGoogleUser(@Param("googleid") String googleid, @Param("googlename") String googlename, @Param("username") String username);

    @Update("update userinfo set wechatid = #{wechatid} where name = #{username} or email=#{username} or phone=#{username}")
    int updateWechatUser(@Param("wechatid") String wechatid, @Param("username") String username);

    @Update("update userinfo set phone = #{phone} where name = #{username}")
    int updatePhoneUser(@Param("phone") String phone, @Param("username") String username);

    @Update("update userinfo set email = #{email} where name = #{username}")
    int updateEmailUser(@Param("email") String email, @Param("username") String username);

    @Update("update userinfo set gender = #{gender} where id = #{id}")
    int updateGenderUser(@Param("id") int id, @Param("gender") String gender);

    @Update("update userinfo set nickname = #{nickname},gender = #{gender},nicknamechangtime = now() where id = #{id}")
    int updateNicknameGenderUser(@Param("id") int id, @Param("nickname") String nickname, @Param("gender") String gender);

    @Update("update userinfo set avatar = #{avatar} where name = #{username}")
    int updateAvatarUser(@Param("username") String username, @Param("avatar") String avatar);
    // 删除
}
