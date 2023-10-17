<template>
    <div class="backdrop" @click.self="closeWindow">
        <slot />
    </div>
</template>

<script setup>
    const emits = defineEmits(['onClose']);

    function closeWindow() {
        emits('onClose');
    }
</script>

<style lang="scss" scoped>
    .backdrop {
        position: fixed;
        top: 0px;
        bottom: 0px;
        left: 0px;
        right: 0px;
        background: transparent;
    }
</style>