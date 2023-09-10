<script setup>
import ButtonUI from './ButtonUI.vue'

</script>

<template>
    
   <header>
    <div class="header-Company-UI">
        <h2>Фрилансеры</h2>
        <div class="header-buttons">
            <ButtonUI label="Добавить" color="tetrary" icon="url(../src/assets/icons/add-person.svg) no-repeat"/>
            <ButtonUI label="Пригласить" color="tetrary" icon="url(../src/assets/icons/import-black.svg) no-repeat" />
            <ButtonUI label="" color="tetrary" icon="url(../src/assets/icons/big-three-dots-black.svg) no-repeat" />
        </div>
    </div>
   </header>
</template>

<style scoped>
.header-Company-UI {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
    width: 1023px;
    height: 40px;
    padding: 0px 0px 0px 48px;
    gap: 8px;
}
.header-buttons {
    display: flex;
    align-items: center;
    justify-content: flex-end;
}
</style>