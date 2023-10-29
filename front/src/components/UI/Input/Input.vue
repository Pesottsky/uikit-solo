<template>
    <div class="input__container">
        <div class="input__effect column">
            <label v-if="label" class="input__label">{{ label }}</label>
            <input 
                :type="type"
                :placeholder="placeholder"
                v-model="inputValue"
                class="input__element"
                :class="{ invalid: error }"
                @focus="$emit('onFocus')"
            />
        </div>
        <span
            v-if="hint || error"
            class="input__hint"
            :class="{ 'input__hint_error': error }"
        >{{ hint || error }}</span>
    </div>
</template>

<script setup>
    import { computed } from 'vue';

    const props = defineProps({
        label: { type: String },
        type: { type: String, default: 'text' },
        modelValue: { type: [String, Number] },
        placeholder: { type: String, default: 'Мой ответ' },
        hint: { type: String },
        error: { type: String }
    })

    const emits = defineEmits(['update:modelValue', 'onFocus']);

    const inputValue = computed({
        get() {
            return props.modelValue
        },
        set(value) {
            if (value < 0 && props.type == 'number') value = null;
            emits('update:modelValue', value)
        }
    });

</script>
  
<style lang="scss" scoped>
    ::placeholder {
        color: var(--black-opacity-50);
    }

    .input {
        &__container {
            width: 100%;
            display: flex;
            flex-direction: column;
            gap: 4px;
        }
        &__label {
            opacity: .5;
            margin-left: 12px;
        }
        &__effect {
            float: left;
            width: 100%;
            position: relative;
            display: flex;
            flex-direction: column;
            gap: 8px;
        }
        &__element {
            color: var(--black);
            background: var(--gray-light);
            border-radius: 6px;
            border: 1px solid transparent;
            outline: none;
        
            display: flex;
            width: 100%;
            padding: 12px 12px;
            gap: 8px;
            font-family: inherit;

            &:hover {
                background: var(--gray-light-hover);
                border: 1px solid var(--black-opacity-10);
            }
            &:focus {
                background: transparent;
                border: 1px solid var(--black);
            }

            /* скрываем плэйсхолдер по клику */
            &:focus::-webkit-input-placeholder {
                color: transparent;
            }
            
            &:focus:-moz-placeholder {
                color: transparent;
            }
            
            /* FF 4-18 */
            &:focus::-moz-placeholder {
                color: transparent;
            }
            
            /* FF 19+ */
            &:focus:-ms-input-placeholder {
                color: transparent;
            }

            &:checked {
                color: var(--black);
                background: var(--gray-light);
                border: 1px solid transparent;
            }
        }
        &__hint {
            float: left;
            width: 100%;
            position: relative;
            font-size: 12px;
            opacity: .6;
            line-height: 140%;
            margin-left: 12px;

            &_error {
                color: var(--input-error);
            }
        }
    }
    
    .invalid {
        border: 1px solid var(--input-error) !important;
        color: var(--input-error) !important;
    }
    
</style>