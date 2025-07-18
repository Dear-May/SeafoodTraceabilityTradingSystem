package com.shopping_c_backend.shoppping_c_backend.Mapper;

import com.shopping_c_backend.shoppping_c_backend.Entity.AddressEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface AddressMapper {
    // 查询
    @Select("SELECT * FROM addressinfo WHERE userid = #{userId} ORDER BY CASE WHEN status = 'default' THEN 0 ELSE 1 END, status DESC")
    List<AddressEntity> getAddressByUserId(int userId);

    @Select("select * from addressinfo where id = #{id}")
    AddressEntity getAddressById(int id);

    @Select("select name from province where id = #{id}")
    String getProvinceName(int id);

    @Select("select name from city where id = #{id}")
    String getCityName(int id);

    @Select("select name from area where id = #{id}")
    String getAreaName(int id);

    @Select("select name from street where id = #{id}")
    String getStreetName(int id);

    @Select("select id from province where name = #{name}")
    Integer getProvinceId(String name);

    @Select("select id from city where name = #{name} and pid = #{pid}")
    Integer getCityId(@Param("name") String name, @Param("pid") int pid);

    @Select("select id from area where name = #{name} and pid = #{pid}")
    Integer getAreaId(@Param("name") String name, @Param("pid") int pid);

    @Select("select id from street where name = #{name} and pid = #{pid}")
    Integer getStreetId(@Param("name") String name, @Param("pid") int pid);

    //更新
    @Update("update addressinfo set consignee = #{consignee}, phone = #{phone}, area = #{area}, detailed_address = #{detailed_address}, status = #{status} where id = #{id}")
    int updateAddress(AddressEntity addressEntity);

    @Update("update addressinfo set status = #{status} where id = #{id}")
    int setDefaultAddress(@Param("status") String status, @Param("id") int id);

    //新增
    @Insert("insert into addressinfo(userid, consignee,phone, country,area,detailed_address, status) values(#{userid}, #{consignee},#{phone},#{country},#{area},#{detailed_address},#{status})")
    int addAddress(AddressEntity addressEntity);

    //删除
    @Delete("delete from addressinfo where id = #{id}")
    int deleteAddressById(int id);

}
