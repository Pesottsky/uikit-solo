<template>
    <Backdrop @on-close="closeWindow" v-if="isShowModal">
        <div class="modal-window">
            <h1>Если у вас уже есть база фрилансеров —<br />ее можно импортировать в Soloteam</h1>
            <div class="modal-window__list">
                <div class="modal-window__item">
                    <div class="modal-window__step">1</div>
                    <span class="modal-window__line">
                        <CrookedLine />
                    </span>
                    <span>Скачайте, существующую таблицу в формате CSV, вот инстуркци для <a href="#">Exel</a> и <a href="#">Notion</a></span>
                </div>
                <div class="modal-window__item">
                    <div class="modal-window__step">2</div>
                    <span class="modal-window__line">
                        <CrookedLine />
                    </span>
                    <span>Напишите нам в телеграм <a href="#">@soloteamcont</a>, скажите email и скиньте файл — адрес аккаунта ваш</span>
                </div>
                <div class="modal-window__item">
                    <div class="modal-window__step">
                        <CheckMarkIcon />
                    </div>
                    В течении нескольких дней мы перенесем в таблицу в ваш аккаунт
                </div>
            </div>
            <p>По всем вопросам импортаи переноса пишите нам <a href="#">@soloteamcont</a></p>
        </div>
    </Backdrop>
</template>

<script setup>
    import { ref } from 'vue';
    import Backdrop from '../../UI/Backdrop/Backdrop.vue';
    import { CheckMarkIcon } from '../../Icons';
    import CrookedLine from '../../Icons/CrookedLine.vue';

    const isShowModal = ref(false);

    function closeWindow() {
        isShowModal.value = false;
    }
    function openWindow() {
        isShowModal.value = true;
    }

    defineExpose({
        open: openWindow,
        close: closeWindow
    })
</script>

<style lang="scss" scoped>
    .modal-window {
        position: absolute;
        top: 227px;
        left: 233px;
        width: 814px;
        height: 378px;
        padding: 32px 48px;
        border-radius: 6px;
        background-color: var(--white);
        box-shadow: 0px 4px 40px 0px rgba(0, 0, 0, 0.20);
        display: flex;
        flex-direction: column;
        justify-content: space-between;

        &__list {
            display: flex;
            gap: 24px;
        }

        &__item {
            min-width: 230px;
            position: relative;
            display: flex;
            flex-direction: column;
            gap: 12px;
        }

        &__step {
            display: flex;
            width: 32px;
            height: 32px;
            padding: 5px;
            justify-content: center;
            align-items: center;
            border-radius: 8px;
            background: #F5EFE9;

            color: var(--black-opacity-50);
            font-family: Antonym;
            font-size: 18px;
            font-weight: 500;
            line-height: 120%;
        }

        &__line {
            position: absolute;
            left: 42px;
        }
    }
</style>