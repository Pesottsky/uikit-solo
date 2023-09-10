<template>
    <input class="radiobox" type="radio" name="rb" id="rb2" :disabled="disabled"> 
    <label for="rb2"> {{ name }} </label>
</template>

<script setup>

    const props = defineProps ({
        name: {
            type: String,
            default: 'Radio button'
        },
        disabled: {
            type: Boolean,
            default: false
        },
      
    })

</script>

<style scoped>


</style>