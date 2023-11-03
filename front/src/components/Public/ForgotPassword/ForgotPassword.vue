<template>
    <AuthForm>
        <template #title>
            <h1>Восстановить пароль</h1>
        </template>
        <template #form-data>
            <Input label="Ваша почта" placeholder="Почта" v-model="state.email" :error="validate.email.$errors[0]?.$message" @on-focus="onFocus" />
        </template>
        <template #form-action>
            <p class="error" v-if="authError">{{ authError }}</p>
            <Button label="Восстановить пароль" class="width_max" :disabled="authLoading" :loading="authLoading" @on-click="onClick" />
        </template>
        <template #links>
            <RouterLink :to="{ name: ROUTES_NAMES.LOGIN }">Есть аккаунт</RouterLink>
        </template>
    </AuthForm>

    <ForgotPasswordModal ref="forgotPasswordModalRef" />
</template>

<script setup>
    import { reactive, ref } from 'vue';
    import { storeToRefs } from 'pinia';
    import AuthForm from '../AuthForm/AuthForm.vue';
    import { Input, Button } from '../../UI';
    import ROUTES_NAMES from '../../../constants/routesNames';
    import ERROR_MESSAGES from '../../../constants/errorMessages';

    import { useVuelidate } from '@vuelidate/core';
    import { required, email, helpers } from '@vuelidate/validators';
    
    import { useAuthStore } from '../../../stores/auth.store';

    import ForgotPasswordModal from "../Modals/ForgotPasswordModal.vue"

    const storeAuth = useAuthStore();
    const { authLoading, authError } = storeToRefs(storeAuth);

    const forgotPasswordModalRef = ref(null);

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

    async function onFocus() {
        validate.value.email.$reset()
        storeAuth.clearError()
    } 

    async function onClick() {
        const result = await validate.value.$validate();
        if (!result) return;
        
        await storeAuth.forgetPassword({login: state.email});
    
        if (!authError.value) {
            forgotPasswordModalRef.value?.open();
        }
    }
</script>

<style lang="scss" scoped>

</style>