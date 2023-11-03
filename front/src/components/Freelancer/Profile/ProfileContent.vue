<template>
    <div class="profile">
        <Quote size="small">
            Заполните, пожалуйста, профиль. Эти данные будет видеть компания в своей таблице, они нужны, чтобы понять, подходите ли вы на проект.
        </Quote>
        <AccountCard />
        <div class="profile__card">
            <FreelancerCard ref="cardRef" :freelancer="freelancerProfile" v-if="freelancerProfile" />
            <Button label="Сохранить изменения" :disabled="freelancerLoading" :loading="freelancerLoading" :type="BUTTON_TYPE.SECONDARY" @on-click="saveCard" />
        </div>
    </div>
</template>

<script setup>
    import { ref } from 'vue';
    import { storeToRefs } from 'pinia';

    import { Quote, Button } from '../../UI';
    import AccountCard from '../AccountCard/AccountCard.vue';
    import FreelancerCard from '../FreelacerCard/FreelancerCard.vue';

    import BUTTON_TYPE from '../../../constants/buttonTypes';

    import { useFreelancerStore } from '../../../stores/freelancer.store';


    const storeFreelancer = useFreelancerStore();
    const { freelancerProfile, freelancerLoading, directory } = storeToRefs(storeFreelancer);
    
    const cardRef = ref(null);

    function saveCard() {
        const payload = { ...cardRef.value?.modelData };
        if (payload.grade) {
            payload.grade = { ...directory.value.grade.find(({ description }) => description === payload.grade) };
        }
        if (payload.loading) {
            payload.loading = { ...directory.value.loading.find(({ description }) => description === payload.loading) };
        }

        storeFreelancer.updateProfile(payload);
    }
</script>

<style lang="scss" scoped>
    .profile {
        display: flex;
        flex-direction: column;
        gap: 40px;
        margin-top: 12px;
        width: 565px;
        margin-bottom: 88px;

        &__card {
            width: 100%;
            display: flex;
            flex-direction: column;
            gap: 40px;
        }
    }
</style>