import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import ROUTES_NAMES from '../constants/routesNames';
import { useNoticeStore } from './notice.store';
import CompanyService from '../api/CompanyService';
import { NOTIFICATION_POSITION, NOTIFICATION_TYPE } from '../constants/notification';
import { FakeFreelancer } from '../constants/hardData';

export const useCompanyStore = defineStore('companyStore', () => {

    const storeNotice = useNoticeStore();
    const router = useRouter();
    const route = useRoute();
    
    const companyLoading = ref(false);
    const companyError = ref(null);

    const bases = ref([]);
    const currentFreelancer = ref(null);

    const fakeFreelancers = ref([
        new FakeFreelancer({ id: 'f1' }),
        new FakeFreelancer({ id: 'f2' }),
        new FakeFreelancer({ id: 'f3' })
    ])

    const currentBase = computed(() => {
        if (route.params?.id) return bases.value.find(item => item.id == route.params.id);
        return null;
    })

    function setFreelancer(freelancer) {
        currentFreelancer.value = freelancer;
    }
    function createFakeFreelancer() {
        const freelancer = new FakeFreelancer({ id: 'f4', created: true });

        if (currentBase.value.rows.length) {
            currentBase.value.rows.push(freelancer);
        } else {
            fakeFreelancers.value.push(freelancer);
        }

        setFreelancer(freelancer);
    }
    function deleteFakeFreelancer() {
        if (currentBase.value.rows.length) {
            currentBase.value.rows = currentBase.value.rows.filter(item => !item.fake);
        } else {
            fakeFreelancers.value.splice(-1);
        }
    }

    async function getBases() {

        companyError.value = null;
        companyLoading.value = true;

        try {
            const data = await CompanyService.getAllBases();
            bases.value = data;
        } catch(e) {
            companyError.value = e || 'Ошибка сервера';
            storeNotice.setError(companyError.value);
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
            storeNotice.setError(companyError.value);
        } finally {
            companyLoading.value = false;
        }
    }

    async function createRowInBase(payload, { setNotification=true }={}) {

        companyError.value = null;
        companyLoading.value = true;

        try {

            payload.price = Number(payload.price) || 0;

            const data = await CompanyService.createRowInBase(payload);

            currentBase.value.rows.push(data);
            currentFreelancer.value = data;

            deleteFakeFreelancer();

            if (setNotification) {
                storeNotice.setMessage('Фрилансер создан');
            }

        } catch(e) {
            companyError.value = e || 'Ошибка сервера';
            storeNotice.setError(companyError.value);
        } finally {
            companyLoading.value = false;
        }
    }
    async function updateRowInBase(payload) {
        companyError.value = null;
        companyLoading.value = true;

        try {

            const id = currentFreelancer.value.profile.id;

            const data = await CompanyService.updateRowInBaseById(id, payload);

            const row = currentBase.value.rows.find(item => item.id === id);
            if (row) row = data;

        } catch(e) {
            companyError.value = e || 'Ошибка сервера';
            storeNotice.setError(companyError.value);
        } finally {
            companyLoading.value = false;
        }
    }

    async function generateInviteLink() {

        companyError.value = null;
        companyLoading.value = true;

        try {
            const data = await CompanyService.generateLink(currentFreelancer.value.profile.id);
            return data;
        } catch(e) {
            companyError.value = e || 'Ошибка сервера';
            storeNotice.setError(companyError.value);
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
        currentFreelancer,
        companyLoading,
        companyError,
        fakeFreelancers,
        getBases,
        createBase,
        createRowInBase,
        setFreelancer,
        updateRowInBase,
        createFakeFreelancer,
        deleteFakeFreelancer,
        generateInviteLink
    }
})