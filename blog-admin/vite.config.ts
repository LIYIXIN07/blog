import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3001,
    strictPort: true,
    open: true,
    proxy: {
      '/api': {
        target: 'http://121.4.27.29:8080',
        changeOrigin: true
      }
    }
  }
})
