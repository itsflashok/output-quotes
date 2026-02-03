import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './router'
import App from './App.vue'
import './assets/styles.css'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)
app.mount('#app')


// Hot Module Replacement: ensure we unmount the app when modules are replaced
if (import.meta && import.meta.hot) {
  import.meta.hot.accept()
  import.meta.hot.dispose(() => {
    try {
      app.unmount()
    } catch (e) {
      // ignore
    }
    if (typeof window !== 'undefined') {
      window.__QUOTES_APP_INSTANCE = null
    }
  })
}
