package com.shopping_c_backend.shoppping_c_backend.Mapper;

import com.shopping_c_backend.shoppping_c_backend.Entity.GoodEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.SpecificationEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface GoodMapper {
    // 查询 商品
    // 分页查询
    @Select("select * from goodinfo where status =1")
    List<GoodEntity> findAllGood();

    @Select("SELECT * FROM goodinfo where status =1 limit #{offset}, #{size}")
    List<GoodEntity> getGoods(@Param("offset") int offset, @Param("size") int size);

    @Select("SELECT * FROM goodinfo where status =1 order by RAND() limit #{offset}, #{size}")
    List<GoodEntity> getGoodsRandom(@Param("offset") int offset, @Param("size") int size);

    @Select("select * from goodinfo where ShopID = #{shopId} order by uplodatime desc;")
    List<GoodEntity> getGoodsByShopIdOrderByUploadTime(@Param("shopId") int shopId);

    @Select("select * from goodinfo where status=1 and ShopID = #{shopId}")
    List<GoodEntity> getGoodsByShopId(@Param("shopId") int shopId);

    @Select("select * from goodinfo where status=1 and GoodName like concat('%',#{name},'%') limit #{offset}, #{size}")
    List<GoodEntity> getGoodsByName(@Param("name") String name, @Param("offset") int offset, @Param("size") int size);

    @Select("select count(*) from goodinfo where status =1")
    int getCount();

    @Select("select count(*) from goodinfo where status =1 and GoodName like concat('%',#{name},'%')")
    int getCountByName(@Param("name") String name);

    @Select("select ShopID from goodinfo where GoodID = #{id}")
    int getShopID(@Param("id") String id);

    @Select("select * from goodinfo where GoodID = #{id}")
    GoodEntity getGoodById(@Param("id") String id);

    @Select("select * from goodinfo where GoodID = #{id} and ShopID = #{shopId}")
    GoodEntity getGoodByShopId(@Param("id") String id, @Param("shopId") int shopId);

    // 查询 规格
    @Select("select * from specificationinfo where mainProductID = #{mainProductID}")
    List<SpecificationEntity> getSpecificationByMainProductID(String mainProductID);

    @Select("select * from specificationinfo where mainProductID = #{mainProductID} and status = 1")
    List<SpecificationEntity> getValidSpecification(String mainProductID);

    @Select("select * from specificationinfo where specificationID = #{specificationID}")
    SpecificationEntity getSpecificationBySpecificationID(String specificationID);

    @Select("select GoodID from goodinfo where ShopID=#{shopId} order by GoodID desc limit 1")
    String getLastGoodId(@Param("shopId") int shopId);

    @Select("select * from goodinfo where GoodName in (select info from t_image_search_image_add_log where id = #{id}) limit 1")
    GoodEntity getGoodByImageId(@Param("id") Long id);

    // 新增
    @Insert("insert into goodinfo (GoodID, GoodName, Price, Comments, uplodatime, status, ShowURL, ShopID, Location) VALUES (#{GoodID}, #{GoodName}, #{Price}, #{Comments}, #{uplodatime}, #{status}, #{ShowURL}, #{ShopID}, #{Location})")
    int addGood(GoodEntity good);

    @Insert("insert into specificationinfo (specificationID, mainProductID, specName, price, status, updateTime, number, Showurl) VALUES (#{specificationID}, #{mainProductID}, #{specName}, #{price}, #{status}, #{updateTime}, #{number}, #{Showurl})")
    int addSpecification(SpecificationEntity specification);

    // 修改
    @Update("update goodinfo set status = #{status} where GoodID = #{GoodID}")
    int updateGoodStatus(@Param("status") boolean status, @Param("GoodID") String goodID);

    @Update("update specificationinfo set status = #{status} , updateTime = now()  where mainProductID = #{mainProductID}")
    int updateSpecificationStatus(@Param("status") boolean status, @Param("mainProductID") String mainProductID);

    @Update("update specificationinfo set status = #{status} , updateTime = now()  where specificationID = #{specificationID}")
    int updateSpecificationStatusByID(@Param("specificationID") String specificationID, @Param("status") boolean status);

    @Update("update goodinfo set GoodName= #{GoodName}, Location = #{Location} ,uplodatime = now() where GoodID = #{GoodID}")
    int updateGood(@Param("GoodID") String goodID, @Param("GoodName") String goodName, @Param("Location") String location);

    @Update("update specificationinfo set specName = #{specName}, price = #{price}, number = #{number},updateTime = now()  where specificationID = #{specificationID}")
    int updateSpecification(@Param("specificationID") String specificationID, @Param("specName") String specName, @Param("price") double price, @Param("number") int number);
}
