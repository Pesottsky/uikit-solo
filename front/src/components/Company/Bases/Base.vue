<template>
    <BaseHeader />
    <BaseTable />

    <RightSidebar ref="rightSidebarRef" />
</template>

<script setup>
    import BaseHeader from './BaseHeader.vue';
    import BaseTable from './BaseTable.vue';
    import RightSidebar from '../RightSidebar/RightSidebar.vue';
    import { provide, ref } from 'vue';

    const rightSidebarRef = ref(null);

    provide('openRightSidebar', () => rightSidebarRef.value?.open());
</script>

<style lang="scss" scoped>

</style>