package com.shopping_c_backend.shoppping_c_backend.Mapper;

import com.shopping_c_backend.shoppping_c_backend.Entity.CommentEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.OrderEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.OrderItemEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.ReturnGoodEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface OrderMapper {
    // 查询
    @Select("select * from orderinfo where userid = #{userid} and status != '已取消' order by insertdate desc")
    List<OrderEntity> getOrderInfoByUserid(@Param("userid") int userid);

    @Select("select * from orderinfo where shopid = #{shopId} and status != '已取消' and status != '未支付' order by insertdate desc")
    List<OrderEntity> getOrderInfoByShopId(@Param("shopId") int shopId);

    @Select("select * from orderinfo o where o.shopid = #{shopId} and exists (select 1 from returngoodinfo r where r.orderId = o.orderId) order by o.insertdate desc")
    List<OrderEntity> getReturnOrderInfoByShopId(@Param("shopId") int shopId);

    @Select("select * from orderinfo where userid = #{userid} and shopid = #{shopId} and status != '已取消' and status != '未支付' order by insertdate desc limit 6")
    List<OrderEntity> getOrderInfoByUseridAndShopId(@Param("userid") int userid, @Param("shopId") int shopId);

    @Select("select count(*) from orderinfo where userid = #{userid}")
    int countByUserid(@Param("userid") int userid);

    @Select("select count(*) from orderinfo where userid = #{userid} and status like concat('%',#{status},'%')")
    int countByUseridAndStatus(@Param("userid") int userid, @Param("status") String status);

    @Select("select count(*) from orderinfo where shopid = #{shopId} and status != '已取消' and status != '未支付'")
    int countByShopId(@Param("shopId") int shopId);

    @Select("select count(*) from orderinfo where shopid = #{shopId} and status like concat('%',#{status},'%')")
    int countByShopIdAndStatus(@Param("shopId") int shopId, @Param("status") String status);

    @Select("select * from orderinfo where orderid = #{orderid}")
    OrderEntity getOrderInfoByOrderid(@Param("orderid") String orderid);

    @Select("SELECT * FROM orderinfo WHERE userid = #{userid} AND status = '已评价' ORDER BY updatedate DESC")
    List<OrderEntity> getCommentedOrderByUserid(@Param("userid") int userid);

    @Select("select * from orderiteminfo where orderid = #{orderId}")
    List<OrderItemEntity> getOrderItemByOrderId(@Param("orderId") String orderId);

    @Select("select * from orderiteminfo where good_id = #{goodId}")
    List<OrderItemEntity> getOrderItemByGoodId(@Param("goodId") String goodId);

    @Select("select * from commentinfo where orderid = #{orderId}")
    CommentEntity getCommentByOrderId(@Param("orderId") String orderId);

    @Select("select * from commentinfo where commentid = #{commentId}")
    CommentEntity getCommentByCommentId(@Param("commentId") int commentId);

    @Select("select commentid from commentinfo where orderid = #{orderId}")
    int getCommentIdByOrderId(@Param("orderId") String orderId);

    @Select("select * from returngoodinfo where orderId = #{orderId}")
    ReturnGoodEntity getReturnGoodByReturnId(@Param("orderId") String orderId);

    // 新增
    @Insert("insert into orderinfo(orderid, userid, shopid, addressid, totalprice, insertdate, updatedate, status, remark) values(#{orderid}, #{userid}, #{shopid}, #{addressid}, #{totalprice}, Now(), NOW(), #{status}, #{remark})")
    int insertOrder(OrderEntity order);

    @Insert("insert into orderiteminfo(orderitemid, orderid, good_id, spec_id, goodname, specname, specprice, specnumber, specimg) VALUES (#{orderitemid}, #{orderid}, #{good_id}, #{spec_id}, #{goodname}, #{specname}, #{specprice}, #{specnumber}, #{specimg})")
    int insertOrderItem(OrderItemEntity orderItem);

    @Insert("insert into commentinfo(orderid, text,rate, inserttime, updatetime, status) values( #{orderid}, #{text},#{rate},Now(), NOW(), #{status})")
    int insertComment(CommentEntity comment);

    @Insert("insert into returngoodinfo(returnId, orderId, text, images, insertTime, updateTime) VALUES (#{returnId}, #{orderId}, #{text}, #{images}, Now(), NOW())")
    int insertReturnGood(ReturnGoodEntity returnGood);

    // 修改
    @Update("update orderinfo set status = #{status}, updatedate = NOW() where orderid = #{orderid}")
    int updateOrder(OrderEntity order);

    @Update("update commentinfo set images = #{images} where commentid = #{commentid}")
    int updateCommentImage(CommentEntity comment);

    @Update("update commentinfo set status = #{status} where commentid = #{commentid}")
    int updateComment(CommentEntity comment);

    @Update("update returngoodinfo set images = #{images} where returnId = #{returnId}")
    int updateReturnGoodImage(ReturnGoodEntity returnGood);

    @Update("update returngoodinfo set workUserId = #{workUserId}, updateTime = NOW() where orderId = #{orderId}")
    int updateReturnGoodWorkUserId(ReturnGoodEntity returnGood);

}
