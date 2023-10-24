<template>
    <BaseHeader />
    <BaseTable />

    <RightSidebar ref="rightSidebarRef" />
    <InviteModal ref="inviteModalRef" />
</template>

<script setup>
    import BaseHeader from './BaseHeader.vue';
    import BaseTable from './BaseTable.vue';
    import RightSidebar from '../RightSidebar/RightSidebar.vue';
    import InviteModal from '../Modals/InviteModal.vue';
    import { onMounted, provide, ref } from 'vue';
    import { useFreelancerStore } from '../../../stores/freelancer.store';

    const storeFreelancer = useFreelancerStore();

    const rightSidebarRef = ref(null);
    const inviteModalRef = ref(null);

    onMounted(() => {
        storeFreelancer.getGrade();
    })

    provide('openRightSidebar', () => rightSidebarRef.value?.open());
    provide('openInviteModal', (data, props) => inviteModalRef.value?.open(data, props));
</script>

<style lang="scss" scoped>

</style>