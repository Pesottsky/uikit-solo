<template>
    <div class="context-menu__item" @click="onClick">
        <slot></slot>
        <div class="context-menu__text">{{ text }}</div>
    </div>
</template>

<script setup>
    defineProps({
        icon: Boolean,
        text: String
    })
    const emits = defineEmits(['onClick']);

    function onClick() {
        emits('onClick');
    }
</script>

<style lang="scss" scoped>
    .context-menu {
        &__item {
            display: flex;
            align-items: center;
            justify-content: flex-start;
            gap: 4px;
            padding: 8px 12px;
            white-space: nowrap;
            cursor: pointer;

            &:hover {
                background: var(--gray-light-hover);
                border-radius: 8px;
            }
        }
    }
</style>