<template>
    <div class="radiobutton__container" :class="{ 'radiobutton__container_outline': isBackground }">
        <label class="radiobutton__label" v-if="label">{{ label }}</label>
        <div class="radiobutton__content">
            <label 
            v-for="item in data" class="radiobutton__item" 
            :class="{ 'radiobutton__item_checked': inputValue === item.value, 'radiobutton__item_big': size === 'big' }"
            >
                <div class="radiobutton__radio" :class="{ 'radiobutton__radio_checked': inputValue === item.value }"></div>
                <input type="radio" class="radiobutton__element" v-model="inputValue" :value="item.value">
                {{ item.value }}
            </label>
        </div>
    </div>
</template>

<script setup>
    import { computed } from 'vue';

    const props = defineProps({
        label: { type: String },
        modelValue: { type: String },
        data: { type: Array, default: () => [] },
        size: { type: String },
        isBackground: { type: Boolean, default: false }
    })

    const emits = defineEmits(['update:modelValue']);

    const inputValue = computed({
        get() {
            return props.modelValue
        },
        set(value) {
            emits('update:modelValue', value)
        }
    });
</script>

<style lang="scss" scoped>
    .radiobutton {

        font-size: 14px;

        &__container {
            width: 100%;
            display: flex;
            flex-direction: column;
            gap: 16px;

            &_outline {
                padding: 8px 6px;
                border-radius: 6px;
                background: var(--gray-light);
                border: 1px solid transparent;

                &:hover {
                    border-color: var(--black-opacity-10);
                }
            }
        }
        &__label {
            color: var(--black-opacity-50);
            font-family: Golos Text;
            font-size: 13px;
            font-weight: 400;
            line-height: normal;
        }
        &__content {
            display: flex;
            flex-direction: column;
            gap: 16px;
        }
        &__item {
            display: flex;
            align-items: center;
            gap: 8px;

            color: var(--black);
            font-family: Golos Text;
            font-weight: 400;
            line-height: 140%;

            &_big {
                font-size: 18px;
            }

            &:hover:not(&_checked) {
                .radiobutton__radio {
                    border: 1px solid var(--black-opacity-10);
                    background: var(--black-opacity-10);
                    box-shadow: 0px 1px 3px 0px rgba(0, 0, 0, 0.15) inset;
                }
            }
        }
        &__element {
            display: none;
        }
        &__radio {
            display: flex;
            width: 20px;
            height: 20px;
            border-radius: 50%;
            background: var(--gray-light-hover);
            box-shadow: 0px 1px 3px 0px rgba(0, 0, 0, 0.15) inset;

            &_checked {
                position: relative;
                
                &::after {
                    content: "";
                    position: absolute;
                    top: 50%;
                    left: 50%;
                    display: flex;
                    width: 12px;
                    height: 12px;
                    border-radius: 50%;
                    background: var(--black);
                    transform: translate(-50%, -50%);
                }
            }
        }
    }
</style>