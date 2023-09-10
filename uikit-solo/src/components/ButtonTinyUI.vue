<template>
    <button class="ButtonTinyUI"
    :disabled="disabled" 
    ><i v-if="icon!=undefined" :style="{background:icon}" class="icon"></i>
    </button>

</template>

<script setup>

    const props = defineProps ({
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
.ButtonTinyUI {
    display: flex;
    width: 16px;
    height: 16px;
    padding: 0px;

    background: #ffffff00;
    border: 0px;
    border-radius: 4px;
    
    transition: background, 0.2s ease-in-out;

    &:enabled:hover{
        background: var(--secondary-hover);
        cursor: pointer;
    }
    &:enabled:active{
        background: var(--secondary-hover);
    } 
    &:enabled:hover .icon {
      opacity: 1;
    }
    &:enabled:active .icon {
      opacity: .6;  
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
    width: 16px;
    height: 16px;
    opacity: 0.6;
}

</style>