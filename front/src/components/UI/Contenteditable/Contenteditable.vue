<template>
    <span
        v-if="tag === 'span'"
        @input="onInput"
        @click="onClick"
        @blur="onReset"
        contenteditable="true"
        class="editable"
    >{{ currentValue }}</span>
    <h1 
        v-else
        @input="onInput"
        @click="onClick"
        @blur="onReset"
        contenteditable="true"
        class="editable"
    >{{ currentValue }}</h1>
</template>

<script setup>
    import { computed, ref } from 'vue';

    const props = defineProps({
        tag: { type: String, default: 'span' },
        placeholder: { type: String, default: 'Пусто' },
        modelValue: { type: String },
    })

    const emits = defineEmits(['update:modelValue']);

    const editable = ref(false);
    const currentValue = computed(() => {
        if (editable.value && !props.modelValue) {
            return '';
        }
        return props.modelValue ? props.modelValue : props.placeholder;
    });

    function onInput(e) {
        console.log(e.target.innerText);
        emits('update:modelValue', e.target.innerText);
    }
    function onClick() {
        editable.value = true;
    }
    function onReset() {
        editable.value = false;
    }

</script>

<style lang="scss" scoped>
    .editable {
        height: inherit;
        display: flex;
        align-items: center;
    }
</style>