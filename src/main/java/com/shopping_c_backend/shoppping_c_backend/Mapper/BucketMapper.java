package com.shopping_c_backend.shoppping_c_backend.Mapper;

import com.shopping_c_backend.shoppping_c_backend.Entity.BucketEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface BucketMapper {
    // 查询
    @Select("select count(*) from bucketinfo where userid = #{usdrId}")
    int countByUserId(@Param("usdrId") int userId);

    @Select("select * from bucketinfo where userid = #{userId} order by updatetime desc")
    List<BucketEntity> selectByUserId(@Param("userId") int userId);

    @Select("select * from bucketinfo where userid = #{userId} and goodid = #{goodId} and specificationid = #{specificationId}")
    BucketEntity selectByGoodIdAndSpecificationId(@Param("userId") int userId, @Param("goodId") String goodId, @Param("specificationId") String specificationId);

    @Select("select * from bucketinfo where bucketid = #{bucketId}")
    BucketEntity selectByBucketId(@Param("bucketId") int bucketId);

    // 插入
    @Insert("insert into bucketinfo(userid, goodid, specificationid, number, inserttime, updatetime) values(#{userid}, #{goodid}, #{specificationid}, #{number}, now(), now())")
    int insert(BucketEntity bucketEntity);

    // 更新
    @Update("update bucketinfo set number = #{number}, updatetime = now() where userid = #{userid} and goodid = #{goodid} and specificationid = #{specificationid}")
    int update(@Param("userid") int userId, @Param("goodid") String goodId, @Param("specificationid") String specificationId, @Param("number") int number);

    @Update("update bucketinfo set number = #{number},specificationid = #{newSpecificationid} ,updatetime = now() where userid = #{userid} and goodid = #{goodid} and specificationid = #{specificationid}")
    int updateOldSpecificationId(@Param("userid") int userId, @Param("goodid") String goodId, @Param("specificationid") String specificationId, @Param("number") int number, @Param("newSpecificationid") String newSpecificationId);

    @Update("update bucketinfo set number = #{number}, updatetime = now() where bucketid = #{bucketid}")
    int updateNumber(@Param("bucketid") int bucketid, @Param("number") int number);

    // 删除
    @Delete("delete from bucketinfo where userid = #{userid} and goodid = #{goodid} and specificationid = #{specificationid}")
    int delete(BucketEntity bucketEntity);

    @Delete("delete from bucketinfo where bucketid = #{bucketid}")
    int deleteByBucketId(@Param("bucketid") int bucketid);
}
