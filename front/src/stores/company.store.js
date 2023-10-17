import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import ROUTES_NAMES from '../constants/routesNames';
import { useNoticeStore } from './notice.store';
import CompanyService from '../api/CompanyService';

export const useCompanyStore = defineStore('companyStore', () => {

    const storeNotice = useNoticeStore();
    const router = useRouter();
    const route = useRoute();
    
    const companyLoading = ref(false);
    const companyError = ref(null);

    const bases = ref([]);

    const currentBase = computed(() => {
        if (route.params?.id) return bases.value.find(item => item.id == route.params.id);
        return null;
    })

    async function getBases() {

        companyError.value = null;
        companyLoading.value = true;

        try {
            const data = await CompanyService.getAllBases();
            bases.value = data;
        } catch(e) {
            companyError.value = e || 'Ошибка сервера';
            storeNotice.setText(e);
        } finally {
            companyLoading.value = false;
        }
    }
    async function createBase() {

        companyError.value = null;
        companyLoading.value = true;

        try {

            const name = Math.random().toString(36).substring(2);

            const data = await CompanyService.createBase(name);
            bases.value.push(data);
            router.push({ name: ROUTES_NAMES.COMPANY_BASE, params: { id: data.id } });
        } catch(e) {
            companyError.value = e || 'Ошибка сервера';
            storeNotice.setText(e);
        } finally {
            companyLoading.value = false;
        }
    }
    async function createRowInBase(payload) {

        companyError.value = null;
        companyLoading.value = true;

        try {

            payload.price = Number(payload.price) || 0;

            const data = await CompanyService.createRowInBase(payload);
            console.log(data);
        } catch(e) {
            companyError.value = e || 'Ошибка сервера';
            storeNotice.setText(e);
        } finally {
            companyLoading.value = false;
        }
    }

    async function test() {

        companyError.value = null;
        companyLoading.value = true;

        try {
            
        } catch(e) {
            companyError.value = e || 'Ошибка сервера';
        } finally {
            companyLoading.value = false;
        }
    }

    return {
        bases,
        currentBase,
        companyLoading,
        companyError,
        getBases,
        createBase,
        createRowInBase
    }
})