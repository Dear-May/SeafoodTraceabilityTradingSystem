package com.shopping_c_backend.shoppping_c_backend.Mapper;

import com.shopping_c_backend.shoppping_c_backend.Entity.AttendanceEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.ReviewLicenseEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.ShopEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.ShopUserEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Mapper
@Component
public interface ShopMapper {
    // 查询
    @Select("SELECT * FROM shopinfo WHERE ShopID = #{id}")
    ShopEntity findShopById(@Param("id") int id);

    @Select("select * from shopuserinfo where shopid = #{shopId} and role = 'merchant'")
    ShopUserEntity findMerchantByShopId(@Param("shopId") int shopId);

    @Select("select * from followshopinfo where shopid = #{shopId} and userid = #{userId}")
    Boolean isFollowed(@Param("shopId") int shopId, @Param("userId") int userId);

    @Select("select * from shopuserinfo where phone = #{phone} and shopid = #{shopId} and status != '已离职'")
    ShopUserEntity findUserByPhoneAndShopId(@Param("phone") String phone, @Param("shopId") int shopId);

    @Select("select * from shopuserinfo where phone = #{phone} and shopid = #{shopId} and status = '已离职'")
    ShopUserEntity findDissmissedUserByPhoneAndShopId(@Param("phone") String phone, @Param("shopId") int shopId);

    @Select("select * from shopuserinfo where phone= #{phone}")
    ShopUserEntity findUserByPhone(String phone);

    @Select("select * from reviewlicense where id = #{id}")
    ReviewLicenseEntity findReviewLicenseById(@Param("id") String id);

    @Select("select * from shopuserinfo where shopid = #{shopId} and status = '正常' and role = 'staff'")
    List<ShopUserEntity> getShopUsersByShopId(@Param("shopId") int shopId);

    @Select("select * from shopuserinfo where shopid = #{shopId} and status = '未审核' and role = 'staff'")
    List<ShopUserEntity> getAuditShopUsersByShopId(@Param("shopId") int shopId);

    @Select("select * from shopuserinfo where id = #{id}")
    ShopUserEntity findUserById(@Param("id") int id);

    @Select("select * from attendanceinfo where userId = #{userId}")
    List<AttendanceEntity> getAttendanceByUserId(@Param("userId") int userId);

    @Select("select count(totalprice) from orderinfo where shopid = #{shopId} and status != '已取消' and status != '未支付' and totalprice > 0 and DATE(insertdate) = CURDATE()")
    int countTodayOrder(@Param("shopId") int shopId);

    @Select("select count(totalprice) from orderinfo where shopid = #{shopId} and status != '已取消' and status != '未支付' and totalprice > 0 and MONTH(insertdate) = MONTH(CURDATE()) and YEAR(insertdate) = YEAR(CURDATE())")
    int countThisMonthOrder(@Param("shopId") int shopId);

    @Select("SELECT COUNT(*) FROM orderinfo o " +
            "WHERE o.orderid IN (SELECT r.orderid FROM returngoodinfo r WHERE DATE(r.inserttime) = CURDATE()) " +
            "AND o.insertdate >= CURDATE() AND o.status != '已取消' AND o.status != '未支付'")
    int countTodayReturnedOrders(@Param("shopId") int shopId);

    @Select("select count(*) from orderinfo where shopid = #{shopId} and status='未支付'")
    int countTodayUnpaidOrders(@Param("shopId") int shopId);

    @Select("select count(*) from orderinfo where shopid = #{shopId} and status='待收货'")
    int countPendingOrders(@Param("shopId") int shopId);

    @Select("select count(*) from shopuserinfo where shopid = #{shopId} and status='正常'")
    int countShopUsers(@Param("shopId") int shopId);

    @Select("select count(*) from followshopinfo where shopid = #{shopId}")
    int countFans(@Param("shopId") int shopId);

    @Select("SELECT COUNT(*) FROM attendanceinfo WHERE attendanceinfo.userId IN " +
            "(SELECT shopuserinfo.id FROM shopuserinfo WHERE shopid = #{shopId} AND status = '正常') " +
            "AND DATE(attendanceinfo.date) = CURDATE()")
    int countTodayAttendance(@Param("shopId") int shopId);

    @Select("select id from shopuserinfo where shopid = #{shopId} and role = 'staff' and status = '正常'")
    List<Integer> getStaffIdsByShopId(@Param("shopId") int shopId);

    @Select("select coalesce(sum(orderinfo.totalprice),0) from orderinfo where orderinfo.orderid in" +
            "(select orderinfo.orderId from orderiteminfo where good_id= #{goodId}) and date(orderinfo.insertdate) = CURDATE() AND orderinfo.status NOT IN ('已取消', '未支付') "
    )
    Double countTodayOrderItem(@Param("goodId") String goodId);

    @Select("select coalesce(sum(orderinfo.totalprice),0) from orderinfo where orderid in " +
            "(select orderId from orderiteminfo where good_id= #{goodId}) " +
            "and MONTH(orderinfo.insertdate) = MONTH(CURDATE()) " +
            "and YEAR(orderinfo.insertdate) = YEAR(CURDATE()) " +
            "and orderinfo.status != '已取消' and orderinfo.status != '未支付'")
    Double countThisMonthOrderItem(@Param("goodId") String goodId);

    @Select("select COALESCE(sum(orderinfo.totalprice),0) from orderinfo where orderid in " +
            "(select orderId from orderiteminfo where good_id= #{goodId}) " +
            "and orderinfo.status != '已取消' " +
            "and orderinfo.status != '未支付'")
    Double countTotalOrderItem(@Param("goodId") String goodId);

    @Select("WITH RECURSIVE week_dates AS (" +
            "    SELECT YEAR(CURDATE()) AS year_number, " +
            "           WEEK(CURDATE(), 1) AS week_number, " +
            "           CURDATE() AS start_date, " +
            "           DATE_ADD(CURDATE(), INTERVAL -6 WEEK) AS min_date " +
            "    UNION ALL " +
            "    SELECT YEAR(DATE_ADD(start_date, INTERVAL -1 WEEK)) AS year_number, " +
            "           WEEK(DATE_ADD(start_date, INTERVAL -1 WEEK), 1) AS week_number, " +
            "           DATE_ADD(start_date, INTERVAL -1 WEEK), " +
            "           min_date " +
            "    FROM week_dates " +
            "    WHERE start_date > min_date" +
            ") " +
            "SELECT wd.week_number, wd.year_number, COALESCE(SUM(o.totalprice), 0) AS total_price " +
            "FROM week_dates wd " +
            "LEFT JOIN orderinfo o " +
            "ON YEAR(o.insertdate) = wd.year_number " +
            "AND WEEK(o.insertdate, 1) = wd.week_number " +
            "AND o.status NOT IN ('已取消', '未支付') " +
            "AND o.orderid IN (" +
            "    SELECT orderId FROM orderiteminfo WHERE good_id = #{goodId}" +
            ") " +
            "GROUP BY wd.year_number, wd.week_number " +
            "ORDER BY wd.year_number, wd.week_number")
    List<Map<String, Object>> getWeeklyTotalPrice(@Param("goodId") String goodId);

    @Select("WITH RECURSIVE weeks AS (" +
            "    SELECT YEARWEEK(CURDATE(), 1) AS week_number " +
            "    UNION ALL " +
            "    SELECT week_number - 1 " +
            "    FROM weeks " +
            "    WHERE week_number > YEARWEEK(DATE_ADD(CURDATE(), INTERVAL -6 WEEK), 1)" +
            ") " +
            "SELECT " +
            "    w.week_number, " +
            "    COALESCE(f.fan_count, 0) AS fan_count " +
            "FROM " +
            "    weeks w " +
            "LEFT JOIN (" +
            "    SELECT " +
            "        YEARWEEK(inserTime, 1) AS week_number, " +
            "        COUNT(*) AS fan_count " +
            "    FROM " +
            "        followshopinfo " +
            "    WHERE " +
            "        shopid = #{shopId} " +
            "        AND inserTime >= DATE_ADD(CURDATE(), INTERVAL -6 WEEK) " +
            "    GROUP BY " +
            "        YEARWEEK(inserTime, 1)" +
            ") f " +
            "ON w.week_number = f.week_number " +
            "ORDER BY " +
            "    w.week_number ")
    List<Map<String, Object>> getWeeklyFanCount(@Param("shopId") int shopId);

    @Select("WITH RECURSIVE weeks AS (" +
            "    SELECT YEARWEEK(CURDATE(), 1) AS week_number " +
            "    UNION ALL " +
            "    SELECT week_number - 1 " +
            "    FROM weeks " +
            "    WHERE week_number > YEARWEEK(DATE_ADD(CURDATE(), INTERVAL -6 WEEK), 1)" +
            ") " +
            "SELECT " +
            "    w.week_number, " +
            "    COUNT(DISTINCT o.userid) AS user_count " +
            "FROM " +
            "    weeks w " +
            "LEFT JOIN (" +
            "    SELECT " +
            "        YEARWEEK(oi.insertdate, 1) AS week_number, " +
            "        oi.userid " +
            "    FROM " +
            "        orderinfo oi " +
            "    INNER JOIN orderiteminfo oi_item ON oi.orderid = oi_item.orderid " +
            "    INNER JOIN goodinfo g ON oi_item.good_id = g.goodid " +
            "    WHERE " +
            "        g.shopid = #{shopId} " +
            "        AND oi.insertdate >= DATE_ADD(CURDATE(), INTERVAL -6 WEEK) " +
            "    GROUP BY " +
            "        YEARWEEK(oi.insertdate, 1), oi.userid" +
            ") o ON w.week_number = o.week_number " +
            "GROUP BY " +
            "    w.week_number " +
            "ORDER BY " +
            "    w.week_number ")
    List<Map<String, Object>> getWeeklyOrderUserCount(@Param("shopId") int shopId);

    @Select("SELECT oi.status AS status, COUNT(*) AS count " +
            "FROM orderinfo oi " +
            "INNER JOIN orderiteminfo oii ON oi.orderid = oii.orderid " +
            "INNER JOIN goodinfo g ON oii.good_id = g.goodid " +
            "WHERE g.shopid = #{shopId} " +
            "GROUP BY oi.status")
    List<Map<String, Object>> getOrderStatusCount(@Param("shopId") int shopId);

    // 新增
    @Insert("insert into followshopinfo (shopid, userid, inserTime) values (#{shopId}, #{userId}, now())")
    int followShop(@Param("shopId") int shopId, @Param("userId") int userId);

    @Insert("insert into shopinfo (ShopName, ShopAvatar, ShopDesc, status) values (#{ShopName}, #{ShopAvatar}, #{ShopDesc}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "ShopID", keyColumn = "ShopID")
    int addShop(ShopEntity shopEntity);

    @Insert("insert into shopuserinfo (phone, shopid, role, nickname, avatar, email, status) values (#{phone}, #{shopid}, #{role}, #{nickname}, #{avatar}, #{email}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int addUser(ShopUserEntity shopUserEntity);

    @Insert("insert into reviewlicense (id, shopid, imageurl, inserttime, updatetime, mangerid, status, socialcreditcode, name, location, registeredcapital, paidIncapital, dateofestablishment, businessscope, legalrepresentative, type, registrationauthority, compositionform, idnumber, periodOfValidity, startingdateofvalidityperiod, dateofapproval, taxregistrationnumber) VALUES (#{id}, #{shopid}, #{imageurl}, #{inserttime}, #{updatetime}, #{mangerid}, #{status}, #{socialcreditcode}, #{name}, #{location}, #{registeredcapital}, #{paidincapital}, #{dateofestablishment}, #{businessscope}, #{legalrepresentative}, #{type}, #{registrationauthority}, #{compositionform}, #{idnumber}, #{periodOfValidity}, #{startingdateofvalidityperiod}, #{dateofapproval}, #{taxregistrationnumber})")
    int addReviewLicense(ReviewLicenseEntity reviewLicenseEntity);

    @Insert("insert into attendanceinfo (userId, date, status, createTime) values (#{userId}, #{date}, #{status}, now())")
    int addAttendance(AttendanceEntity attendanceEntity);

    // 修改
    @Update("update shopinfo set ShopAvatar = #{ShopAvatar}, ShopDesc = #{ShopDesc}, status = #{status} where ShopID = #{ShopID}")
    int updateShop(ShopEntity shopEntity);

    @Update("update reviewlicense set imageurl = #{imageurl}, updatetime = now(),status = #{status} where id = #{id}")
    int updateReviewLicense(ReviewLicenseEntity reviewLicenseEntity);

    @Update("update shopuserinfo set status = '正常',inserttime = now() where id = #{id} and status = '未审核'")
    int addStaff(@Param("id") int id);

    @Update("update shopuserinfo set status = '已离职',inserttime = now() where id = #{id} and status = '正常'")
    int dismissStaff(@Param("id") int id);

    @Update("update shopuserinfo set nickname = #{nickname},email = #{email},status = '未审核',inserttime=now() where id = #{id}")
    void updateDissmissedStaff(ShopUserEntity shopUserEntity);

    // 删除
    @Delete("delete from followshopinfo where shopid = #{shopId} and userid = #{userId}")
    int unfollowShop(@Param("shopId") int shopId, @Param("userId") int userId);

    @Delete("delete from shopuserinfo where id = #{id}")
    int deleteAuditStaff(@Param("id") int id);
}
