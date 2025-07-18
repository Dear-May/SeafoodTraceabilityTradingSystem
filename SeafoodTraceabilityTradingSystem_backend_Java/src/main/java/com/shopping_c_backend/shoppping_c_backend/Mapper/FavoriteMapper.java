package com.shopping_c_backend.shoppping_c_backend.Mapper;

import com.shopping_c_backend.shoppping_c_backend.Entity.FavoriteEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface FavoriteMapper {
    // 查询
    @Select("select * from favoriteinfo where userid = #{userId} and goodid = #{goodId}")
    FavoriteEntity getFavoriteInfo(@Param("userId") int userId, @Param("goodId") String goodId);

    @Select("select goodid from favoriteinfo where userid = #{userId}")
    List<String> getFavoriteInfoByUserId(@Param("userId") int userId);

    @Select("select * from favoriteinfo where userid = #{userId}")
    List<FavoriteEntity> getFavoriteInfoByUserIdEntity(@Param("userId") int userId);

    // 新增
    @Insert("insert into favoriteinfo(userid, goodid, time) values(#{userid}, #{goodid}, now())")
    int insertFavoriteInfo(FavoriteEntity favoriteEntity);

    // 删除
    @Delete("delete from favoriteinfo where userid = #{userId} and goodid = #{goodId}")
    int deleteFavoriteInfoByUserId(@Param("userId") int userId, @Param("goodId") String goodId);

}
