<script>
import InputText from '../common/InputText.vue'
import ButtonUI from '../common/ButtonUI.vue'


export default {
    name: "Company",
    components: {
        InputText,
        ButtonUI
    },
    data() {
        return {
            company: "123"
        }
    },
    methods: {
        companyChange(company) {
            this.company = company
        }
    }
}
</script>

<template>
    <div class="reg">
        <InputText :value="company" label="Компания" placeholder="Название" @input="companyChange"/>
        <InputText placeholder="Почта" />
        <InputText style="margin-top: 4px;" label="Пароль" placeholder="пароль" />
        <InputText placeholder="подтверждение пароля" />
        <ButtonUI style="width: 100%; margin-top: 12px;" label="Зарегистрироваться" color="primary"/>

    </div>
</template>


<style scoped>
.reg {
    padding: 32px;
}
</style>
