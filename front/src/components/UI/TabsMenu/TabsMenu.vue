<template>
    <ul class="tabs">
        <li 
            class="tabs__item" 
            v-for="item in list" 
            :key="item.id" 
            :class="{ 'tabs__item_selected': item.selected }"
            @click="item.action"
        >{{ item.text }}</li>
    </ul>
</template>

<script setup>
    const props = defineProps({
        list: { type: Array, default: () => [] }
    })
</script>

<style lang="scss" scoped>
    .tabs {
        list-style: none;
        padding: 0;
        display: flex;
        gap: 8px;
        justify-content: center;

        &__item {
            display: flex;
            padding: 8px 12px;
            gap: 4px;
            justify-content: center;
            cursor: pointer;
            color: var(--black-opacity-50);

            &:hover:not(&_selected) {
                box-shadow: 0px 2px 0px 0px var(--black-opacity-10);
                color: var(--black);
            } 
            &_selected {
                box-shadow: 0px 2px 0px 0px var(--Black, #000);
                color: var(--black);
            }
        }
    }
</style>