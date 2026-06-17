import request from './request';

// 商品相关 API
export function getCartCount(params) {
    return request.post('/api/good/getCartCount', params)
}

export function getAllGoodInfo(params) {
    return request.post('/api/shop/getAllGoodInfo', params)
}

export function insertGoodInfo(data) {
    return request.post('/api/shop/insertGoodInfo', data)
}

export function updateSpecInfo(data) {
    return request.post('/api/shop/updateSpecInfo', data)
}

export function updateSpecImage(formData) {
    return request.post('/api/shop/updateSpecImage', formData, {
        headers: {'Content-Type': 'multipart/form-data'}
    })
}

export function getProductBuyInfo(params) {
    return request.post('/api/shop/info/getProductBuyInfo', params)
}

export function getProductTrendInfo(params) {
    return request.post('/api/shop/info/getProductTrendInfo', params)
}
