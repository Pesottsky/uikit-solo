import { defineStore } from "pinia";
import { ref } from "vue";

import { useNoticeStore } from "./notice.store";
import FreelancerService from "../api/FreelancerService";
import NOTIFICATION_MESSAGES from "../constants/notificationMessages";

export const useFreelancerStore = defineStore('freelancerStore', () => {
    const storeNotice = useNoticeStore();

    const freelancerError = ref(null);
    const freelancerLoading = ref(null);

    const freelancerProfile = ref(null);

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
    async function updateEmploymentStatus({ level_key, description }) {
        freelancerError.value = null;
        freelancerLoading.value = true;

        try {
            const data = await FreelancerService.updateLoading({ level_key, description });
            freelancerProfile.value = data;

            storeNotice.setMessage(NOTIFICATION_MESSAGES.EMPLOYMENT_CHANGE);
        } catch(e) {
            freelancerError.value = e || 'Ошибка сервера';
            storeNotice.setError(freelancerError.value);
        } finally {
            freelancerLoading.value = false;
        }
    }

    return {
        freelancerError,
        freelancerLoading,
        freelancerProfile,
        getProfile,
        updateEmploymentStatus
    }
})