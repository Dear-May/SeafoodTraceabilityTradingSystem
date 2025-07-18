import {defineStore} from 'pinia';

export const useThemeStore = defineStore('theme', {
    state: () => ({
        isNightMode: false,
    }),
    actions: {
        toggleNightMode() {
            this.isNightMode = !this.isNightMode;
        },
        setNightMode(value) {
            this.isNightMode = value;
        }
    }
});
