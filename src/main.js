// 导入Vue3的核心API，用于创建Vue应用实例
import {createApp} from 'vue';

// 导入根组件App.vue，它是整个应用程序的主视图模板
import App from './App.vue';

import ElementPlus from 'element-plus';

import 'element-plus/dist/index.css';
import zhCn from 'element-plus/es/locale/lang/zh-cn'
// 导入已配置好的路由模块（index.js或router.ts等），它管理着应用内的页面跳转逻辑
import router from "@/router";
import {createPinia} from 'pinia';
// 引入Bootstrap CSS
import 'bootstrap/dist/css/bootstrap.min.css'
// 引入Bootstrap JS（bundle版本）
import 'bootstrap/dist/js/bootstrap.bundle.min.js'
import 'bootstrap-icons/font/bootstrap-icons.css'

// 使用createApp方法创建一个Vue应用实例，并注册全局路由配置
const app = createApp(App).use(router);
const pinia = createPinia();

// 将Vue应用挂载到HTML文档中id为'app'的元素上
// 这将把整个应用程序渲染到这个DOM元素内部
app.use(ElementPlus, {
    locale: zhCn,
})
app.use(pinia);
app.mount('#app');

router.beforeEach((to, from, next) => {
    window.document.title = to.meta.title === undefined ? '默认标题' : to.meta.title
    next();
})
