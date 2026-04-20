import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './style.css' // Global Design System
import App from './App.vue'
import axios from 'axios'
import router from './router' // <--- 1. 引入路由
import { patchMessageDefaults } from './utils/message'

axios.defaults.baseURL = 'http://localhost:8080';
patchMessageDefaults()

const app = createApp(App)

app.use(ElementPlus)
app.config.globalProperties.$http = axios

app.use(router) // <--- 2. 挂载路由
app.mount('#app')
