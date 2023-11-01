<template>
    <h1 class="start__title">Привет! Это сервис для ведения<br /> базы аутсорса Soloteam</h1>
    <div class="start__description">
        <p class="start__text">Здесь ты можешь отслеживать занятость чтобы заказчики понимали когда можно обращаться и быть с нмим на связи</p>
        <p class="start__text">А еще, чтобы не теряться, делиться профилем и портфолио</p>
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

    onMounted(() => {
        if (route.query?.link) {
            successAppendModalRef.value?.open();
            router.replace({ name: ROUTES_NAMES.FREELANCER_START_SCREEN, query: {} });
        }
    })
</script>

<style lang="scss" scoped>
    .start {
        &__title {
            margin-top: 40px;
        }
        &__text {
            color: var(--black);
            font-family: Inter;
            font-size: 17px;
            font-style: normal;
            font-weight: 400;
            line-height: normal;
        }
        &__description {
            margin-top: 48px;
            width: 661px;
            display: flex;
            flex-direction: column;
            gap: 17px;
        }
    }
</style>