import ROUTES_NAMES from "../constants/routesNames";
import ROLES from "../constants/roles";

const freelancerRouter = [
    {
        path: '/freelancer',
        component: () => import('../layouts/Freelancer/FreelancerLayout.vue'),
        children: [
            {
                path: 'start',
                name: ROUTES_NAMES.FREELANCER_START_SCREEN,
                component: () => import('../views/Freelancer/StartScreenView.vue'),
                meta: {
                    roles: [ROLES.FREELANCER]
                }
            },
            {
                path: 'employment',
                name: ROUTES_NAMES.FREELANCER_EMPLOYMENT,
                component: () => import('../views/Freelancer/EmploymentView.vue'),
                meta: {
                    roles: [ROLES.FREELANCER]
                }
            },
            {
                path: 'profile',
                name: ROUTES_NAMES.FREELANCER_PROFILE,
                component: () => import('../views/Freelancer/ProfileView.vue'),
                meta: {
                    roles: [ROLES.FREELANCER]
                }
            }
        ]
    }
];

export default freelancerRouter;