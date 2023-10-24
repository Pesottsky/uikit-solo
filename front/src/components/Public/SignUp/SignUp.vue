<template>
    <AuthForm>
        <template #title>
            <h1>Регистрация</h1>
        </template>
        <template #switcher>
            <TabsMenu :list="menu" />
        </template>
        <template #form-data>
            <div class="auth-form__item">
                <Input
                    v-if="isCompany"
                    placeholder="Название"
                    v-model="state.name"
                    label="Компания" 
                    :error="validate.name.$errors[0]?.$message"
                    @on-focus="validate.name.$reset" 
                />
                <template v-else>
                    <Input 
                        placeholder="Имя" 
                        v-model="state.name" 
                        label="Как вас зовут" 
                        :error="validate.name.$errors[0]?.$message" 
                        @on-focus="validate.name.$reset"
                    />
                    <Input 
                        placeholder="Фамилия" 
                        v-model="state.surName" 
                        :error="validate.surName.$errors[0]?.$message"
                        @on-focus="validate.surName.$reset"
                    />
                </template>
                <Input 
                    placeholder="Почта"
                    v-model="state.email"
                    type="email"
                    :error="validate.email.$errors[0]?.$message"
                    @on-focus="validate.email.$reset"
                />
            </div>
            <div class="auth-form__item">
                <Input 
                    placeholder="Пароль"
                    v-model="state.password"
                    type="password"
                    :error="validate.password.$errors[0]?.$message"
                    @on-focus="validate.password.$reset"
                />
                <Input 
                    placeholder="Подтверждение пароля"
                    v-model="state.repeatPassword"
                    type="password"
                    :error="validate.repeatPassword.$errors[0]?.$message"
                    @on-focus="validate.repeatPassword.$reset"
                />
            </div>
        </template>
        <template #form-action>
            <p class="error" v-if="authError">{{ authError }}</p>
            <Button label="Зарегистрироваться" class="width_max" :disabled="authLoading" @on-click="onRegistration" />
        </template>
        <template #links>
            <RouterLink :to="{ name: ROUTES_NAMES.LOGIN }">Есть аккаунт</RouterLink>
            <span class="v-splitter"></span>
            <RouterLink :to="{ path: '/forgot-password' }">Забыли пароль?</RouterLink>
        </template>
    </AuthForm>
</template>

<script setup>
    import { reactive, computed, watch } from 'vue';
    import ROUTES_NAMES from '../../../constants/routesNames';
    import SIGN_UP_TYPE from '../../../constants/signUpType';
    import ERROR_MESSAGES from '../../../constants/errorMessages';

    import { useRoute, useRouter } from 'vue-router';
    import { useAuthStore } from '../../../stores/auth.store';
    import { storeToRefs } from 'pinia';

    import { Button, Input, TabsMenu } from '../../UI';
    import AuthForm from '../AuthForm/AuthForm.vue';

    import { useVuelidate } from '@vuelidate/core';
    import { required, email, minLength, helpers } from '@vuelidate/validators';

    const route = useRoute();
    const router = useRouter();

    const storeAuth = useAuthStore();
    const { authLoading, authError } = storeToRefs(storeAuth);

    const menuSource = [
        { id: SIGN_UP_TYPE.COMPANY, text: 'Как компания', 
            action: () => router.push({ to: route.name, query: { ...route.query, type: SIGN_UP_TYPE.COMPANY } }) },
        { id: SIGN_UP_TYPE.FREELANCER, text: 'Как фрилансер', 
            action: () => router.push({ to: route.name, query: { ...route.query, type: SIGN_UP_TYPE.FREELANCER } }) }
    ];

    const menu = computed(() => {
        return menuSource.map(item => {
            item.selected = item.id === route.query.type;
            return item;
        })
    })

    const formData = {
        name: '',
        email: '',
        password: '',
        repeatPassword: '',
    }

    const isCompany = computed(() => route.query.type === SIGN_UP_TYPE.COMPANY);

    const state = computed(() => {
        if (isCompany.value) return reactive({ ...formData });
        return reactive({ ...formData, surName: '' })
    });
    const rules = computed(() => {
        if (isCompany.value) {
            return {
                name: { required: helpers.withMessage(ERROR_MESSAGES.REQUIRED, required) },
                email: { 
                    required: helpers.withMessage(ERROR_MESSAGES.REQUIRED, required), 
                    email: helpers.withMessage(ERROR_MESSAGES.EMAIL, email)
                },
                password: { 
                    required: helpers.withMessage(ERROR_MESSAGES.REQUIRED, required), 
                    minLength: helpers.withMessage(ERROR_MESSAGES.MIN_LENGTH(5), minLength(5))
                },
                repeatPassword: { 
                    required: helpers.withMessage(ERROR_MESSAGES.REQUIRED, required), 
                    matchPassword: helpers.withMessage(ERROR_MESSAGES.NO_MATCH_PASSWORD, (value) => value === state.value.password)
                }
            }
        }
        return {
            name: { required: helpers.withMessage(ERROR_MESSAGES.REQUIRED, required) },
            surName: {},
            email: { 
                required: helpers.withMessage(ERROR_MESSAGES.REQUIRED, required), 
                email: helpers.withMessage(ERROR_MESSAGES.EMAIL, email)
            },
            password: { 
                required: helpers.withMessage(ERROR_MESSAGES.REQUIRED, required), 
                minLength: helpers.withMessage(ERROR_MESSAGES.MIN_LENGTH(5), minLength(5))
            },
            repeatPassword: { 
                required: helpers.withMessage(ERROR_MESSAGES.REQUIRED, required), 
                matchPassword: helpers.withMessage(ERROR_MESSAGES.NO_MATCH_PASSWORD, (value) => value === state.value.password) 
            }
        }
    })
    const validate = useVuelidate(rules, state);

    async function onRegistration() {
        const result = await validate.value.$validate();
        if (!result) return;

        if (isCompany.value) {
            await storeAuth.registrationCompany({
                login: state.value.email,
                name: state.value.name,
                password: state.value.password
            });
        } else {
            await storeAuth.registrationFreelancer({
                login: state.value.email,
                first_name: state.value.name,
                last_name: state.value.surName,
                password: state.value.password
            })
        }
    }

    function onFocus() {
        console.log('FOCUS');
    }

    watch(route, () => {
        validate.value.$reset();
    })

</script>

<style lang="scss" scoped>
    .auth-form__item {
        display: flex;
        flex-direction: column;
        gap: 12px;
    }
</style>