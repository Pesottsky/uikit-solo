<template>
    <button :class="['ButtonUI', `ButtonUI_${color}`]"
    >{{ label }} 
   
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
        
    })

</script>

<style>
.ButtonUI{
    display: inline-flex;
    padding: 8px 12px;
    justify-content: center;
    align-items: center;
    gap: 4px;

    border-radius: 8px;
    border: 1px solid var(--black);
    background: var(--black);
    color: var(--white);

    &:hover{
        background: var(--white);
    }
   
}
.ButtonUI_secondary {
        background: var(--white);
        border: 1px solid #000;
    }
.ButtonUI_tetrary {
        border: 1px solid #000;
        background: #f50202;
    }
</style>