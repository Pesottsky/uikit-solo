import { createRouter, createWebHashHistory } from 'vue-router'
import App from "../App.vue"
import Components from "../view/Components.vue"
import Authorization from "../view/Authorization.vue"
import Registration from "../view/Registration.vue"
import { get, accessKey } from "../localstorage"

const routes = [
    {
        path: '/',
        name: "Main",
        component: App,
        meta: { requiresAuth: true }
    },
    {
        path: "/registration",
        name: "Registration",
        component: Registration,
    },
    {
        path: "/login",
        name: "Login",
        component: Authorization,
    },
    {
        path: '/components',
        component: Components,
    },
]


const router = createRouter({
    history: createWebHashHistory(),
    routes: routes,
})

router.beforeEach((to, from) => {
    if (to.meta.requiresAuth && get(accessKey) == null) {
        return {
            path: '/login',
            query: { redirect: to.fullPath },
        }
    }
})

export default router