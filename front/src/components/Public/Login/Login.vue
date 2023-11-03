<template>
    <AuthForm>
        <template #title>
            <h1>Вход</h1>
        </template>
        <template #form-data>
            <Input placeholder="Почта" v-model="state.email" :error="validate.email.$errors[0]?.$message" />
            <Input placeholder="Пароль" v-model="state.password" type="password" :error="validate.password.$errors[0]?.$message" />
        </template>
        <template #form-action>
            <p class="error" v-if="authError">{{ authError }}</p>
            <Button label="Войти" class="width_max" :disabled="authLoading" :loading="authLoading" @on-click="onLogin" />
        </template>
        <template #links>
            <RouterLink :to="{ name: ROUTES_NAMES.SIGN_UP }">Регистрация</RouterLink>
            <span class="v-splitter"></span>
            <RouterLink :to="{ name: ROUTES_NAMES.FORGOT_PASSWORD }">Забыли пароль?</RouterLink>
        </template>
    </AuthForm>
</template>

<script setup>
    import { reactive } from 'vue';
    import { Button, Input } from '../../UI';
    import AuthForm from '../AuthForm/AuthForm.vue';
    import ROUTES_NAMES from '../../../constants/routesNames';
    import ERROR_MESSAGES from '../../../constants/errorMessages';

    import { useAuthStore } from '../../../stores/auth.store';
    import { storeToRefs } from 'pinia';

    import { useVuelidate } from '@vuelidate/core';
    import { required, email, helpers } from '@vuelidate/validators';

    const storeAuth = useAuthStore();
    const { authLoading, authError } = storeToRefs(storeAuth);

    const state = reactive({
        email: '',
        password: ''
    })
    const rules = {
        email: {
            required: helpers.withMessage(ERROR_MESSAGES.REQUIRED, required),
            email: helpers.withMessage(ERROR_MESSAGES.EMAIL, email)
        },
        password: {
            required: helpers.withMessage(ERROR_MESSAGES.REQUIRED, required)
        }
    }
    const validate = useVuelidate(rules, state);

    async function onLogin() {
        const result = await validate.value.$validate();
        if (!result) return;

        await storeAuth.login({
            login: state.email,
            password: state.password
        })
    }

</script>

<style lang="scss" scoped>

</style>