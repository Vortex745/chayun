import { createRouter, createWebHistory } from 'vue-router'
import MyLayout from '../layout/MyLayout.vue'

const routes = [
    // 1. 登录页
    {
        path: '/login',
        name: 'Login',
        component: () => import('../views/login/Login.vue')
    },
    // 2. 后台管理路由
    {
        path: '/',
        component: MyLayout,
        redirect: '/front/home', // 默认去前台
        children: [
            { path: 'tea', name: 'TeaManage', component: () => import('../views/tea/TeaManage.vue') },
            { path: 'orders', name: 'OrderManage', component: () => import('../views/order/OrderManage.vue') },
            { path: 'user', name: 'UserManage', component: () => import('../views/user/UserManage.vue') },
            { path: 'stats', name: 'SalesStats', component: () => import('../views/stats/SalesStats.vue') },
            { path: 'carousel', name: 'CarouselManage', component: () => import('../views/marketing/CarouselManage.vue') },
        ]
    },
    // 3. 商城前台路由
    {
        path: '/front',
        component: () => import('../layout/ClientLayout.vue'),
        redirect: '/front/home',
        children: [
            // 首页
            { path: 'home', component: () => import('../views/front/Home.vue') },
            // 详情页
            { path: 'detail/:id', component: () => import('../views/front/TeaDetail.vue') },
            // 登录页
            { path: 'login', component: () => import('../views/front/FrontLogin.vue') },

            // === 【重点检查这里】 ===
            // 必须引用 Cart.vue，绝对不能是 Home.vue
            { path: 'cart', component: () => import('../views/front/Cart.vue') },
            // =======================

            // 我的订单
            { path: 'myorder', component: () => import('../views/front/MyOrder.vue') },
            { path: 'user', component: () => import('../views/front/UserCenter.vue') }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 路由守卫保持不变...
router.beforeEach((to, from, next) => {
    const user = localStorage.getItem('tea-admin-user')
    if (to.path === '/login' || to.path.startsWith('/front')) {
        next()
    } else if (!user) {
        next('/login')
    } else {
        next()
    }
})

export default router