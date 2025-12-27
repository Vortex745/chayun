import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import axios from 'axios'
import router from './router' // <--- 1. 引入路由

axios.defaults.baseURL = 'http://localhost:8080';

const app = createApp(App)

app.use(ElementPlus)
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}
app.config.globalProperties.$http = axios

app.use(router) // <--- 2. 挂载路由
app.mount('#app')