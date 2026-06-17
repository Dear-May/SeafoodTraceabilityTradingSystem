import {createRouter, createWebHistory} from 'vue-router';
import {setupGuards} from './guards';

import publicRoutes from './modules/public';
import userRoutes from './modules/user';
import storeRoutes from './modules/store';
import adminRoutes from './modules/admin';

const routes = [
    ...publicRoutes,
    ...userRoutes,
    ...storeRoutes,
    ...adminRoutes
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

setupGuards(router);

export default router;
