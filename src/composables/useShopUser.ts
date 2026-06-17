import {ref} from 'vue';
import {useRouter} from 'vue-router';
import axios from 'axios';
import {ElMessage} from 'element-plus';

export function useUserShop() {
    const UserForm = ref({
        id: null,
        phone: null,
        shopid: null,
        nickname: null,
        role: null,
        avatar: null,
        email: null,
        status: null,
    });

    const shopForm = ref({
        status: null,
        shopID: null,
        shopDesc: null,
        shopName: null,
        shopAvatar: null,
    });

    const router = useRouter();

    const initUserSession = () => {
        try {
            const ShopUserBaseInfo = JSON.parse(decodeURIComponent(document.cookie.replace(/(?:^|.*;\s*)ShopUserBaseInfo\s*=\s*([^;]*).*$|^.*$/, "$1")));
            if (ShopUserBaseInfo) {
                sessionStorage.setItem('ShopUserBaseInfoSession', JSON.stringify(ShopUserBaseInfo));
            }
        } catch (error) {
            console.log("无cookie");
        }

        const ShopUserBaseInfoFromSession = JSON.parse(sessionStorage.getItem('ShopUserBaseInfoSession'));
        if (ShopUserBaseInfoFromSession) {
            UserForm.value = ShopUserBaseInfoFromSession;
            if (window.location.pathname === '/store/login') {
                router.replace('/store/index');
            }
        } else {
            router.replace('/store/login');
        }
    };

    const initShopInfo = async () => {
        try {
            const response = await axios.post('/api/shop/findShopById', {
                shopId: UserForm.value.shopid,
            }, {
                headers: {
                    'Content-Type': 'application/json;charset=UTF-8'
                },
                withCredentials: true,
            });
            if (response.data !== null) {
                shopForm.value = response.data;
            } else {
                ElMessage.error('获取店铺信息失败，请稍后再试');
            }
        } catch (err) {
            ElMessage.error('获取店铺信息失败，请稍后再试');
        }
    };

    return {
        UserForm,
        shopForm,
        initUserSession,
        initShopInfo,
    };
}
