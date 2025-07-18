package com.shopping_c_backend.shoppping_c_backend.Service;

import com.shopping_c_backend.shoppping_c_backend.Entity.AddressEntity;
import com.shopping_c_backend.shoppping_c_backend.Mapper.AddressMapper;
import com.shopping_c_backend.shoppping_c_backend.Mapper.UserMapper;
import com.shopping_c_backend.shoppping_c_backend.Util.AddressUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class AddressImpl {
    @Resource
    private AddressMapper addressMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisTemplate redisTemplate;
    private static final Logger logger = LoggerFactory.getLogger(AddressImpl.class);

    /**
     * 获取用户策略:先从缓存中获取用户，没有则读mysql数据，再将数据写入缓存
     */
    public List<AddressEntity> getUserAddresses(int userId) {
        List<AddressEntity> addresses;
        String username = userMapper.getUserNameById(userId);
        String key = "user_" + username + "_addresses";
        ValueOperations<String, List<AddressEntity>> operations = redisTemplate.opsForValue();
        //判断redis中是否有键为key的缓存
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            List<AddressEntity> list = operations.get(key);
            logger.info("从缓存中获取数据:{}_addresses", username);
            return list;
        } else {
            addresses = addressMapper.getAddressByUserId(userId);
            logger.info("从数据库中获取数据:{}_addresses", username);
            logger.info("缓存数据:{}_addresses", username);
            //写入缓存
            operations.set(key, addresses, 5, TimeUnit.HOURS);
        }
        return addresses;
    }

    public static List<String> extractAddress(String address) {
        List<String> components = new ArrayList<>();
        // 检查是否是直辖市
        if (address.startsWith("北京市") || address.startsWith("天津市") ||
                address.startsWith("上海市") || address.startsWith("重庆市")) {
            // 直接添加直辖市和区
            components.add("北京");
            components.add("北京市");
            String[] parts = address.substring(3).split("区|县");
            if (parts.length > 0) {
                components.add(parts[0] + "区"); // 添加区名
            }
            if (parts.length > 1) {
                components.add(parts[1]); // 添加街道或详细地址
            }
        } else {
            String[] parts = address.split("省|市|区|县");
            components.add(parts[0]); // 省
            if (parts.length > 1) {
                components.add(parts[1] + "市"); // 市
            }
            if (parts.length > 2) {
                components.add(parts[2] + "区"); // 区
            }
            if (parts.length > 3) {
                components.add(address.substring(address.indexOf(parts[2]) + parts[2].length() + 1)); // 街道或详细地址
            }
        }
        return components;
    }

    public List<Integer> addressToId(String address) {
        List<String> result = AddressUtil.extractAddress(address);
        List<Integer> ids = new ArrayList<>();
        ids.add(result.size());
        int provinceId, cityId, areaId, streetId;
        if (result.size() == 4) {
            provinceId = addressMapper.getProvinceId(result.get(0));
            cityId = addressMapper.getCityId(result.get(1), provinceId);
            areaId = addressMapper.getAreaId(result.get(2), cityId);
            streetId = addressMapper.getStreetId(result.get(3), areaId);
            ids.add(provinceId);
            ids.add(cityId);
            ids.add(areaId);
            ids.add(streetId);
        } else if (result.size() == 3) {
            provinceId = addressMapper.getProvinceId(result.get(0));
            cityId = addressMapper.getCityId(result.get(1), provinceId);
            areaId = addressMapper.getAreaId(result.get(2), cityId);
            ids.add(provinceId);
            ids.add(cityId);
            ids.add(areaId);
        }
        return ids;
    }

    /*
      新增地址
     */
    public String getAddressInfo(int provinceId, int cityId, int areaId, int streetId) {
        String provinceName = addressMapper.getProvinceName(provinceId);
        String cityName = addressMapper.getCityName(cityId);
        String areaName = addressMapper.getAreaName(areaId);
        String streetName = addressMapper.getStreetName(streetId);
        if (provinceName.equals("北京") || provinceName.equals("上海") || provinceName.equals("天津") || provinceName.equals("重庆"))
            return provinceName + "市" + cityName + areaName;
        else
            return provinceName + "省" + cityName + areaName + streetName;
    }


    public int addAddress(AddressEntity addressEntity) {
        String username = userMapper.getUserNameById(addressEntity.getUserid());
        String key = "user_" + username + "_addresses";
        ValueOperations<String, List<AddressEntity>> operations = redisTemplate.opsForValue();
        //判断redis中是否有键为key的缓存
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            redisTemplate.delete(key);
            logger.info("删除缓存中的key-----------> {}", key);
        }
        List<AddressEntity> oldAddresses = addressMapper.getAddressByUserId(addressEntity.getUserid());
        if (oldAddresses.isEmpty()) {
            addressEntity.setStatus("default");
        } else {
            if (addressEntity.getStatus().equals("default")) {
                AddressEntity oldDefaultAddress = oldAddresses.get(0);
                int result = addressMapper.setDefaultAddress("", oldDefaultAddress.getId());
                if (result != 1)
                    return 0;
            }
        }
        int result = addressMapper.addAddress(addressEntity);
        if (result == 1) {
            List<AddressEntity> addresses = addressMapper.getAddressByUserId(addressEntity.getUserid());
            operations.set(key, addresses, 5, TimeUnit.HOURS);
            logger.info("更新缓存中的数据-----------> {}", key);
            return result;
        }
        return 0;
    }

    /*
      修改地址
     */
    public int updateAddress(AddressEntity addressEntity) {
        String username = userMapper.getUserNameById(addressEntity.getUserid());
        String key = "user_" + username + "_addresses";
        ValueOperations<String, List<AddressEntity>> operations = redisTemplate.opsForValue();
        //判断redis中是否有键为key的缓存
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            redisTemplate.delete(key);
            logger.info("删除缓存中的key-----------> {}", key);
        }
        if (addressEntity.getStatus().equals("default")) {
            List<AddressEntity> oldAddresses = addressMapper.getAddressByUserId(addressEntity.getUserid());
            AddressEntity oldDefaultAddress = oldAddresses.get(0);
            int result = addressMapper.setDefaultAddress("", oldDefaultAddress.getId());
            if (result == 0)
                return 0;
        }
        int result = addressMapper.updateAddress(addressEntity);
        if (result == 1) {
            List<AddressEntity> addresses = addressMapper.getAddressByUserId(addressEntity.getUserid());
            operations.set(key, addresses, 5, TimeUnit.HOURS);
            logger.info("更新缓存中的数据-----------> {}", key);
            return result;
        }
        return 0;

    }

    public int updateDefaultAddress(int addressId, int userId) {
        String username = userMapper.getUserNameById(userId);
        List<AddressEntity> addresses = getUserAddresses(userId);
        AddressEntity oldDefaultAddress = addresses.get(0);
        String key = "user_" + username + "_addresses";
        int result = addressMapper.setDefaultAddress("", oldDefaultAddress.getId());
        if (result == 1) {
            int result_new = addressMapper.setDefaultAddress("default", addressId);
            if (result_new == 1) {
                ValueOperations<String, List<AddressEntity>> operations = redisTemplate.opsForValue();
                //判断redis中是否有键为key的缓存
                boolean hasKey = redisTemplate.hasKey(key);
                if (hasKey) {
                    redisTemplate.delete(key);
                    logger.info("删除缓存中的key-----------> {}", key);
                }
                List<AddressEntity> newAddresses = addressMapper.getAddressByUserId(userId);
                operations.set(key, newAddresses, 5, TimeUnit.HOURS);
                logger.info("更新缓存中的数据-----------> {}", key);
                return 1;
            } else
                return 0;
        }
        return 0;
    }

    /*
      删除地址策略:删除数据表中数据，然后更新缓存中的数据
     */
    public int deleteAddress(int addressId, int userid) {
        AddressEntity addressEntity = addressMapper.getAddressById(addressId);
        boolean isDefault = Objects.equals(addressEntity.getStatus(), "default");
        int result = addressMapper.deleteAddressById(addressId);
        if (result == 1) {
            if (isDefault) {
                //如果删除的是默认地址，则查询用户的其他地址，将第一个地址设置为默认地址
                List<AddressEntity> addressEntities = addressMapper.getAddressByUserId(userid);
                if (!addressEntities.isEmpty()) {
                    AddressEntity newDefaultAddress = addressEntities.get(0);
                    result = addressMapper.setDefaultAddress("default", newDefaultAddress.getId());
                    if (result != 1) {
                        return result;
                    }
                }
            }
            String username = userMapper.getUserNameById(userid);
            String key = "user_" + username + "_addresses";
            ValueOperations<String, List<AddressEntity>> operations = redisTemplate.opsForValue();
            //判断redis中是否有键为key的缓存
            boolean hasKey = redisTemplate.hasKey(key);
            if (hasKey) {
                redisTemplate.delete(key);
                logger.info("删除缓存中的key-----------> {}", key);
            }
            List<AddressEntity> addresses = addressMapper.getAddressByUserId(userid);
            operations.set(key, addresses, 5, TimeUnit.HOURS);
            logger.info("更新缓存中的数据-----------> {}", key);
            return result;
        }
        return 0;
    }

}
