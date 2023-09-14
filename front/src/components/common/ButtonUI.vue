<template>
    <button :class="['ButtonUI', `ButtonUI_${color}`, 'font-body']"
    :disabled="disabled" 
    >{{ label }} 
    <i v-if="icon!=undefined" :style="{background:icon}" class="icon"></i>
    </button>

</template>

<script setup>

    const props = defineProps ({
        label: {
            type: String,
            default: 'Press me'
        },
        color:{
            type: String,
            default: 'primary'
        },
        disabled: {
            type: Boolean,
            default: false
        },
        icon: {
            type: String,
            default: undefined
        }
        
    })

</script>

<style scoped>
.ButtonUI {
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

    &:enabled:hover{
        background: var(--primary-hover);
        cursor: pointer;
    }
    &:enabled:active{
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
}

.ButtonUI_secondary {
    background: #ffffff00;
    color: var(--black);
    border: 1px solid #000;
    &:enabled:hover {
        background: var(--secondary-hover);
    }
    &:enabled:active {
        color: var(--secondary-color-active);
    }
}
.ButtonUI_tetrary {
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
.icon {
    float: left;
    width: 16px;
    height: 16px;
    opacity: 0.5;

}

</style>