import { defineStore } from "pinia";
import { onMounted, reactive, ref } from "vue";

import { useNoticeStore } from "./notice.store";
import FreelancerService from "../api/FreelancerService";
import NOTIFICATION_MESSAGES from "../constants/notificationMessages";

export const useFreelancerStore = defineStore('freelancerStore', () => {
    const storeNotice = useNoticeStore();

    const freelancerError = ref(null);
    const freelancerLoading = ref(null);

    const freelancerProfile = ref(null);

    const directory = reactive({
        loading: [],
        grade: [],
    })

    async function getLoading() {
        try {
            const data = await FreelancerService.getLoading();
            directory.loading = data;
        } catch(e) {
            freelancerError.value = e || 'Ошибка сервера';
            storeNotice.setError(freelancerError.value);
        }
    }
    async function getGrade() {
        try {
            const data = await FreelancerService.getGrade();
            directory.grade = data;
        } catch(e) {
            freelancerError.value = e || 'Ошибка сервера';
            storeNotice.setError(freelancerError.value);
        }
    }
    
    async function getProfile() {
        freelancerError.value = null;

        try {
            const data = await FreelancerService.getProfile();
            freelancerProfile.value = data;
        } catch(e) {
            freelancerError.value = e || 'Ошибка сервера';
            storeNotice.setError(freelancerError.value);
        }
    }
    async function getProfileById(id) {
        freelancerLoading.value = true;
        freelancerError.value = null;
        try {
            const data = await FreelancerService.getProfileById(id);
            freelancerProfile.value = data;
        } catch(e) {
            freelancerError.value = e || 'Ошибка сервера';
            storeNotice.setError(freelancerError.value);
        } finally {
            freelancerLoading.value = false;
        }
    }
    async function updateEmploymentStatus({ loading_key, description }) {
        freelancerError.value = null;
        freelancerLoading.value = true;

        try {
            const data = await FreelancerService.updateLoading({ loading_key, description });
            freelancerProfile.value = data;

            storeNotice.setMessage(NOTIFICATION_MESSAGES.EMPLOYMENT_CHANGE);
        } catch(e) {
            freelancerError.value = e || 'Ошибка сервера';
            storeNotice.setError(freelancerError.value);
        } finally {
            freelancerLoading.value = false;
        }
    }
    async function updateProfile(payload) {
        freelancerLoading.value = true;
        freelancerError.value = null;
        
        try {

            payload.price = Number(payload.price);

            const data = await FreelancerService.updateProfile(payload);
            freelancerProfile.value = data;

            storeNotice.setMessage('Информация обновлена');
        } catch(e) {
            freelancerError.value = e || 'Ошибка сервера';
            storeNotice.setError(freelancerError.value);
        } finally {
            freelancerLoading.value = false;
        }
    }

    return {
        directory,
        freelancerError,
        freelancerLoading,
        freelancerProfile,
        getLoading,
        getGrade,
        getProfile,
        updateEmploymentStatus,
        updateProfile,
        getProfileById
    }
})