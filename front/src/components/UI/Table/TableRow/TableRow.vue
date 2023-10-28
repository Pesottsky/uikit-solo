<template>
    <div 
        class="table__row"
        :class="{ 'table__row_header': isHeader, 'table__row_fake': isFake, 'table__row_hover_disabled': !isHover, 'table__row_selected': selected }">
        <slot></slot>
    </div>
</template>

<script setup>
    defineProps({
        isHeader: { type: Boolean, default: false },
        count: { type: Number, default: 1 },
        isHover: { type: Boolean, default: true },
        isFake: { type: Boolean, default: false },
        selected: { type: Boolean, default: false }
    })
</script>

<style lang="scss" scoped>
    .table__row {
        display: grid;
        grid-template-columns: repeat(v-bind(count), minmax(50px, 1fr));
        font-size: 14px;
        color: var(--black);
        font-family: Golos Text;
        line-height: 140%;
        border-bottom: 1px solid var(--gray-light);

        &:last-child {
            border-bottom: none;
        }

        &_header {
            color: var(--black-opacity-50);
            font-size: 12px;
        }
        &_fake {
            color: var(--black-opacity-50);
        }
        &_selected {
            background: var(--gray-light-hover);
        }

        &:hover:not(&_header):not(&_hover_disabled) {
            background: var(--gray-light-hover);
            cursor: pointer;
        }
    }
</style>