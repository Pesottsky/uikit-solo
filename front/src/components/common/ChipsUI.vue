<template>
    <button :class="['ChipsUI', `ChipsUI_${color}`, 'font-body']"
    :disabled="disabled" 
    >{{ label }} 
    <!-- <i v-if="icon!=undefined" :style="{background:icon}" class="icon"></i> -->
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
        // icon: {
        //     type: String,
        //     default: undefined
        // }
        
    })

</script>

<style scoped>
.ChipsUI, .ChipsUI_free {
    padding: 2px 8px;
    height: 24px;
    gap: 4px;
    display: inline-flex;
    justify-content: center;
    align-items: center;

    background: rgba(167, 255, 54, 0.40);
    color: rgba(66, 116, 0, 1);
    border-radius: 6px;
    border: 0px;
}
.ChipsUI_available {
    background: rgba(255, 215, 73, 0.40);
    color: rgba(129, 76, 27, 1)
}
.ChipsUI_busy {
    background: rgba(255, 188, 150, 0.40);
    color: rgba(216, 65, 0, 1)
}

.ChipsUI_disabled {
    background: var(--gray-light-hover);
    color: var(--black-opacity-50);
}

/* .icon {
    float: left;
    width: 16px;
    height: 16px;
    opacity: 0.5;

} */

</style>