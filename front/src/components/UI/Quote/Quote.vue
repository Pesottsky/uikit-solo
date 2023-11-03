<template>
    <div class="quote" :class="sizeQuote">
        <slot></slot>
    </div>
</template>

<script setup>
    import { computed } from 'vue';

    const props = defineProps({
        size: { type: String }
    })

    const sizeQuote = computed(() => props.size ? `quote_size_${props.size}` : null);
</script>

<style lang="scss" scoped>
    .quote {
        padding: 4px 8px;
        line-height: 140%;
        border-left: 3px solid var(--black-opacity-30);
        background: var(--biege-light);

        font-family: Golos Text;
        font-size: 14px;
        font-weight: 400;
        line-height: 140%;

        &_size {
            &_small {
                font-size: 12px;
            }
        }
    }
</style>