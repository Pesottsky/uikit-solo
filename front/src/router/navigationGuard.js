import { useAuthStore } from "../stores/auth.store";
import ROLES from "../constants/roles";
import ROUTES_NAMES from "../constants/routesNames";

import { storeToRefs } from 'pinia';

const navigationGuard = (to, from, next) => {

    const store = useAuthStore();
    const { userData, isAuthenticated } = storeToRefs(store);

    const userRole = userData.value?.userType;

    const metaRoles = to?.meta?.roles || [];

    if (metaRoles.includes(ROLES.ANY)) {
        return next();
    }

    if (isAuthenticated.value) {
        if (!metaRoles.includes(userRole) || !metaRoles.length) {
            if (userRole === ROLES.COMPANY) {
                return next({ name: ROUTES_NAMES.COMPANY_PROFILE });
            } else if (userRole === ROLES.FREELANCER) {
                return next({ name: ROUTES_NAMES.FREELANCER_EMPLOYMENT });
            }
            return next({ name: ROUTES_NAMES.NOT_FOUND });
        }
    } else {
        if (metaRoles.length) {
            return next({ name: ROUTES_NAMES.LOGIN })
        }
    }

    next();
}

export default navigationGuard;