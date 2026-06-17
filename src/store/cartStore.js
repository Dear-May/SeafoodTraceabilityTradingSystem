import {defineStore} from 'pinia';
import {getCartCount} from '@/api/goods';

export const useCartStore = defineStore('cart', {
    state: () => ({
        count: 0
    }),

    getters: {
        cartCount: (state) => state.count,
        hasItems: (state) => state.count > 0
    },

    actions: {
        async fetchCartCount(params) {
            try {
                const response = await getCartCount(params)
                if (response.data !== undefined && response.data !== null) {
                    this.count = Number(response.data)
                }
            } catch {
                this.count = 0
            }
        },

        setCount(val) {
            this.count = val
        },

        clearCart() {
            this.count = 0
        }
    }
})
