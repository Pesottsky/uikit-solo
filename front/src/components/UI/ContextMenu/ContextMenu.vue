<template>
    <div class="context-menu">
        <slot></slot>
    </div>
</template>

<script setup>

</script>

<style lang="scss" scoped>
    .context-menu {
        position: absolute;
        display: inline-flex;
        padding: 8px;
        flex-direction: column;
        align-items: flex-start;
        gap: 8px;

        border-radius: 6px;
        background: var(--white);
        box-shadow: 0px 4px 24px 0px rgba(0, 0, 0, 0.20);
    }
</style>