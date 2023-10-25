<template>
    <AuthForm>
        <template #title>
            <h1>Восстановить пароль</h1>
        </template>
        <template #form-data>
            <Input label="Ваша почта" placeholder="Почта" v-model="state.email" :error="validate.email.$errors[0]?.$message" @on-focus="validate.email.$reset" />
        </template>
        <template #form-action>
            <Button label="Восстановить пароль" class="width_max" :disabled="authLoading" :loading="authLoading" @on-click="onClick" />
        </template>
        <template #links>
            <RouterLink :to="{ name: ROUTES_NAMES.LOGIN }">Есть аккаунт</RouterLink>
        </template>
    </AuthForm>
</template>

<script setup>
    import { reactive } from 'vue';
    import AuthForm from '../AuthForm/AuthForm.vue';
    import { Input, Button } from '../../UI';
    import ROUTES_NAMES from '../../../constants/routesNames';
    import ERROR_MESSAGES from '../../../constants/errorMessages';

    import { useVuelidate } from '@vuelidate/core';
    import { required, email, helpers } from '@vuelidate/validators';
    
    import { useAuthStore } from '../../../stores/auth.store';

    const storeAuth = useAuthStore();
    const { authLoading } = storeToRefs(storeAuth);

    const state = reactive({
        email: ''
    })
    const rules = {
        email: { 
            required: helpers.withMessage(ERROR_MESSAGES.REQUIRED, required),
            email: helpers.withMessage(ERROR_MESSAGES.EMAIL, email) 
        }
    }
    const validate = useVuelidate(rules, state);

    async function onClick() {
        const result = await validate.value.$validate();
        if (!result) return;
        
        alert('Какой то запрос на сервер');
    }
</script>

<style lang="scss" scoped>

</style>