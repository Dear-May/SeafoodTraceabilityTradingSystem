package com.shopping_c_backend.shoppping_c_backend.Mapper;

import com.shopping_c_backend.shoppping_c_backend.Entity.FootMarkEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface FootMarkMapper {
    // 查询
    @Select("select * from footmarkinfo where userid = #{userId} order by time desc")
    List<FootMarkEntity> findAllFootMark(int userId);

    @Select("select id from footmarkinfo where userid = #{userId}  and goodid = #{goodId}")
    Integer findFootMarkId(@Param("userId") int userId, @Param("goodId") String goodId);

    @Select("select * from footmarkinfo where userid = #{userId} order by count desc limit 7")
    List<FootMarkEntity> findFootMarkByUserCount(int userId);

    // 新增
    @Insert("insert into footmarkinfo(userid, goodid, time) values(#{userId},#{goodId},now())")
    int insertFootMark(@Param("userId") int userId, @Param("goodId") String goodId);

    // 更新
    @Update("update footmarkinfo set time = now(),count = count + 1 where userid = #{userId} and goodid = #{goodId}")
    int updateFootMark(@Param("userId") int userId, @Param("goodId") String goodId);

    // 删除
    @Delete("delete from footmarkinfo where userid = #{userId} and goodid = #{goodId}")
    int deleteFootMark(@Param("userId") int userId, @Param("goodId") String goodId);
}
