<template>
    <div class="notice" v-if="show">
        {{ textMessage }}
    </div>
</template>

<script setup>
    import { useNoticeStore } from '@/stores/notice.store';
    import { storeToRefs } from 'pinia';

    const store = useNoticeStore();
    const { show, textMessage } = storeToRefs(store);
</script>

<style lang="scss" scoped>
    .notice {
        position: fixed;
        bottom: 40px;
        right: 50%;
        transform: translateX(50%);
        border-radius: 4px;
        padding: 14px 16px;
        background-color: var(--black-opacity-50);
        color: var(--white);
        z-index: 10;
    }
</style>