<template>
    <AuthForm>
        <template #title>
            <h1>Сбросить пароль</h1>
        </template>
        <template #form-data>
            <Input placeholder="Почта" v-model="state.email" :error="validate.email.$errors[0]?.$message" />

            <Input 
                label="Новый пароль"
                placeholder="Пароль"
                type="password"
                v-model="state.password"
                :error="validate.password.$errors[0]?.$message"
                @on-focus="validate.password.$reset"
            />
            <Input
                placeholder="Подтверждение пароля"
                type="password"
                v-model="state.repeatPassword"
                :error="validate.repeatPassword.$errors[0]?.$message"
                @on-focus="validate.repeatPassword.$reset"
            />
        </template>
        <template #form-action>
            <p class="error" v-if="authError">{{ authError }}</p>
            <Button label="Сбросить пароль" class="width_max" :disabled="authLoading" :loading="authLoading" @on-click="onClick" />
        </template>
        <template #links>
            <RouterLink :to="{ name: ROUTES_NAMES.LOGIN }">Есть аккаунт</RouterLink>
        </template>
    </AuthForm>
</template>

<script setup>
    import { reactive, onMounted } from 'vue';
    import { storeToRefs } from 'pinia';
    import AuthForm from '../AuthForm/AuthForm.vue';
    import { Input, Button } from '../../UI';
    import ROUTES_NAMES from '../../../constants/routesNames';
    import ERROR_MESSAGES from '../../../constants/errorMessages';
    import { useRoute, useRouter } from 'vue-router';
    import { useVuelidate } from '@vuelidate/core';
    import { required, minLength, helpers, email } from '@vuelidate/validators';

    import { useAuthStore } from '../../../stores/auth.store';

    const storeAuth = useAuthStore();
    const { authLoading, authError } = storeToRefs(storeAuth);
    const route = useRoute();
    const router = useRouter();

    onMounted(() => {
        if (!route.query?.code) router.replace({ name: ROUTES_NAMES.NOT_FOUND });
    })

    const state = reactive({
        email: '',
        password: '',
        repeatPassword: ''
    })
    const rules = {
        email: {
            required: helpers.withMessage(ERROR_MESSAGES.REQUIRED, required),
            email: helpers.withMessage(ERROR_MESSAGES.EMAIL, email)
        },
        password: { 
            required: helpers.withMessage(ERROR_MESSAGES.REQUIRED, required),
            minLength: helpers.withMessage(
                ERROR_MESSAGES.MIN_LENGTH(import.meta.env.VITE_MIN_LENGTH_PASSWORD), 
                minLength(import.meta.env.VITE_MIN_LENGTH_PASSWORD)
            )
        },
        repeatPassword: {
            required: helpers.withMessage(ERROR_MESSAGES.REQUIRED, required),
            noMatch: helpers.withMessage(ERROR_MESSAGES.NO_MATCH_PASSWORD, noMatch)
        }
    }
    const validate = useVuelidate(rules, state);

    function noMatch(value) {
        return value === state.password;
    }
    async function onClick() {
        const result = await validate.value.$validate();
        if (!result) return;
        
        await storeAuth.updatePassword({login: state.email, new_password: state.password});
    }
</script>

<style lang="scss" scoped>

</style>