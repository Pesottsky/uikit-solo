<template>
    <template v-if="isCompany">
        <div class="wrapper">
            <Sidebar>
                <AccountMenuCompany />
            </Sidebar>
            <Content>
                <PublicFreelancerCard :is-company="true" />
            </Content>
        </div>
    </template>
    <template v-else-if="isFreelancer">
        <div class="wrapper">
            <Sidebar>
                <AccountMenuFreelancer />
            </Sidebar>
            <Content>
                <PublicFreelancerCard />
            </Content>
        </div>
    </template>
    <template v-else>
        <PublicHeader />
        <PublicFreelancerCard :is-company="true" :is-public="true" />
    </template>
</template>

<script setup>
    import { storeToRefs } from 'pinia';
    import { computed } from 'vue';

    import PublicHeader from '../Header/PublicHeader.vue';
    import PublicFreelancerCard from './PublicFreelancerCard.vue';

    import Sidebar from '@/components/Sidebar/Sidebar.vue';
    import AccountMenuCompany from '@/components/Company/AccountMenu/AccountMenu.vue';
    import AccountMenuFreelancer from '@/components/Freelancer/AccountMenu/AccountMenu.vue';
    import Content from '@/components/Content/Content.vue';

    import { useAuthStore } from '../../../stores/auth.store';

    import ROLES from '../../../constants/roles';
    

    const storeAuth = useAuthStore();
    const { userData } = storeToRefs(storeAuth);

    const isCompany = computed(() => userData.value?.userType === ROLES.COMPANY);
    const isFreelancer = computed(() => userData.value?.userType === ROLES.FREELANCER);
    
</script>