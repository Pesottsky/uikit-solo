<script setup>
import ButtonUI from './components/ButtonUI.vue'
import IconBase from './components/IconBase.vue'
import IconWrite from './components/icons/IconWrite.vue'
</script>

<template>
  <h1 class="heading-1">Кнопки и цвета</h1>
  
  <div class="line"> 
    <ButtonUI label="Primary" color="primary"/>
    <ButtonUI label="Secondary" color="secondary"/>
    <ButtonUI label="Tetrary" color="tetrary" />
    <icon-base icon-name="write"><icon-write /></icon-base>
  </div>
  
</template>

<style scoped>
.line{
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin: 20px 0px;
}
</style>
