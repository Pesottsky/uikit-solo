<template>
    <div class="employment">
        <div class="employment__card">
            <AccountCard />
        </div>
        <Radiobutton label="Изменить занятость" :data="employmentList" size="big" v-model="employment" />
    </div>
</template>

<script setup>
    import { computed, onMounted, ref, watch } from 'vue';

    import AccountCard from '../AccountCard/AccountCard.vue';
    import { Radiobutton } from '../../UI';

    import { EMPLOYMENT_NAME } from '../../../constants/employment'

    import { useFreelancerStore } from '../../../stores/freelancer.store';
    import { storeToRefs } from 'pinia';

    const storeFreelancer = useFreelancerStore();
    const { directory, freelancerProfile } = storeToRefs(storeFreelancer);

    const employment = ref(EMPLOYMENT_NAME.FREE);
    const timer = ref(null);

    const activeEmployment = computed(() => employmentList.value.find(item => item.value === employment.value));
    const employmentList = computed(() => directory.value.loading.map(item => ({ level: item.loading_key, value: item.description })));

    watch(employment, value => {
        if (value && value !== freelancerProfile.value?.loading?.description) {
            if (timer.value) clearTimeout(timer.value);

            timer.value = setTimeout(() => {
                const payload = {
                    loading_key: activeEmployment.value.level, 
                    description: activeEmployment.value.value
                }
                storeFreelancer.updateEmploymentStatus(payload);
            }, 300)
        }
    })

    watch(freelancerProfile, value => {
        if (value) {
            employment.value = value?.loading?.description;
        }
    })
    onMounted(() => {
        employment.value = freelancerProfile.value?.loading?.description;
    })

</script>

<style lang="scss" scoped>
    .employment {
        margin-top: 40px;
        display: flex;
        flex-direction: column;
        align-items: flex-start;
        gap: 40px;

        &__card {
            width: auto;
            padding: 16px 24px;
            border-radius: 6px;
            border: 1px solid var(--black-opacity-10);
            background: var(--white);
        }
    }
</style>