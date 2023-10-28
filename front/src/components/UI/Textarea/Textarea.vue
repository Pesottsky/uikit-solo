<template>
    <div class="textarea__container">
        <div class="textarea__effect column">
            <label v-if="label" class="textarea__label">{{ label }}</label>
            <textarea
                v-model="inputValue" 
                :placeholder="placeholder" 
                class="textarea__element"
                :class="{ invalid: error, 'textarea__element_headless': isHeadless }"
                ref="textareaRef"
                :readonly="readonly"
                @input="onInput"
            ></textarea>
        </div>
        <span
            v-if="hint || error"
            class="textarea__hint"
            :class="{ 'textarea__hint_error': error }"
        >{{ hint || error }}</span>
    </div>
</template>

<script setup>
    import { computed, onMounted, ref, watch, nextTick } from 'vue';

    const props = defineProps({
        modelValue: { type: String },
        label: { type: String },
        placeholder: { type: String, default: 'Мой ответ' },
        hint: { type: String },
        error: { type: String },
        isHeadless: { type: Boolean, default: false },
        readonly: { type: Boolean }
    })

    const emits = defineEmits(['update:modelValue']);

    const textareaRef = ref(null);

    const inputValue = computed({
        get() {
            return props.modelValue
        },
        set(value) {
            emits('update:modelValue', value)
        }
    });

    function onInput(e) {
        e.target.style.height = 0;
        e.target.style.height = (e.target.scrollHeight + 1) + "px";
    }

    onMounted(async () => {
        await nextTick();
        if (props.modelValue) {
            const event = new Event('input');
            textareaRef.value.dispatchEvent(event);
        }
    })

</script>

<style lang="scss" scoped>
    ::placeholder {
        color: var(--black-opacity-50);
    }

    .textarea {
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
            gap: 4px;
        }
        &__element {
            color: var(--black);
            background: var(--gray-light);
            border-radius: 6px;
            border: 1px solid transparent;
            outline: none;

            overflow-y: hidden;
            resize: none;
            height: 41px;
        
            display: flex;
            width: 100%;
            padding: 10px 12px;
            gap: 8px;

            font-family: Golos Text;
            font-size: 14px;
            font-weight: 400;
            line-height: 140%;

            &_headless {
                height: 34px;
                padding: 7px 6px;
                background-color: transparent;

                &::placeholder {
                    color: var(--black-opacity-30);
                }

                &:focus {
                    border: 1px solid var(--black-opacity-30);
                }
                &:hover:not(&:focus) {
                    background: var(--gray-light-hover);
                }
            }

            &:hover:not(&_headless) {
                background: var(--gray-light-hover);
                border: 1px solid var(--black-opacity-10);
            }
            &:focus:not(&_headless) {
                background: transparent;
                border: 1px solid var(--black);
            }

            /* скрываем плэйсхолдер по клику */
            &:focus::-webkit-input-placeholder:not(&_headless) {
                color: transparent;
            }
            
            &:focus:-moz-placeholder:not(&_headless) {
                color: transparent;
            }
            
            /* FF 4-18 */
            &:focus::-moz-placeholder:not(&_headless) {
                color: transparent;
            }
            
            /* FF 19+ */
            &:focus:-ms-input-placeholder:not(&_headless) {
                color: transparent;
            }

            &:checked {
                color: var(--black);
                background: var(--gray-light);
                border: 1px solid transparent;
            }

            &:read-only {
                pointer-events: none;
            }
        }
        &__hint {
            float: left;
            width: 100%;
            position: relative;
            font-size: 12px;
            opacity: .6;
            margin-left: 12px;
            line-height: 140%;

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