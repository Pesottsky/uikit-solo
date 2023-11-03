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
                <template v-if="!isCompany">
                    <Input 
                        placeholder="Имя" 
                        v-model="state.name" 
                        label="Контактные данные" 
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
                    :label="isCompany ? 'Ваша почта' : undefined"
                    :placeholder="isCompany ? 'name@email.ru' : 'Почта'"
                    v-model="state.email"
                    type="email"
                    :error="validate.email.$errors[0]?.$message"
                    @on-focus="validate.email.$reset"
                />
            </div>
            <div class="auth-form__item">
                <Input 
                    label="Пароль"
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
            <Button label="Зарегистрироваться" class="width_max" :disabled="authLoading" :loading="authLoading" @on-click="onRegistration" />
            <p class="font-caption-small color_black-opacity-50">Регистрируюясь, вы соглашаетесь с <a class="cursor_pointer" href="https://docs.google.com/document/d/1Lu1lPIUjNh_Sqdgnmy_TicP7LeQ6A5mRNLYY98uh2ko/edit#heading=h.rvcxzbnm66cc">условиями</a></p>
        </template>
        <template #links>
            <RouterLink :to="{ name: ROUTES_NAMES.LOGIN }">Есть аккаунт</RouterLink>
            <span class="v-splitter"></span>
            <RouterLink :to="{ name: ROUTES_NAMES.FORGOT_PASSWORD }">Забыли пароль?</RouterLink>
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
                minLength: helpers.withMessage(
                    ERROR_MESSAGES.MIN_LENGTH(import.meta.env.VITE_MIN_LENGTH_PASSWORD), 
                    minLength(import.meta.env.VITE_MIN_LENGTH_PASSWORD)
                )
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