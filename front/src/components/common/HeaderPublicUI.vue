<script setup>
import ButtonUI from './ButtonUI.vue'
import { useRouter } from 'vue-router';
const router = useRouter()

function toLogin() {
    router.push({ name: "Login" })
}

function toRegistration() {
    router.push({ name: "Registration" })
}
</script>

<template>
    <header>
        <div class="header-Public-UI">
            <img class="logo" src="../../assets/icons/Soloteamlogo.svg" alt="Soloteam logo">

            <div class="header-buttons">
                <ButtonUI label="Вход" color="secondary" @click="toLogin" />
                <ButtonUI label="Регистрация" color="primary" @click="toRegistration" />
                <ButtonUI label="" color="tetrary" icon="url(../src/assets/icons/big-three-dots-black.svg) no-repeat" />
            </div>
        </div>
    </header>
</template>

<style scoped>
.header-Public-UI {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
    width: 1280px;
    padding: 16px 0px 0px 24px;
    gap: 8px;
}

.header-buttons {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    gap: 8px;
}

.logo {
    padding-top: 8px;
}
</style>