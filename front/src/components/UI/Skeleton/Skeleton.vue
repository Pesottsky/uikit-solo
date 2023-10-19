<template>
    <div class="skeleton"></div>
</template>

<script setup>
    import { computed } from 'vue';

    const props = defineProps({
        size: { 
            type: Number, 
            default: 100,
            validator(value) {
                return value > 10 && value <= 100;
            } 
        }
    })

    const sizePercent = computed(() => `${props.size}%`);   

</script>

<style lang="scss" scoped>
    .skeleton {
        position: relative;
        width: v-bind(sizePercent);
        height: 9px;
        border-radius: 24px;
        background: var(--gray-light-hover);
        overflow: hidden;

        &::after {
            content: "";
            position: absolute;
            top: 0px;
            bottom: 0px;
            left: 0px;
            right: 0px;
            transform: translateX(-100%);
            background-image: linear-gradient(
                90deg,
                rgba(255, 255, 255, 0) 0,
                rgba(255, 255, 255, 0.2) 20%,
                rgba(255, 255, 255, 0.5) 60%,
                rgba(255, 255, 255, 0)
            );
            animation: skate 1.5s normal infinite;
            border-radius: 24px;
        }
    }

    @keyframes skate {
        100% {
            transform: translateX(100%);
        }
    }
</style>