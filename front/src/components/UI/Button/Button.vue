<template>
    <button :class="['button', `button_${type}`, 'font-body', classSize]" :disabled="disabled" @click.stop="onClick">
        <i class="icon icon_spiner" v-if="loading">
            <SpinerWhiteIcon v-if="type == 'primary'" />
            <SpinerBlackIcon v-else />
        </i>
        {{ label }}
        <i v-if="icon" class="icon">
            <slot></slot>
        </i>
    </button>
</template>

<script setup>
    import { computed } from 'vue';
    import { SpinerBlackIcon, SpinerWhiteIcon } from '../../Icons';

    const props = defineProps({
        label: {
            type: String,
            default: ''
        },
        type: {
            type: String,
            default: 'primary'
        },
        disabled: {
            type: Boolean
        },
        icon: {
            type: Boolean
        },
        loading: {
            type: Boolean
        },
        size: { type: String, default: null }
    })

    const emits = defineEmits(['onClick'])

    const classSize = computed(() => props.size ? `button_${props.type}_${props.size}` : null);

    function onClick(e) {
        if (!props.disabled && !props.loading) {
            emits('onClick', e);
        }
    }

</script>

<style lang="scss" scoped>
    .button {
        padding: 8px 12px;
        gap: 4px;
        display: inline-flex;
        justify-content: center;
        align-items: center;

        background: var(--black);
        color: var(--white);
        border: 1px solid var(--black);
        border-radius: 8px;

        transition: background, 0.2s ease-in-out;

        &:enabled:hover {
            background: var(--primary-hover);
            cursor: pointer;
        }
        &:enabled:active {
            background: var(--black);
            color: var(--primary-color-active)
        }
        &:enabled:hover .icon {
            opacity: 1;
        }
        &:enabled:active .icon {
            opacity: .5;
        }
        &:disabled {
            opacity: .6;
        }
        &:focus {
            outline: none;
            box-shadow: 0 0 0 4px var(--focus);
        }

        &_secondary {
            background: #ffffff00;
            color: var(--black);
            border: 1px solid var(--black);

            &:enabled:hover {
                background: var(--secondary-hover);
            }
            &:enabled:active {
                color: var(--secondary-color-active);
            }
        }

        &_tetrary {
            background: #ffffff00;
            color: var(--black);
            border: 0px;

            &:enabled:hover {
                background: var(--tetrary-hover);
            }
            &:enabled:active {
                color: var(--tetrary-color-active);
            }
        }

        &_tiny {
            width: 16px;
            height: 16px;
            padding: 0px;

            background: #ffffff00;
            border: 0px;
            border-radius: 4px;

            &_big {
                padding: 16px;
            }

            &:enabled:hover{
                background: var(--secondary-hover);
                cursor: pointer;
            }
            &:enabled:active{
                background: var(--secondary-hover);
            }
        }
    }

    .icon {
        float: left;
        width: 16px;
        height: 16px;
        opacity: 0.5;

        &_spiner {
            animation: spiner 1.5s linear infinite;
            opacity: 1;
        }
    }

    @keyframes spiner {
        100% {
            transform: rotate(360deg);
        }
    }
</style>