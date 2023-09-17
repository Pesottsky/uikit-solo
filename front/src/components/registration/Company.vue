<script>
import InputText from '../common/InputText.vue'
import ButtonUI from '../common/ButtonUI.vue'
import { registration } from "../../services/AuthService"
import { validateEmail } from '../../utils/validation'

export default {
    name: "Company",
    components: {
        InputText,
        ButtonUI
    },
    data() {
        return {
            name: undefined,
            email: undefined,
            password: undefined,
            cpassword: undefined,
            emailError: undefined,
            passwordError: undefined,
            nameError: undefined
        }
    },
    methods: {
        onCompany(value) {
            this.name = value
            this.nameError = undefined
        },
        onEmail(value) {
            this.email = value
            this.emailError = undefined
        },
        onPassword(value) {
            this.password = value
            this.passwordError = undefined
        },
        onCPassword(value) {
            this.cpassword = value
            this.passwordError = undefined
        },
        onClick() {
            let hasErrors = false
            if (!validateEmail(this.email)) {
                this.emailError = Errors.Email
                hasErrors = true
            }
            else this.emailError = undefined

            if (this.name) this.nameError = undefined
            else {
                this.nameError = Errors.Name
                hasErrors = true
            }

            if (this.password === this.cpassword && this.password) this.passwordError = undefined
            else {
                this.passwordError = Errors.Password
                hasErrors = true
            }

            if (!hasErrors) {
                registration(this.name, this.email, this.password).then(this.$router.push(this.$route.query.redirect ? this.$route.query.redirect : "/")).catch(error => {
                    if (error.response.status == 400) {
                        this.emailError = error.response.data
                    } else {
                        this.passwordError = "Ошибка на стороне сервера"
                    }
                })
            }
        },
    },
}

const Errors = {
    Email: "Укажите корректный email",
    Name: "",
    Password: "Пароли не совпадают",
}
</script>

<template>
    <div class="reg column">
        <InputText :value="name" label="Компания" placeholder="Название" @input="onCompany" :error="nameError"/>
        <InputText :value="email" placeholder="Почта" @input="onEmail" :error="emailError" />
        <InputText :value="password" style="margin-top: 16px;" label="Пароль" placeholder="Пароль" type="password" 
            @input="onPassword" :error="passwordError ? '' : undefined"/>
        <InputText :value="cpassword" placeholder="Подтверждение пароля" type="password" :error="passwordError"
            @input="onCPassword" />
        <ButtonUI style="width: 100%; margin-top: 24px;" label="Зарегистрироваться" color="primary" @onClick="onClick" />
    </div>
</template>


<style scoped>
.reg {
    padding: 32px;
}
</style>
