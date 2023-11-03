<template>
    <div class="not-found">
        <NotFoundIcon />
        <div class="not-found__title">
            <h1>404 Error</h1>
            <h2 class="color_black-opacity-50">не получилось найти страницу(</h2>
        </div>
        <RouterLink :to="{ name: mainPageName }">
            <Button label="Вернутся на главную" />
        </RouterLink>
    </div>
</template>

<script setup>
    import { Button } from '../../UI';
    import { NotFoundIcon } from '../../Icons';

    defineProps({
        mainPageName: { type: String, required: true }
    })
</script>

<style lang="scss" scoped>
    .not-found {
        display: flex;
        flex-direction: column;
        gap: 32px;
        align-items: center;
        width: 560px;
        margin: 72px auto;

        &__title {
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 8px;
        }
    }
</style>