<script>
import InputText from '../common/InputText.vue'
import ButtonUI from '../common/ButtonUI.vue'
import { registration } from "../../services/AuthService"
import { validateEmail } from '../../utils/validation'
import { set, accessKey, refreshKey, userType } from '../../localstorage'

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
                registration(this.name, this.email, this.password).then(response => {
                    set({
                        accessKey: response.data.access,
                        refreshKey: response.data.refresh,
                        userType: response.data.userType
                    })
                    this.$router.push(this.$route.query.redirect ? this.$route.query.redirect : "/")
                }
                ).catch(error => {
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
        <InputText :value="name" label="Компания" placeholder="Название" @input="onCompany" :error="nameError" />
        <InputText :value="email" placeholder="Почта" @input="onEmail" :error="emailError" />
        <InputText :value="password" style="margin-top: 16px;" label="Пароль" placeholder="Пароль" type="password"
            @input="onPassword" :error="passwordError ? '' : undefined" />
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

@dim : 60px;

.spinner {
   height:@dim;
   width:@dim;
//   margin:0 auto;
//   position:relative;
  position: absolute;
  top:50%;
  left:50%;
  margin: -(@dim/2) 0 0 -(@dim/2);
   -webkit-animation: rotation 1s infinite linear;
   -moz-animation: rotation 1s infinite linear;
   -o-animation: rotation 1s infinite linear;
   animation: rotation 1s infinite linear;
   border:6px solid rgba(0,0,0,.2);
   border-radius:100%;
}

.spinner:before {
   content:"";
   display:block;
   position:absolute;
   left:-6px;
   top:-6px;
   height:100%;
   width:100%;
   border-top:6px solid rgba(0,0,0,.8);
   border-left:6px solid transparent;
   border-bottom:6px solid transparent;
   border-right:6px solid transparent;
   border-radius:100%;
}

@-webkit-keyframes rotation {
   from {-webkit-transform: rotate(0deg);}
   to {-webkit-transform: rotate(359deg);}
}
@-moz-keyframes rotation {
   from {-moz-transform: rotate(0deg);}
   to {-moz-transform: rotate(359deg);}
}
@-o-keyframes rotation {
   from {-o-transform: rotate(0deg);}
   to {-o-transform: rotate(359deg);}
}
@keyframes rotation {
   from {transform: rotate(0deg);}
   to {transform: rotate(359deg);}
  }
