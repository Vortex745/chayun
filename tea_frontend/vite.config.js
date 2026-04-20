import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  build: {
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (!id.includes('node_modules')) return
          if (id.includes('node_modules/echarts')) return 'echarts-vendor'
          if (id.includes('node_modules/element-plus')) return 'element-plus'
          if (
            id.includes('node_modules/vue') ||
            id.includes('node_modules/@vue') ||
            id.includes('node_modules/vue-router')
          ) {
            return 'vue-vendor'
          }
          if (id.includes('node_modules/axios')) return 'http-vendor'
        },
      },
    },
  },
})
