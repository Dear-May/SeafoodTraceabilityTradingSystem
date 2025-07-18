package com.shopping_c_backend.shoppping_c_backend.Mapper;

import com.shopping_c_backend.shoppping_c_backend.Entity.LiveMessageEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.ShopEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface LiveMapper {
    // 查询
    @Select("select * from shopinfo where ShopID = #{ShopID} and status = '正常'")
    ShopEntity getShopInfo(@Param("ShopID") int ShopID);

    @Select("select onlineUserId from live_online_user where roomId = #{roomId}")
    List<Integer> getOnlineUserIdByRoomId(int roomId);

    @Select("select * from live_message where roomID = #{roomId} limit 1000")
    List<LiveMessageEntity> getLiveMessageByRoomId(int roomId);

    // 新增
    @Insert("insert into live_online_user (onlineUserId, roomId) values (#{onlineUserId}, #{roomId})")
    int insertLiveUser(@Param("onlineUserId") int onlineUserId, @Param("roomId") int roomId);

    @Insert("insert into live_message (id, sendID, roomID, message, time,sendType) values (#{id}, #{sendID}, #{roomID}, #{message}, now(), #{sendType})")
    int insertLiveMessage(LiveMessageEntity liveMessageEntity);

    // 修改
    @Update("update shopinfo set livestatus = #{livestatus} where ShopID = #{ShopID}")
    int updateLiveStatus(@Param("ShopID") int ShopID, @Param("livestatus") String Livestatus);

    // 删除
    @Delete("delete from live_online_user where onlineUserId = #{onlineUserId} and roomId = #{roomId}")
    int deleteLiveUser(@Param("onlineUserId") int onlineUserId, @Param("roomId") int roomId);

}
