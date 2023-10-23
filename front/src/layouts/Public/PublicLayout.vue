<template>
    <PublicHeader />
    <main class="content">
        <RouterView />
    </main>

</template>

<script setup>
    import PublicHeader from '../../components/Public/Header/PublicHeader.vue';
</script>

<style lang="scss" scoped>
    .content {
        display: flex;
        justify-content: center;
        align-items: flex-start;
    }
</style>