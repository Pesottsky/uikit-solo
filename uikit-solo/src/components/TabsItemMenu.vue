<template>
    <button class= "TabsItemMenu font-body"
    :disabled="disabled" 
    >{{ label }} 
    </button>
</template>

<script setup>
    const props = defineProps ({
        label: {
            type: String,
            default: 'Press me'
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
.TabsItemMenu {
    padding: 8px 12px;
    gap: 4px;
    display: inline-flex;
    justify-content: left;
    align-items: center;

    background: #ffffff00;
    color: var(--black);
    border: 0px;
    border-radius: 8px;

    transition: background, 0.2s ease-in-out;

    &:enabled:hover{
        background: var(--tetrary-hover);
        cursor: pointer;
    }
    &:enabled:active{
        color: var(--tetrary-color-active)
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
.icon {
    float: right;
    width: 16px;
    height: 16px;
    opacity: 0.5;

}

</style>