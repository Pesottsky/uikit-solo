import ROUTES_NAMES from "../constants/routesNames";
import ROLES from "../constants/roles";
import { useCompanyStore } from "../stores/company.store";
import { storeToRefs } from "pinia";

async function checkBases(to, from, next) {
    const storeCompany = useCompanyStore();
    const { bases } = storeToRefs(storeCompany);
    await storeCompany.getBases();

    if (bases.value.length) {
        return next({ name: ROUTES_NAMES.COMPANY_BASE, params: { id: bases.value[0].id } });
    }

    next();
}

const companyRouter = [
    {
        path: '/company',
        component: () => import('../layouts/Company/CompanyLayout.vue'),
        children: [
            {
                path: 'profile',
                name: ROUTES_NAMES.COMPANY_PROFILE,
                component: () => import('../views/Company/ProfileView.vue'),
                meta: {
                    roles: [ROLES.COMPANY]
                }
            }, {
                path: 'base',
                name: ROUTES_NAMES.COMPANY_BASE_UNKNOWN,
                component: () => import('../views/Company/BaseUnknownView.vue'),
                beforeEnter: [checkBases],
                meta: {
                    roles: [ROLES.COMPANY]
                }
            },
            {
                path: 'base/:id',
                name: ROUTES_NAMES.COMPANY_BASE,
                component: () => import('../views/Company/BaseView.vue'),
                meta: {
                    roles: [ROLES.COMPANY]
                }
            }
        ]
    }
];

export default companyRouter;