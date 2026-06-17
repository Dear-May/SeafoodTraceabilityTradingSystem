package com.shopping_c_backend.Service;

import java.util.List;
import java.util.Map;

public interface TraceService {
    List<Map<String, Object>> getTraceInfoById(String goodId);
    boolean isInfoExist(String goodId);
    Map<String, Object> addTraceInfo(String goodId, List<Map<String, Object>> traceData);
}
