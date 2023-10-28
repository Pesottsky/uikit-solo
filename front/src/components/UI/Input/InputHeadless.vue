<template>
    <div class="buffer" :class="{ 'buffer_title': isTitle, 'buffer_subtitle': isSubTitle }" ref="bufferRef"></div>
    <input 
        ref="inputRef"
        :type="type" 
        class="input-headless" 
        :class="{ 'input-headless_max': isMax, 'input-headless_title': isTitle, 'input-headless_subtitle': isSubTitle }" 
        :style="inputStyle" 
        :placeholder="placeholder"
        :readonly="readonly"
        v-model="inputValue"
        @input="onInput"
        @focusout="focusOut"
    >
</template>

<script setup>
    import { computed, onMounted, reactive, ref, nextTick, watch } from 'vue';

    const props = defineProps({
        type: { type: String },
        modelValue: { type: [String, Number] },
        placeholder: { type: String, default: 'Мой ответ' },
        isMax: { type: Boolean, default: true },
        isTitle: { type: Boolean, default: false },
        isSubTitle: { type: Boolean, default: false },
        readonly: { type: Boolean }
    })

    const emits = defineEmits(['update:modelValue', 'onFocusOut']);

    const bufferRef = ref(null);
    const inputRef = ref(null);


    const inputValue = computed({
        get() {
            return props.modelValue
        },
        set(value) {
            if (value < 0 && props.type == 'number') value = null;
            bufferRef.value.textContent = value || props.placeholder;
            emits('update:modelValue', value)
        }
    });

    const inputStyle = reactive({ width: `${bufferRef.value?.clientWidth + 16}px` });

    function onInput() {
        if (props.isMax) return;
        inputStyle.width = `${bufferRef.value?.clientWidth + 16}px`
    }
    function setFocus() {
        inputRef.value.focus();
    }
    function focusOut() {
        emits('onFocusOut');
    }

    watch(props, () => {
        if (bufferRef.value.textContent === 'Мой ответ') {
            bufferRef.value.textContent = props.modelValue || props.placeholder;
            onInput();
        }
    })

    onMounted(async () => {
        await nextTick();
        bufferRef.value.textContent = props.modelValue || props.placeholder;
        onInput();
    })

    defineExpose({
        setFocus
    })

</script>

<style lang="scss" scoped>
    .input-wrapper {
        width: 100%;
        position: relative;
    }
    .input-headless {
        max-width: 100%;
        height: 36px;
        border: 1px solid transparent;
        outline: none;
        padding: 1px 6px;
        border-radius: 6px;
        background: transparent;

        color: var(--black);
        font-family: Golos Text;
        font-size: 14px;
        font-weight: 400;
        line-height: 140%;

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
        &_subtitle {
            font-family: Antonym;
            font-size: 24px;
            font-weight: 500;
            line-height: 100%;
            height: 35px;
        }

        &:focus {
            border-color: var(--black-opacity-30);
        }

        &::placeholder {
            color: var(--black-opacity-30);
        }
        &:hover:not(&:focus) {
            background: var(--gray-light-hover);
        }

        &:read-only {
            pointer-events: none;
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
        &_subtitle {
            font-family: Antonym;
            font-size: 24px;
            font-weight: 500;
            line-height: 100%;
        }
    }
</style>