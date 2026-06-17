package com.shopping_c_backend.Service;

import java.util.concurrent.TimeUnit;

public interface CacheService {

    void set(String key, Object value);

    void set(String key, Object value, long timeout, TimeUnit unit);

    <T> T get(String key, Class<T> type);

    boolean hasKey(String key);

    void delete(String key);

    void expire(String key, long timeout, TimeUnit unit);
}
