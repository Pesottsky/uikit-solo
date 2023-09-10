<script setup>
import { useSlots, ref , provide} from 'vue'

const slots = useSlots()
const tabTitles = ref(slots.default().map(tab => tab.props.title))
const selectedTitle = ref(tabTitles.value[0])

provide('selectedTitle', selectedTitle)
</script>

<template>
    <div class="tabs">

        <ul class="tabs__header">

            <li 
                v-for="title in tabTitles" 
                :key="title"
                class="tabs__item"
                :class="{ selected: selectedTitle === title}"
                @click="selectedTitle = title"
            >
                {{ title }}
            </li>

        </ul>

        <slot />
    </div>
</template>

<style>
.tabs {
    margin: 0;
}

.tabs__header {
    list-style: none;
    padding: 0;
    margin: 0 0 16px 0;
    display: flex;
    gap: 8px;
}

.tabs__item {
    display: flex;
    padding: 8px 12px;
    gap: 4px;
    justify-content: center;
    cursor: pointer;
    color: var(--black-opacity-50);
}
.tabs__item:hover {
        box-shadow: 0px 2px 0px 0px var(--black-opacity-10);
        color: var(--black);
} 
.tabs__item.selected {
    box-shadow: 0px 2px 0px 0px var(--Black, #000);
    color: var(--black);
}

.tabs__content {
    background-color: var(--gray-light-hover);
    min-height: 200px;
    display: grid;
    padding: 10px;
}
</style>