package com.shopping_c_backend.shoppping_c_backend.Mapper;

import com.shopping_c_backend.shoppping_c_backend.Entity.AdminTokenEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.ReviewLicenseEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface AdminMapper {
    @Select("select * from admintoken where token = #{token}")
    AdminTokenEntity getAdminToken(@Param("token") String token);

    @Select("select * from reviewlicense where status = '审核中'")
    List<ReviewLicenseEntity> getReviewLicenses();

    @Select("select * from reviewlicense where id = #{id}")
    ReviewLicenseEntity getReviewLicenseById(@Param("id") String id);

    @Update("update reviewlicense set status = #{status}, updatetime = now(),mangerid = #{mangerid} where id = #{id}")
    int updateReviewLicense(ReviewLicenseEntity reviewLicense);
}
