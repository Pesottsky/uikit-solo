<template>
    <Backdrop v-if="isShow" @on-close="onClose">
        <div class="sidebar" :class="{ 'sidebar_opened': isShowSidebar }">
            <div class="sidebar-header">
                <div class="sidebar-header__item">
                    <DoubleArrowRightIcon @click="onClose" />
                </div>
                <div class="sidebar-header__item sidebar-header__item_right">
                    <Button :type="BUTTON_TYPE.TETRARY" label="Поделиться" />
                    <Button label="Сохранить" @on-click="onSave" />
                </div>
            </div>
            <div class="sidebar-content">
                <div class="sidebar-content__name">
                    <Conteneditable tag="h1" v-model="state.first_name" placeholder="Имя" :class="{ 'text_disabled': !state.first_name }" />
                    <Conteneditable tag="h1" v-model="state.last_name" placeholder="Фамилия" :class="{ 'text_disabled': !state.last_name }" />
                </div>
                <div class="sidebar-content__link-access">
                    <span class="text_gray">Отправили ссылку на приглашение</span> ivanov@gmail.com
                </div>
                <div class="sidebar-content__invite">
                    <Button :type="BUTTON_TYPE.SECONDARY" label="Пригласить" :icon="true">
                        <ImportIcon />
                    </Button>
                    <span class="text_gray">Отправим фрилансеру ссылку на почту, и он сам заполнит профиль</span>
                </div>
                <div class="sidebar-content__anketa">
                    <div class="grid-column">Загрузка</div>
                    <div class="grid-column">
                        <Chip :type="CHIP_TYPE.UNKNOWN" text="Не ясно" />
                    </div>
                    <div class="grid-column">Грейд</div>
                    <div class="grid-column">Пусто</div>
                    <div class="grid-column">Ставка</div>
                    <div class="grid-column">
                        <Conteneditable v-model="state.price" :class="{ 'text_disabled': !state.price }" />
                    </div>
                    <div class="grid-column">Портфолио</div>
                    <div class="grid-column">
                        <Conteneditable v-model="state.portfolio" placeholder="https://" :class="{ 'text_disabled': !state.portfolio }" />
                    </div>
                    <div class="grid-column">Опыт</div>
                    <div class="grid-column">
                        <Conteneditable v-model="state.experience" :class="{ 'text_disabled': !state.experience }" />
                    </div>
                    <div class="grid-column">Скилы</div>
                    <div class="grid-column">
                        <Conteneditable v-model="state.skills" :class="{ 'text_disabled': !state.skills }" />
                    </div>
                    <div class="grid-column">Резюме</div>
                    <div class="grid-column">
                        <Conteneditable v-model="state.summary" :class="{ 'text_disabled': !state.summary }" />
                    </div>
                    <div class="grid-column">Email</div>
                    <div class="grid-column">
                        <Conteneditable v-model="state.email" placeholder="email@mail.ru" :class="{ 'text_disabled': !state.email }" />
                    </div>
                    <div class="grid-column">Телеграм</div>
                    <div class="grid-column">
                        <Conteneditable v-model="state.telegram" placeholder="@telegram" :class="{ 'text_disabled': !state.telegram }" />
                    </div>
                </div>
            </div>
            <div class="sidebar-footer">
                <Button :type="BUTTON_TYPE.TETRARY" label="Удалить" @on-click="onClose" />
            </div>
        </div>
    </Backdrop>
</template>

<script setup>
    import { Button, Backdrop, Conteneditable, Chip } from '../../UI';
    import { DoubleArrowRightIcon, ImportIcon } from '../../Icons';
    import BUTTON_TYPE from '../../../constants/buttonTypes';
    import CHIP_TYPE from '../../../constants/chipTypes';
    import { reactive, ref } from 'vue';
    import { useCompanyStore } from '../../../stores/company.store';
    import { storeToRefs } from 'pinia';

    const storeCompany = useCompanyStore();
    const { companyError } = storeToRefs(storeCompany);

    const state = reactive({
        first_name: '',
        last_name: '',
        telegram: '',
        skills: '',
        summary: '',
        email: '',
        experience: '',
        portfolio: '',
        price: '',
    })
    const isShow = ref(false);
    const isShowSidebar = ref(false);

    async function onSave() {
        await storeCompany.createRowInBase(state)
        if (!companyError.value) onClose();
    }

    function onOpen() {
        isShow.value = true;
        setTimeout(() => {
            isShowSidebar.value = true;
        }, 5)
    }
    function onClose() {
        isShowSidebar.value = false;
        setTimeout(() => {
            isShow.value = false;
        }, 500)
    }

    defineExpose({
        open: onOpen,
        close: onClose
    })

</script>

<style lang="scss" scoped>
    .sidebar {
        position: fixed;
        right: 0px;
        top: 0px;
        bottom: 0px;
        width: 640px;
        border: 1px solid var(--gray-light-hover);
        background: var(--white);
        box-shadow: -8px 4px 40px 0px rgba(0, 0, 0, 0.05);
        padding: 16px 24px 16px 48px;
        display: flex;
        flex-direction: column;
        gap: 12px;
        justify-content: space-between;
        
        transform: translateX(100%);
        transition: transform 500ms;

        &_opened {
            transform: translateX(0%);
        }

        &-header {
            height: 36px;
            display: flex;
            align-items: center;
            justify-content: space-between;

            &__item {
                display: flex;
                align-items: center;
                gap: 12px;

                &_right {
                    justify-content: flex-end;
                }
            }

        }

        &-content {
            flex: 1 1 auto;
            display: flex;
            flex-direction: column;
            gap: 24px;

            &__name {
                width: 100%;
                height: 48px;
                display: flex;
                align-items: center;
                gap: 10px;
                padding: 8px 6px;
            }

            &__link-access {
                padding: 4px 12px;
                border-left: 1px solid var(--black);
            }

            &__invite {
                display: flex;
                align-items: center;
                gap: 30px;
                padding: 12px;
                background: var(--biege-light);
                border-radius: 8px;
            }

            &__anketa {
                display: grid;
                grid-template-columns: 2fr 5fr;
                column-gap: 12px;
                row-gap: 4px;
            }
        }

        &-footer {
            height: 36px;
            display: flex;
            justify-content: flex-start;
        }
    }

    .text {
        &_disabled {
            color: var(--black-opacity-30);
        }
        &_gray {
            color: var(--black-opacity-50);
        }
    }

    .grid-column {
        width: 100%;
        height: auto;
        padding: 8px 0px;
        display: flex;
        justify-content: flex-start;
    }
</style>