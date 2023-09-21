<script>
import HeaderPublicUI from '../components/common/HeaderPublicUI.vue'
import { login } from "../services/AuthService"
import InputText from '../components/common/InputText.vue'
import ButtonUI from '../components/common/ButtonUI.vue'
import { validateEmail } from '../utils/validation'
import { set, accessKey, refreshKey, userType } from '../localstorage'


export default {
    name: "Authorization",
    components: {
        InputText,
        ButtonUI,
        HeaderPublicUI
    },
    data() {
        return {
            email: "1@1.ru",
            password: "1",
            emailError: undefined,
            passwordError: undefined,
        }
    },
    methods: {
        onEmail(value) {
            this.email = value
            this.emailError = undefined
        },
        onPassword(value) {
            this.password = value
            this.passwordError = undefined
        },
        onClick() {
            let hasErrors = false
            if (!validateEmail(this.email)) {
                this.emailError = ""
                hasErrors = true
            }
            else this.emailError = undefined

            if (this.password) this.passwordError = undefined
            else {
                this.passwordError = ""
                hasErrors = true
            }

            if (!hasErrors) {
                login(this.email, this.password).then().catch(error => {
                    if (error.response.status == 400) {
                        this.passwordError = error.response.data
                        this.emailError = ""
                    } else {
                        this.passwordError = "Ошибка на стороне сервера"
                    }
                }).then(response => {
                    set({
                        accessKey: response.data.access,
                        refreshKey: response.data.refresh,
                        userType: response.data.userType
                    })
                    // this.$router.push(this.$route.query.redirect ? this.$route.query.redirect : "/")
                    this.$router.push("/")

                })
            }
        },
    }
}
</script>

<template>
    <HeaderPublicUI></HeaderPublicUI>
    <div style="display: flex; justify-content: center;">
        <div class="auth_container">
            <h1 style="text-align: center;">Вход</h1>
            <div class="reg column">
                <InputText :value="email" placeholder="Почта" @input="onEmail" :error="emailError" />
                <InputText :value="password" placeholder="Пароль" @input="onPassword" type="password"
                    :error="passwordError" />
                <ButtonUI style="width: 100%; margin-top: 24px;" label="Войти" color="primary" @onClick="onClick" />
            </div>
        </div>
    </div>
</template>


<style scoped>
.auth_container {
    margin-top: 48px;
    width: 394px;
    min-width: 394px;
}


.reg {
    margin-top: 24px;
    padding: 32px;
    background-color: var(--white);
    border-radius: 6px;
}
</style>
