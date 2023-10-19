<template>
    <input 
        type="text" 
        class="input-headless" 
        :class="{ 'input-headless_max': isMax, 'input-headless_title': isTitle }" 
        :style="inputStyle" 
        :placeholder="placeholder" 
        v-model="inputValue" 
        @input="onInput"
    >
    <div class="buffer" :class="{ 'buffer_title': isTitle }" ref="bufferRef">{{ inputValue || placeholder }}</div>
</template>

<script setup>
    import { computed, onMounted, reactive, ref } from 'vue';

    const props = defineProps({
        modelValue: { type: String },
        placeholder: { type: String, default: 'Мой ответ' },
        isMax: { type: Boolean, default: true },
        isTitle: { type: Boolean, default: false }
    })

    const emits = defineEmits(['update:modelValue']);

    const bufferRef = ref(null);

    const inputValue = computed({
        get() {
            return props.modelValue
        },
        set(value) {
            emits('update:modelValue', value)
        }
    });
    const inputStyle = reactive({ width: `${bufferRef.value?.clientWidth}px` });

    function onInput() {
        if (props.isMax) return;
        inputStyle.width = `${bufferRef.value?.clientWidth + 14}px`
    }

    onMounted(() => onInput())

</script>

<style lang="scss" scoped>
    .input-wrapper {
        width: 100%;
        position: relative;
    }
    .input-headless {
        max-width: 100%;
        height: 20px;
        border: 1px solid transparent;
        outline: none;
        padding: 1px 6px;
        border-radius: 6px;

        &_max {
            width: 100%;
        }
        &_title {
            font-family: Antonym;
            font-size: 32px;
            font-weight: 500;
            line-height: 100%;
            height: 48px;
        }

        &:focus {
            border-color: var(--black-opacity-30);
        }

        &::placeholder {
            color: var(--black-opacity-30);
        }

    }
    .buffer {
        position: absolute;
        top: 0px;
        left: 0px;
        border: 1px solid red;
        white-space: nowrap;
        visibility: hidden;

        &_title {
            font-family: Antonym;
            font-size: 32px;
            font-weight: 500;
            line-height: 100%;
        }
    }
</style>