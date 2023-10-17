<template>
    <ContextMenu v-if="isShow" :style="coords" ref="contextMenuRef">
        <ContextMenuItem text="Импортировать базу" :icon="true" @on-click="clickItem">
            <ImportIcon />
        </ContextMenuItem>
    </ContextMenu>
</template>

<script setup>
    import { inject, reactive, ref } from 'vue';
    import { ContextMenu, ContextMenuItem } from '../../UI';
    import { ImportIcon } from '../../Icons';
    import clickOutside from '@/directives/clickOutside';

    const openImportModal = inject('openImportModal');

    const isShow = ref(false);
    const contextMenuRef = ref(null);
    const coords = reactive({
        top: 0,
        left: 0
    })

    clickOutside(contextMenuRef, onClose);

    function clickItem() {
        onClose();
        openImportModal();
    }

    function onOpen({ x, y }) {
        coords.top = `${x}px`;
        coords.left = `${y}px`;
        isShow.value = true;
    }
    function onClose() {
        isShow.value = false;
    }

    defineExpose({
        open: onOpen,
        close: onClose
    })
</script>

<style lang="scss" scoped>

</style>