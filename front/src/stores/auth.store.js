import { defineStore } from "pinia";
import { computed, ref } from "vue";
import { useNoticeStore } from "./notice.store"
import ROUTES_NAMES from '../constants/routesNames';
import ROLES from '../constants/roles';
import { useRouter, useRoute } from "vue-router";
import NOTIFICATION_MESSAGES from '../constants/notificationMessages';

import AuthService from "../api/AuthService";

export const useAuthStore = defineStore('authStore', () => {

    const router = useRouter();
    const route = useRoute();
    const storeNotice = useNoticeStore();

    const authError = ref(null);
    const authLoading = ref(false);

    const userData = localStorage.getItem(import.meta.env.VITE_ACCESS_NAME) 
    ? ref({
        access: localStorage.getItem(import.meta.env.VITE_ACCESS_NAME),
        refresh: localStorage.getItem(import.meta.env.VITE_REFRESH_NAME),
        userType: localStorage.getItem(import.meta.env.VITE_USER_TYPE_NAME)
    }) 
    : ref(null);

    const isAuthenticated = computed(() => !!userData.value);

    function setAuth({ access, refresh, user_type }, isLocation=true) {
        localStorage.setItem(import.meta.env.VITE_ACCESS_NAME, access);
        localStorage.setItem(import.meta.env.VITE_REFRESH_NAME, refresh);
        localStorage.setItem(import.meta.env.VITE_USER_TYPE_NAME, user_type);

        userData.value = {
            access,
            refresh,
            userType: user_type
        }

        if (isLocation) {
            if (user_type === ROLES.COMPANY) {
                return router.replace({ name: ROUTES_NAMES.COMPANY_BASE_UNKNOWN });
            }
            if (user_type === ROLES.FREELANCER) {
                return router.replace({ name: ROUTES_NAMES.FREELANCER_EMPLOYMENT });
            }
        }

    }
    function clearAuth() {
        localStorage.removeItem(import.meta.env.VITE_ACCESS_NAME);
        localStorage.removeItem(import.meta.env.VITE_REFRESH_NAME);
        localStorage.removeItem(import.meta.env.VITE_USER_TYPE_NAME);

        userData.value = null;
        
        router.replace({ name: ROUTES_NAMES.LOGIN });
    }
    function clearError() {
        authError.value = null;
    }

    async function registrationCompany({ login, password }) {

        authLoading.value = true;
        authError.value = null;

        try {
            const data = await AuthService.registrationCompany({ login, password });
            setAuth(data);

        } catch(e) {
            authError.value = e || 'Ошибка сервера';
        } finally {
            authLoading.value = false;
        }
    }
    async function registrationFreelancer({ login, first_name, last_name, password }) {

        authLoading.value = true;
        authError.value = null;

        try {

            const link = route.query?.link;

            const data = await AuthService.registrationFreelancer({ login, first_name, last_name, password }, link);
            setAuth(data, false);
            router.replace({ name: ROUTES_NAMES.FREELANCER_START_SCREEN, query: { link } });

        } catch(e) {
            authError.value = e || 'Ошибка сервера';
        } finally {
            authLoading.value = false;
        }
    }
    async function login({ login, password }) {
        
        authLoading.value = true;
        authError.value = null;

        try {
            const link = route.query?.link;
            const data = await AuthService.login({ login, password }, link);
            setAuth(data);
        } catch(e) {
            console.warn(e);
            authError.value = e || 'Ошибка сервера';
        } finally {
            authLoading.value = false;
        }
    }
    async function refresh() {
        try {
            if (userData.value?.refresh) {
                const data = await AuthService.refresh(userData.value.refresh);
                setAuth(data, false);
            } else {
                clearAuth();
            }
            
        } catch(e) {
            clearAuth();
        }
    }
    async function forgetPassword({login}) {
        authLoading.value = true;
        authError.value = null;

        try {
            await AuthService.forgetPassword(login);
        } catch(e) {
            authError.value = e || 'Ошибка сервера';
        } finally {
            authLoading.value = false;
        }
    }
    async function updatePassword({login, new_password}) { 
        authLoading.value = true;
        authError.value = null;

        try {
            const code = route.query?.code;
            const data = await AuthService.updatePassword({login, new_password, code});
            setAuth(data);
            storeNotice.setMessage(NOTIFICATION_MESSAGES.PASSWORD_RECOVERED);
        } catch(e) {
            authError.value = e || 'Ошибка сервера';
        } finally {
            authLoading.value = false;
        }
    }
    async function logout() {

        authLoading.value = true;
        authError.value = null;

        try {
            await AuthService.logout();
            clearAuth();
        } catch(e) {
            authError.value = e || 'Ошибка сервера';
        } finally {
            authLoading.value = false;
        }
    }

    return {
        userData,
        isAuthenticated,
        authError,
        authLoading,
        registrationCompany,
        registrationFreelancer,
        login,
        refresh,
        forgetPassword,
        logout,
        clearError,
        updatePassword
    }

});