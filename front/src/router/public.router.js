import ROLES from "../constants/roles";
import ROUTES_NAMES from "../constants/routesNames";
import SIGN_UP_TYPE from "../constants/signUpType";

import { useAuthStore } from "../stores/auth.store";
import { useFreelancerStore } from "../stores/freelancer.store";

function setSignUpParams(to, from, next) {
    const types = [SIGN_UP_TYPE.COMPANY, SIGN_UP_TYPE.FREELANCER];
    if (!types.includes(to.query?.type)) {
        const type = to.query?.link ? SIGN_UP_TYPE.FREELANCER : SIGN_UP_TYPE.COMPANY;
        return next({ name: to.name, query: { ...to.query, type } })
    }
    next();
}

function transferLink(to, from, next) {

    if (from.query?.link && !to.query?.link) {
        return next({ name: to.name, query: { link: from.query.link } });
    }

    next()
}

function resetAuthError(to, from, next) {
    const store = useAuthStore();
    store.clearError();
    
    next();
}

const publicRouter = [
    {
        path: '/',
        component: () => import('../layouts/Public/PublicLayout.vue'),
        redirect: '/login',
        children: [
            {
                path: 'login',
                name: ROUTES_NAMES.LOGIN,
                component: () => import('../views/Auth/LoginView.vue'),
                beforeEnter: [resetAuthError, transferLink],
                meta: {
                    roles: []
                }
            },
            {
                path: 'signup',
                name: ROUTES_NAMES.SIGN_UP,
                component: () => import('../views/Auth/SignUpView.vue'),
                beforeEnter: [setSignUpParams, transferLink, resetAuthError],
                meta: {
                    roles: []
                }
            },
            {
                path: 'forgot-password',
                name: ROUTES_NAMES.FORGOT_PASSWORD,
                component: () => import('../views/Auth/ForgotPasswordView.vue'),
                meta: {
                    roles: []
                }
            },
            {
                path: 'reset-password',
                name: ROUTES_NAMES.RESET_PASSWORD,
                component: () => import('../views/Auth/ResetPasswordView.vue'),
                meta: {
                    roles: []
                }
            }
        ]
    },
    {
        path: '/freelancers/:id',
        name: ROUTES_NAMES.FREELANCER_PROFILE_PUBLIC,
        component: () => import('../views/Freelancer/ProfilePublicView.vue'),
        meta: {
            roles: [ROLES.ANY]
        }
    },
    {
        path: '/:pathMatch(.*)*',
        name: ROUTES_NAMES.NOT_FOUND,
        component: () => import('../views/NotFoundView.vue'),
        meta: {
            roles: [ROLES.ANY]
        }
    },
]

export default publicRouter;