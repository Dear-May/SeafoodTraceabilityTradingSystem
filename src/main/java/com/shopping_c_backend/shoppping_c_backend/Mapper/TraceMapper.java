package com.shopping_c_backend.shoppping_c_backend.Mapper;

import com.shopping_c_backend.shoppping_c_backend.Entity.Trace.TraceEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.Trace.TraceGoodEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.Trace.TraceInformationEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.Trace.TraceUserEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface TraceMapper {
    // 查询
    @Select("select * from traceinfo where shopID = #{shopID}")
    List<TraceEntity> selectByShopID(@Param("shopID") int shopID);

    @Select("SELECT g.* " +
            "FROM tracegoodinfo g " +
            "JOIN traceinfo t ON g.traceID = t.traceID " +
            "WHERE g.goodID = #{goodID} AND t.updateTime >= NOW() - INTERVAL 1 MONTH")
    List<TraceGoodEntity> selectGoodByTraceID(@Param("goodID") String goodID);

    @Select("select * from traceinformation where traceID = #{traceID}")
    List<TraceInformationEntity> selectTraceInformationByTraceID(@Param("traceID") String traceID);

    @Select("select * from traceinfo where traceID = #{traceID}")
    TraceEntity selectTraceByTraceID(@Param("traceID") String traceID);

    @Select("select * from traceuserinfo where informationID = #{informationID}")
    TraceUserEntity selectTraceUserByInformationID(@Param("informationID") String informationID);

    @Select("select goodID from tracegoodinfo where traceID = #{traceID}")
    List<String> selectGoodIDByTraceID(@Param("traceID") String traceID);

    @Select("select * from traceinformation where linkID = #{linkID}")
    TraceInformationEntity selectInformationByLinkID(@Param("linkID") String linkID);

    @Select("SELECT * FROM traceinformation WHERE traceID = #{traceID} order by insertTime DESC limit 1")
    TraceInformationEntity selectLastTraceInformation(@Param("traceID") String traceID);

    @Select("select * from traceuserinfo where traceID = #{traceID} order by insertTime DESC limit 1")
    TraceUserEntity selectLastTraceUser(@Param("traceID") String traceID);

    // 新增
    @Insert("insert into traceinfo(traceID, shopID, insertTime, updateTime) values(#{traceID}, #{shopID}, #{insertTime}, #{updateTime})")
    int createTrace(TraceEntity trace);

    @Insert("insert into traceinformation(linkID, traceID, insertTime, text)values (#{linkID}, #{traceID}, #{insertTime}, #{text})")
    int createTraceInfo(TraceInformationEntity traceInfo);

    @Insert("insert into tracegoodinfo(linkID, traceID, goodID) VALUES (#{linkID}, #{traceID}, #{goodID})")
    int createTraceGood(TraceGoodEntity traceGood);

    @Insert("insert into traceuserinfo(linkID, traceID, userID, informationID, type, insertTime) values (#{linkID}, #{traceID}, #{userID}, #{informationID}, #{type}, #{insertTime})")
    int createTraceUser(TraceUserEntity traceUser);

    // 更新
    @Update("update traceinformation set imgUrL = #{imgUrL} where linkID = #{linkID}")
    int updateTraceImage(TraceInformationEntity traceInfo);
}
