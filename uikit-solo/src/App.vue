<script setup>
import ButtonUI from './components/ButtonUI.vue'
import TabsItemMenu from './components/TabsItemMenu.vue'
import TabsWrapper from './components/TabsWrapper.vue'
import TabItem from './components/tabsitem.vue'
import ButtonTinyUI from './components/ButtonTinyUI.vue'
import RadioBox from './components/RadioBox.vue'
import InputText from './components/InputText.vue'
import HeaderCompanyUI from './components/HeaderCompanyUI.vue'
import HeaderFreelancerUI from './components/HeaderFreelancerUI.vue'
import HeaderPublicUI from './components/HeaderPublicUI.vue'
import ChipsUI from './components/ChipsUI.vue'

</script>

<template>

  <img src="../src/assets/icons/Soloteamlogo.svg" alt="Soloteam logo" height="32"/>

  <h1>Кнопки и цвета</h1>
  
  <div class="line"> 
    <ButtonUI label="Primary" color="primary" icon="url(../src/assets/icons/import.svg) no-repeat"/>
    <ButtonUI label="Secondary" color="secondary" icon="url(../src/assets/icons/plus-black.svg) no-repeat" />
    <ButtonUI label="Tetrary" color="tetrary" icon="url(../src/assets/icons/plus-black.svg) no-repeat" />
    <icon-base icon-name="write"><icon-write /></icon-base>
  </div>

  <div class="line"> 
    <ButtonUI label="Primary" color="primary" icon="url(../src/assets/icons/import.svg) no-repeat" :disabled="true" />
    <ButtonUI label="Secondary" color="secondary" :disabled="true"/>
    <ButtonUI label="Tetrary" color="tetrary" :disabled="true"/>
  </div>

  <div class="line"> 
    <TabsItemMenu label="Новая таблица"/>
  </div>
  
  <TabsWrapper>
        <TabItem title="Как компания">Content from Tab 1</TabItem>
        <TabItem title="Как фрилансер">Content from Tab 2 Lorem ipsum dolor sit amet.</TabItem>
        <TabItem title="Еще таб">Content from Tab 3 Lorem ipsum dolor sit amet consectetur, adipisicing elit. Voluptates, ipsa.</TabItem>
  </TabsWrapper>

  <div class="line"> 
    <ButtonTinyUI icon="url(../src/assets/icons/plus-black.svg) no-repeat"/>
    <ButtonTinyUI icon="url(../src/assets/icons/three-dots-black.svg) no-repeat"/>
  </div>

<div class="line"> 
    <RadioBox name="radio one"/>
    <RadioBox name="radio two"/>
</div>

<div class="line" style="gap:0px"> 
  <InputText v-model="firstname" placeholder="your first name"/>
  <InputText v-model="email" hint="Email address"  placeholder="your email" />
</div>

<a href="*">Пример ссылки на что-то</a>

<div class="line"> 
<HeaderCompanyUI></HeaderCompanyUI>
<HeaderFreelancerUI></HeaderFreelancerUI> 
<HeaderPublicUI></HeaderPublicUI>
</div>

<div class="line"> 
<ChipsUI label="Свободен" color="free"/>
<ChipsUI label="Есть время" color="available"/>
<ChipsUI label="Занят" color="busy"/>
<ChipsUI label="Не известно" color="disabled"/>
</div>

</template>


<style scoped>
.line{
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin: 20px 0px;
}

#app {
    font-family: Avenir, Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
    margin-top: 60px;
}

</style>
