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
    import { provide, ref } from 'vue';

    const rightSidebarRef = ref(null);
    const inviteModalRef = ref(null);

    provide('openRightSidebar', () => rightSidebarRef.value?.open());
    provide('openInviteModal', () => inviteModalRef.value?.open());
</script>

<style lang="scss" scoped>

</style>