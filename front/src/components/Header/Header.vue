<template>
    <header class="header" :class="{ 'header_company': isCompany }">
        <div class="header__title">
            <slot name="title"></slot>
        </div>
        <div class="header__action">
            <slot name="action"></slot>
        </div>
    </header>
</template>

<script setup>
    defineProps({
        isCompany: { type: Boolean }
    })
</script>

<style lang="scss" scoped>
    .header {
        width: 100%;
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 6px 0px 10px 0px;

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

        &_company {
            padding: 0px 0px 10px 0px;
        }
    }
</style>