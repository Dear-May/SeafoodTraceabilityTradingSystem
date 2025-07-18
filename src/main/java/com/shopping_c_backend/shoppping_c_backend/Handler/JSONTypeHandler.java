package com.shopping_c_backend.shoppping_c_backend.Handler;

import com.alibaba.fastjson.JSON;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JSONTypeHandler extends BaseTypeHandler<JSON> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JSON parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.toJSONString());
    }

    @Override
    public JSON getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        if (json == null) {
            return null;
        }

        // 尝试解析为 JSONArray，如果解析失败，则尝试解析为 JSONObject
        try {
            return JSON.parseArray(json); // 尝试解析为 JSONArray
        } catch (Exception e) {
            return JSON.parseObject(json); // 如果失败，则解析为 JSONObject
        }
    }

    @Override
    public JSON getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        if (json == null) {
            return null;
        }

        // 同样处理按位置索引获取的结果
        try {
            return JSON.parseArray(json);
        } catch (Exception e) {
            return JSON.parseObject(json);
        }
    }

    @Override
    public JSON getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        if (json == null) {
            return null;
        }

        // 同样处理可调用语句结果
        try {
            return JSON.parseArray(json);
        } catch (Exception e) {
            return JSON.parseObject(json);
        }
    }

}

