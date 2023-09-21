import { createRouter, createWebHashHistory } from 'vue-router'
import Components from "../view/Components.vue"
import Authorization from "../view/Authorization.vue"
import Registration from "../view/Registration.vue"
import Main from "../components/main/Main.vue"
import { get, accessKey } from "../localStorage"

const routes = [
    {
        path: '/',
        name: "Main",
        component: Main,
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
        console.log("AAA")
        return {
            path: '/login',
            query: { redirect: to.fullPath },
        }
    }
})

export default router