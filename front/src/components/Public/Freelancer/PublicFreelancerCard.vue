<template>
    <NotFoundBlock :main-page-name="mainPageName" v-if="freelancerError" />
    <div v-else class="freelancer-card">
        <div class="freelancer-card__title">
            <h1>{{ userName }}</h1>
            <Button v-if="isCompany" label="Добавить в базу" :icon="true" @on-click="pushToBase">
                <PlusWhiteIcon />
            </Button>
        </div>
        <FreelancerCard :freelancer="freelancerProfile" :is-edit="false" />
    </div>
</template>

<script setup>
import { storeToRefs } from 'pinia';
    import { computed, onMounted } from 'vue';

    import { useRoute } from 'vue-router';

    import FreelancerCard from '../../Freelancer/FreelacerCard/FreelancerCard.vue';
    import NotFoundBlock from '../NotFound/NotFoundBlock.vue';
    import { Button } from '../../UI';
    import { PlusWhiteIcon } from '../../Icons';

    import { useFreelancerStore } from '../../../stores/freelancer.store';
    import { useCompanyStore } from '../../../stores/company.store';

    const storeFreelancer = useFreelancerStore();
    const { freelancerProfile, freelancerError } = storeToRefs(storeFreelancer);

    const storeCompany = useCompanyStore();

    const props = defineProps({
        isCompany: { type: Boolean, default: false },
        mainPageName: { type: String }
    })

    const route = useRoute();

    const userName = computed(() => {
        if (!freelancerProfile.value?.first_name && !freelancerProfile.value?.last_name) return 'Имя Фамилия';
        return `${freelancerProfile.value?.first_name || 'Имя'} ${freelancerProfile.value?.last_name}`
    });

    function pushToBase() {
        storeCompany.pushRowInBase(freelancerProfile.value.id);
    }

    onMounted(() => {
        storeFreelancer.getProfileById(route.params.id);
    })
</script>

<style lang="scss" scoped>
    .freelancer-card {
        box-sizing: content-box;
        width: 620px;
        display: flex;
        flex-direction: column;
        gap: 48px;
        padding: 64px 72px;
        margin: 32px auto;
        border-radius: 6px;
        border: 1px solid rgba(0, 0, 0, 0.03);
        background: #FFF;
        box-shadow: -8px 4px 40px 0px rgba(0, 0, 0, 0.05);

        &__title {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
    }
</style>