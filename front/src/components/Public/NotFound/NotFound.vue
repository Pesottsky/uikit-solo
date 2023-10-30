<template>
    <template v-if="isCompany">
        <div class="wrapper">
            <Sidebar>
                <AccountMenuCompany />
            </Sidebar>
            <Content>
                <NotFoundBlock :main-page-name="mainPageName" />
            </Content>
        </div>
    </template>
    <template v-else-if="isFreelancer">
        <div class="wrapper">
            <Sidebar>
                <AccountMenuFreelancer />
            </Sidebar>
            <Content>
                <NotFoundBlock :main-page-name="mainPageName" />
            </Content>
        </div>
    </template>
    <template v-else>
        <PublicHeader />
        <NotFoundBlock :main-page-name="mainPageName" />
    </template>

</template>

<script setup>
    import { storeToRefs } from 'pinia';
    import { computed } from 'vue';

    import PublicHeader from '../Header/PublicHeader.vue';
    import NotFoundBlock from './NotFoundBlock.vue';

    import Sidebar from '@/components/Sidebar/Sidebar.vue';
    import AccountMenuCompany from '@/components/Company/AccountMenu/AccountMenu.vue';
    import AccountMenuFreelancer from '@/components/Freelancer/AccountMenu/AccountMenu.vue';
    import Content from '@/components/Content/Content.vue';

    import { useAuthStore } from '@/stores/auth.store';

    import ROLES from '@/constants/roles';
    import ROUTES_NAMES from '../../../constants/routesNames';
    

    const storeAuth = useAuthStore();
    const { userData } = storeToRefs(storeAuth);

    const isCompany = computed(() => userData.value?.userType === ROLES.COMPANY);
    const isFreelancer = computed(() => userData.value?.userType === ROLES.FREELANCER);

    const mainPageName = computed(() =>{
        if (isCompany.value) return ROUTES_NAMES.COMPANY_PROFILE;
        if (isFreelancer.value) return ROUTES_NAMES.FREELANCER_EMPLOYMENT;
        return ROUTES_NAMES.LOGIN;
    })
</script>

<style lang="scss" scoped>
    .not-found {
        display: flex;
        flex-direction: column;
        gap: 32px;
        align-items: center;
        width: 560px;
        margin-top: 72px;

        &__title {
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 8px;
        }
    }
</style>