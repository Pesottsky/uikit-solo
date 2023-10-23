<template>
    <Backdrop v-if="isShow" @on-close="onClose">
        <div class="sidebar" :class="{ 'sidebar_opened': isShowSidebar }">
            <div class="sidebar-header">
                <div class="sidebar-header__item">
                    <DoubleArrowRightIcon @click="onClose" />
                </div>
                <div class="sidebar-header__item sidebar-header__item_right">
                    <Button 
                        v-if="!!currentFreelancer && !currentFreelancer?.fake"
                        :type="BUTTON_TYPE.TETRARY" 
                        label="Поделиться" 
                        @on-click="shareFreelancer" 
                    />
                    <Button v-if="isChangeData" label="Сохранить" @on-click="onSave" />
                </div>
            </div>
            <div class="sidebar-content">
                <div class="sidebar-content__name">
                    <InputHeadless placeholder="Имя" :readonly="!isChangeData" v-model="state.first_name" :is-title="true" :is-max="false" />
                    <InputHeadless placeholder="Фамилия" :readonly="!isChangeData" v-model="state.last_name" :is-title="true" :is-max="false" />
                </div>
                <Quote v-if="currentFreelancer?.link && isChangeData">
                    <template v-if="currentFreelancer.link.is_email_sending">
                        <span class="text_gray">Отправили ссылку на приглашение</span> [ email ]
                    </template>
                    <template v-else>
                        <span class="text_gray">Создали ссылку на приглашение</span> {{ currentFreelancer.link.link }}
                    </template>
                </Quote>
                <div class="sidebar-content__invite" v-if="isChangeData">
                    <Button :type="BUTTON_TYPE.SECONDARY" label="Пригласить" :icon="true" @on-click="sendInvite">
                        <ImportIcon />
                    </Button>
                    <span class="text_gray">Отправим фрилансеру ссылку на почту, и он сам заполнит профиль</span>
                </div>
                <div class="sidebar-content__anketa">
                    <div class="grid-column text_gray">Загрузка</div>
                    <div class="grid-column grid-column_margin_left">
                        <Chip :type="CHIP_TYPE.UNKNOWN" text="Не ясно" />
                    </div>
                    <div class="grid-column text_gray">Грейд</div>
                    <div class="grid-column grid-column_margin_left">Пусто</div>
                    <div class="grid-column text_gray">Ставка</div>
                    <div class="grid-column">
                        <InputHeadless placeholder="Пусто" :readonly="!isChangeData" v-model="state.price" />
                    </div>
                    <div class="grid-column text_gray">Портфолио</div>
                    <div class="grid-column">
                        <InputHeadless v-if="isChangeData" placeholder="https://" v-model="state.portfolio" />
                        <a v-else :href="state.portfolio" target="_blank">{{ state.portfolio }}</a>
                    </div>
                    <div class="grid-column text_gray">Опыт</div>
                    <div class="grid-column">
                        <InputHeadless placeholder="Пусто" :readonly="!isChangeData" v-model="state.experience" />
                    </div>
                    <div class="grid-column text_gray">Скилы</div>
                    <div class="grid-column">
                        <InputHeadless placeholder="Пусто" :readonly="!isChangeData" v-model="state.skills" />
                    </div>
                    <div class="grid-column text_gray">Резюме</div>
                    <div class="grid-column">
                        <Textarea placeholder="Пусто" :readonly="!isChangeData" :is-headless="true" v-model="state.summary" />
                    </div>
                    <div class="grid-column text_gray">Email</div>
                    <div class="grid-column">
                        <InputHeadless placeholder="email@mail.ru" :readonly="!isChangeData" v-model="state.email" />
                    </div>
                    <div class="grid-column text_gray">Телеграм</div>
                    <div class="grid-column">
                        <InputHeadless placeholder="@telegram" :readonly="!isChangeData" v-model="state.telegram" />
                    </div>
                </div>
                <div class="sidebar-content__anketa sidebar-content__anketa_border-top" v-if="!!currentFreelancer && !currentFreelancer?.fake">
                    <div class="grid-column grid-column_column">
                        <p class="text_gray">Ваши комментарии</p>
                        <span class="font-caption text_gray">(видны только вам)</span>
                    </div>
                    <div class="grid-column grid-column_row">
                        <Textarea placeholder="Оставить комментарий" :is-headless="true" v-model="commentFreelancer" />
                        <Button :icon="true" :disabled="commentLoading" @on-click="onSaveComment">
                            <ArrowTopWhiteIcon />
                        </Button>
                    </div>
                </div>
            </div>
            <div class="sidebar-footer">
                <Button :type="BUTTON_TYPE.TETRARY" label="Удалить" @on-click="removeFreelancer" />
            </div>
        </div>
    </Backdrop>
</template>

<script setup>
    import { Button, Backdrop, Textarea, Chip, InputHeadless, Quote } from '../../UI';
    import { DoubleArrowRightIcon, ImportIcon, ArrowTopWhiteIcon } from '../../Icons';
    import BUTTON_TYPE from '../../../constants/buttonTypes';
    import CHIP_TYPE from '../../../constants/chipTypes';
    import NOTIFICATION_MESSAGES from '../../../constants/notificationMessages';
    import { inject, reactive, ref, watch } from 'vue';
    import { useCompanyStore } from '../../../stores/company.store';
    import { useNoticeStore } from '../../../stores/notice.store';
    import { copyToClipboard } from '../../../helpers/clipboard';
    import { generateLink } from '../../../helpers/profile';
    import { storeToRefs } from 'pinia';

    const openInviteModal = inject('openInviteModal')

    const storeCompany = useCompanyStore();
    const { currentFreelancer, commentFreelancer, commentLoading } = storeToRefs(storeCompany);

    const storeNotice = useNoticeStore();

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
    const isChangeData = ref(false);

    function shareFreelancer() {
        const cb = () => {
            storeNotice.setMessage(NOTIFICATION_MESSAGES.COPY_URL_FREELANCER);
        }
        const url = generateLink(currentFreelancer.value.profile);
        copyToClipboard(url, cb);
        
    }
    async function onSave() {
        if (currentFreelancer.value && !currentFreelancer.value?.fake) {
            await storeCompany.updateRowInBase({ ...state })
        } else {
            await storeCompany.createRowInBase({ ...state });
        }
    }
    async function onSaveComment() {
        await storeCompany.createComment({ comment: commentFreelancer.value });
    }
    async function removeFreelancer() {
        await storeCompany.removeRowInBase();
        onClose();
    }
    async function sendInvite() {
        if (currentFreelancer.value?.link) {
            openInviteModal(currentFreelancer.value.link, { isClearFreelancer: false });
        } else {
            const data = await storeCompany.generateInviteLink();
            openInviteModal(data, { isClearFreelancer: false });
        }
    }

    function clearState() {
        Object.keys(state).map(key => state[key] = '');
    }

    function onOpen() {

        clearState();

        isShow.value = true;
        setTimeout(() => {
            isShowSidebar.value = true;
        }, 5)
    }
    function onClose() {
        storeCompany.setFreelancer(null);
        storeCompany.deleteFakeFreelancer();
        isShowSidebar.value = false;
        setTimeout(() => {
            isShow.value = false;
        }, 500)
    }

    watch(currentFreelancer, (value) => {

        if (value) {
            Object.keys(state).map(key => {
                state[key] = String(currentFreelancer.value?.profile?.[key] || '');
            })

            if (!value?.fake) storeCompany.getComment();

            isChangeData.value = value.can_change;
        } else {
            clearState();
            isChangeData.value = true;
        }
    })

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
            height: calc(100vh - 78px);
            display: flex;
            flex-direction: column;
            gap: 24px;
            overflow-x: hidden;
            overflow-y: auto;

            &__name {
                width: 100%;
                height: 48px;
                display: flex;
                align-items: center;
                padding: 8px 6px;
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
                padding-top: 4px;
                
                &_border {
                    &-top {
                        border-top: 1px solid var(--black-opacity-10);
                    }
                }
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
        min-height: 36px;
        height: auto;
        display: flex;
        justify-content: flex-start;
        align-items: center;
        gap: 4px;

        &_margin_left {
            margin-left: 7px;
        }

        &_column {
            padding-top: 4px;
            flex-direction: column;
            align-items: flex-start;
        }
        &_row {
            align-items: flex-start;
        }
    }
</style>