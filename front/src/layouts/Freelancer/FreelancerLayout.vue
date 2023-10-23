<template>
    <div class="wrapper">
        <Sidebar>
            <AccountMenu />
        </Sidebar>
        <Content>
            <RouterView />
        </Content>
    </div>
</template>

<script setup>
    import Sidebar from '@/components/Sidebar/Sidebar.vue';
    import AccountMenu from '@/components/Freelancer/AccountMenu/AccountMenu.vue';
    import Content from '@/components/Content/Content.vue';

    import { useFreelancerStore } from '../../stores/freelancer.store';
    import { onMounted } from 'vue';

    const storeFreelancer = useFreelancerStore();

    onMounted(() => {
        storeFreelancer.getGrade();
        storeFreelancer.getLoading();
        storeFreelancer.getProfile();
    })
</script>