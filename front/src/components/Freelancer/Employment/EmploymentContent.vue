<template>
    <div class="employment">
        <div class="employment__card">
            <AccountCard />
        </div>
        <Radiobutton label="Изменить занятость" :data="EMPLOYMENT_VALUE" v-model="employment" />
    </div>
</template>

<script setup>
    import { computed, ref, watch } from 'vue';

    import AccountCard from '../AccountCard/AccountCard.vue';
    import { Radiobutton } from '../../UI';

    import { EMPLOYMENT_VALUE, EMPLOYMENT_NAME } from '../../../constants/employment'

    import { useFreelancerStore } from '../../../stores/freelancer.store';

    const storeFreelancer = useFreelancerStore();

    const employment = ref(EMPLOYMENT_NAME.FREE);
    const timer = ref(null);

    const activeEmployment = computed(() => EMPLOYMENT_VALUE.find(item => item.value === employment.value));

    watch(employment, value => {
        if (value) {
            if (timer.value) clearTimeout(timer.value);

            timer.value = setTimeout(() => {
                const payload = {
                    level_key: activeEmployment.value.level, 
                    description: activeEmployment.value.value
                }
                storeFreelancer.updateEmploymentStatus(payload);
            }, 300)
        }
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