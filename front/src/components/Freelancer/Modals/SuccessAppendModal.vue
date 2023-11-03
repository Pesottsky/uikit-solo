<template>
    <Backdrop @on-close="closeWindow" v-if="isShowModal">
        <div class="modal-window">
            <div class="modal-window__wrapper">
                <SuccessIcon />
                <div class="modal-window__item">
                    <h3 class="modal-window__title">Успех! Вы добавлены в базу фрилансеров</h3>
                    <p>Теперь вы и компания будете на связи</p>
                </div>
                <Button label="Хорошо" :type="BUTTON_TYPE.SECONDARY" @on-click="closeWindow" />
            </div>
        </div>
    </Backdrop>
</template>

<script setup>
    import { ref } from 'vue';
    import { Backdrop, Button, Input } from '../../UI';
    import SuccessIcon from '../../Icons/Success/SuccessIcon.vue';
    import BUTTON_TYPE from '../../../constants/buttonTypes';

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
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);

        width: 418px;
        padding: 12px;
        gap: 16px;
        border-radius: 6px;
        background: var(--white, #FFF);
        box-shadow: 0px 20px 72px 0px rgba(46, 28, 0, 0.10);

        &__wrapper {
            display: flex;
            flex-direction: column;
            align-items: flex-start;
            gap: 16px;
            padding: 16px 36px 32px;
            background: var(--biege-light);
        }
        &__item {
            display: flex;
            flex-direction: column;
            gap: 12px;
        }

        &__title {
            font-family: Antonym;
            font-weight: 500;
            line-height: 120%;
        }
    }
</style>