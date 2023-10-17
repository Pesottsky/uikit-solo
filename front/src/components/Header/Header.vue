<template>
    <header class="header">
        <div class="header__title">
            <slot name="title"></slot>
        </div>
        <div class="header__action">
            <slot name="action"></slot>
        </div>
    </header>
</template>

<script setup>

</script>

<style lang="scss" scoped>
    .header {
        width: 100%;
        display: flex;
        align-items: center;
        justify-content: space-between;

        &__title {
            display: flex;
            justify-content: flex-start;
            align-items: center;
            gap: 8px;
        }
        &__action {
            display: flex;
            flex-direction: row;
            justify-content: flex-end;
            align-items: center;
            gap: 5px;
        }
    }
</style>