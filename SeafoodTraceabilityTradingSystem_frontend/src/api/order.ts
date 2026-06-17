import request from './request';

// 订单相关 API
export function getShopOrderCount(params) {
    return request.post('/api/shop/getShopOrderCount', params)
}

export function getShopOrderInfo(params) {
    return request.post('/api/shop/getShopOrderInfo', params)
}

export function updateOrderStatus(data) {
    return request.post('/api/shop/updateOrderStatus', data)
}

export function getShopReturnInfo(params) {
    return request.post('/api/shop/return/getShopReturnInfo', params)
}

export function changeReturnStatus(data) {
    return request.post('/api/shop/return/changeReturnStatus', data)
}

export function getOrderStatusInfo(params) {
    return request.post('/api/shop/info/getOrderStatusInfo', params)
}
