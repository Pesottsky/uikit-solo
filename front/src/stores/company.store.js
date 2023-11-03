import { defineStore } from 'pinia';
import { computed, reactive, ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import ROUTES_NAMES from '../constants/routesNames';
import { useNoticeStore } from './notice.store';
import { useAuthStore } from './auth.store';
import CompanyService from '../api/CompanyService';
import NOTIFICATION_MESSAGES from '../constants/notificationMessages';
import { FakeFreelancer } from '../constants/FakeFreelancer';

export const useCompanyStore = defineStore('companyStore', () => {

    const storeNotice = useNoticeStore();
    const storeAuth = useAuthStore();
    const router = useRouter();
    const route = useRoute();
    
    const companyLoading = ref(false);
    const commentLoading = ref(false);

    const companyError = ref(null);

    const bases = ref([]);
    const currentFreelancer = ref(null);

    const companyInfo = reactive({
        id: null,
        name: '',
        link: '',
        about: '',
    })

    const fakeFreelancers = ref([
        new FakeFreelancer({ id: 'f1' }),
        new FakeFreelancer({ id: 'f2' }),
        new FakeFreelancer({ id: 'f3' })
    ])

    const commentFreelancer = ref(null);

    const currentBase = computed(() => {
        if (route.params?.id) return bases.value.find(item => item.id == route.params.id);
        return null;
    })

    function setCompanyInfo({ id, name, link, about }) {
        companyInfo.name = name;
        companyInfo.link = link;
        companyInfo.about = about;
        companyInfo.id = id;
    }
    function setFreelancer(freelancer) {
        currentFreelancer.value = freelancer;
    }

    function setComment(comment) {
        commentFreelancer.value = comment
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
            if (fakeFreelancers.value.length > 3) fakeFreelancers.value.splice(-1);
        }
    }

    function setLinkData(linkData) {
        const index = currentBase.value.rows.findIndex(item => item.id === currentFreelancer.value.id);
        if (index >= 0) {
            currentBase.value.rows[index].link = linkData;
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

            const name = 'Фрилансеры';// Math.random().toString(36).substring(2);

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
    async function renameBase(name) {
        companyError.value = null;

        try {
            await CompanyService.renameBase(currentBase.value.id, name);
            currentBase.value.name = name;
        } catch(e) {
            companyError.value = e || 'Ошибка сервера';
            storeNotice.setError(companyError.value);
        }
    }

    async function createRowInBase(payload, { setNotification=true }={}) {

        companyError.value = null;
        companyLoading.value = true;

        try {

            payload.price = Number(payload.price) || 0;
            
            delete payload.grade;
            delete payload.loading;

            const data = await CompanyService.createRowInBase(payload);

            currentBase.value.rows.push(data);
            currentFreelancer.value = data;

            deleteFakeFreelancer();

            if (setNotification) {
                storeNotice.setMessage(NOTIFICATION_MESSAGES.FREELANCER_CREATED);
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

            const rowId = currentFreelancer.value.id;

            payload.price = Number(payload.price);

            delete payload.grade;
            delete payload.loading;

            const data = await CompanyService.updateRowInBaseById(rowId, payload);

            const index = currentBase.value.rows.findIndex(item => item.id === rowId);
            if (index >= 0) {
                currentBase.value.rows[index].profile = data;
            }

        } catch(e) {
            companyError.value = e || 'Ошибка сервера';
            storeNotice.setError(companyError.value);
        } finally {
            companyLoading.value = false;
        }
    }
    async function pushRowInBase(payload) {
        companyLoading.value = true;
        companyError.value = null;
        
        try {
            if (storeAuth.isAuthenticated) {
                if (bases.value.length) {
                    await CompanyService.pushRowInBaseByProfileId(payload);
                    await getBases();
                    storeNotice.setMessage(NOTIFICATION_MESSAGES.FREELANCER_APPENDED);
                }
                sessionStorage.removeItem('PROFILE_ID_FOR_PUSH_BASE');
            } else {
                sessionStorage.setItem('PROFILE_ID_FOR_PUSH_BASE', payload);
                router.push({ name: ROUTES_NAMES.SIGN_UP });
            }
        } catch(e) {
            companyError.value = e || 'Ошибка сервера';
            storeNotice.setError(companyError.value);
        } finally {
            companyLoading.value = false;
        }
    }
    async function removeRowInBase() {
        companyError.value = null;
        companyLoading.value = true;
        try {
            await CompanyService.removeRowInBase(currentFreelancer.value.id);
            const index = currentBase.value.rows.findIndex(item => item.id === currentFreelancer.value.id);
            currentBase.value.rows.splice(index, 1);
            storeNotice.setMessage(NOTIFICATION_MESSAGES.FREELANCER_REMOVED);
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
            const data = await CompanyService.generateLink(currentFreelancer.value.id);
            setLinkData(data);

            return data;
        } catch(e) {
            companyError.value = e || 'Ошибка сервера';
            storeNotice.setError(companyError.value);
        } finally {
            companyLoading.value = false;
        }
    }
    async function sendInviteByEmail({ link_id=null, email=null }) {
        companyError.value = null;
        companyLoading.value = true;

        try {

            const payload = {
                link_id,
                email,
                row_id: currentFreelancer.value.id
            }

            const data = await CompanyService.sendInviteByEmail(payload);
            if (data.status === 200) {
                await getBases();
            } else {
                setLinkData(data);
            }
            
            
        } catch(e) {
            companyError.value = e || 'Ошибка сервера';
            storeNotice.setError(companyError.value);
        } finally {
            companyLoading.value = false;
        }
    }

    async function getCompanyInfo() {
        companyError.value = null;
        companyLoading.value = true;

        try {
            const data = await CompanyService.getCompanyInfo();
            setCompanyInfo(data);
        } catch(e) {
            companyError.value = e || 'Ошибка сервера';
            storeNotice.setError(companyError.value);
        } finally {
            companyLoading.value = false;
        }
    }
    async function updateCompanyInfo(payload) {
        companyError.value = null;
        companyLoading.value = true;

        try {
            payload.id = companyInfo.id;
            await CompanyService.updateCompanyInfo(payload);
            setCompanyInfo(payload);
            storeNotice.setMessage(NOTIFICATION_MESSAGES.UPDATE_INFO_COMPANY);
        } catch(e) {
            companyError.value = e || 'Ошибка сервера';
            storeNotice.setError(companyError.value);
        } finally {
            companyLoading.value = false;
        }
    }

    async function getComment() {
        companyError.value = null;
        commentLoading.value = true;

        try {
            const { comment } = await CompanyService.getComment(currentFreelancer.value.profile.id);
            commentFreelancer.value = comment;
        } catch(e) {
            companyError.value = e || 'Ошибка сервера';
            storeNotice.setError(companyError.value);
        } finally {
            commentLoading.value = false;
        }
    }
    async function createComment({ comment }) {
        companyError.value = null;
        commentLoading.value = true;

        try {
            const payload = {
                profile_id: currentFreelancer.value.profile.id,
                comment
            }

            await CompanyService.createComment(payload);
            commentFreelancer.value = comment;

            storeNotice.setMessage(NOTIFICATION_MESSAGES.SAVE_COMMENT);

        } catch(e) {
            companyError.value = e || 'Ошибка сервера';
            storeNotice.setError(companyError.value);
        } finally {
            commentLoading.value = false;
        }
    }

    return {
        bases,
        currentBase,
        currentFreelancer,
        companyLoading,
        commentLoading,
        companyError,
        fakeFreelancers,
        companyInfo,
        commentFreelancer,
        setComment,
        getBases,
        createBase,
        createRowInBase,
        removeRowInBase,
        setFreelancer,
        updateRowInBase,
        createFakeFreelancer,
        deleteFakeFreelancer,
        generateInviteLink,
        sendInviteByEmail,
        updateCompanyInfo,
        getCompanyInfo,
        getComment,
        createComment,
        pushRowInBase,
        renameBase
    }
})