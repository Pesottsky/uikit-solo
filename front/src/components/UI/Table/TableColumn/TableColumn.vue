<template>
    <div class="table__column" :class="clasName[alignContent]">
        <slot></slot>
    </div>
</template>

<script setup>
    defineProps({
        span: { type: Number, default: 1 },
        alignContent: { type: String, default: 'left' }
    })

    const clasName = {
        left: 'table__column_left',
        center: 'table__column_center',
        right: 'table__column_right'
    }

</script>

<style lang="scss" scoped>
    .table__column {
        grid-column: span v-bind(span);
        padding: 10px 8px;
        border-right: 1px solid var(--gray-light);
        display: flex;
        align-items: flex-start;
        justify-content: flex-start;

        span {
            overflow: hidden;
            text-overflow: ellipsis;
        }
        

        &:last-child {
            border-right: none;
        }

        &_center {
            justify-content: center;
        }
        &_left {
            justify-content: left;
        }
        &_right {
            justify-content: right;
        }
    }

    .table__row_header {
        .table__column {
            border: none;
        }
    }
</style>