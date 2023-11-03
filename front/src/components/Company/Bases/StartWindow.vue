<template>
    <div class="start-window">
        <div class="start-window__title">
            <h1>Привет👋 — это Soloteam, платформа для ведения вашей базы фрилансеров </h1>
            <h2 class="color_black-opacity-50">У вас бесплатный период на 1 месяц. Потом попросим с вас 690₽/мес.</h2>
        </div>
        <div class="start-window__description">
            <div class="start-window__column">
                <span>🌎👽<br />Можете с нуля создать свой список фрилансеров и разослать приглашения</span>
                <span class="start-window__url" @click="createBase">Создать новую базу</span>
            </div>
            <div class="start-window__column">
                <span>📥<br />Если у вас уже есть база, мы импортируем ее в&nbsp;Soloteam</span>
                <span class="start-window__url" @click="openImportModal">Импортировать</span>
            </div>
        </div>
    </div>
</template>

<script setup>
    import { inject } from 'vue';

    const openImportModal = inject('openImportModal');
    const createBase = inject('createBase');
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