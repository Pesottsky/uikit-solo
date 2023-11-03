<template>
    <div class="start-window">
        <div class="start-window__title">
            <h1>Привет👋 — это Soloteam, платформа для сотрудничества компаний и фрилансеров</h1>
            <h2 class="color_black-opacity-50">Делитесь вашим профилем с заказчиками, чтобы оставаться на связи.</h2>
        </div>
        <div class="start-window__description">
            <div class="start-window__column">
                <span>🌏 🏁<br />Регулярно отмечайте занятость, чтобы компании смогли планировать проекты с вами</span>
                <span class="start-window__url" @click="openEmployment">Отметить занятость</span>
            </div>
            <div class="start-window__column">
                <span>📝<br />Заполняйте и обновляйте профиль, так вам будут приходить более подходящие заказы</span>
                <span class="start-window__url" @click="openProfile">Заполнить профиль</span>
            </div>
        </div>
    </div>

    <SuccessAppendModal ref="successAppendModalRef" />

</template>

<script setup>
    import { onMounted, ref } from 'vue';
    import SuccessAppendModal from '../Modals/SuccessAppendModal.vue';

    import { useRoute, useRouter } from 'vue-router';

    import ROUTES_NAMES from '../../../constants/routesNames';

    const route = useRoute();
    const router = useRouter();

    const successAppendModalRef = ref(null);

    function openEmployment() {
        router.replace({name: ROUTES_NAMES.FREELANCER_EMPLOYMENT, query: {} })

    }

    function openProfile() {
        router.replace({name: ROUTES_NAMES.FREELANCER_PROFILE, query: {} })
    }

    onMounted(() => {
        if (route.query?.link) {
            successAppendModalRef.value?.open();
            router.replace({ name: ROUTES_NAMES.FREELANCER_START_SCREEN, query: {} });
        }
    })
</script>

<style lang="scss" scoped>

    .start-window {
        width: 584px;
        margin-top: 56px;
        display: flex;
        flex-direction: column;
        gap: 80px;

        &__title {
            display: flex;
            flex-direction: column;
            gap: 16px;
        }

        &__description {
            display: flex;
            gap: 40px;
            font-size: 17px;
        }

        &__column {
            display: flex;
            flex-direction: column;
            gap: 20px;
        }

        &__url {
            font-weight: bold;
            text-decoration: underline;
            cursor: pointer;
            text-underline-offset: 3px;

            &:hover {
                text-decoration: none;
            }
        }
    }

</style>