package com.shopping_c_backend.module.address;

import com.shopping_c_backend.module.address.AddressEntity;
import java.util.List;

public interface AddressService {
    List<AddressEntity> getUserAddresses(int userId);
    List<Integer> addressToId(String address);
    String getAddressInfo(int provinceId, int cityId, int areaId, int streetId);
    int insertAddress(AddressEntity addressEntity);
    int updateAddress(AddressEntity addressEntity);
    int updateDefaultAddress(int addressId, int userId);
    int deleteAddress(int addressId, int userId);
}
