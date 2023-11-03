<template>
    <Backdrop v-if="isShow" @on-close="onClose">
        <div class="sidebar" :class="{ 'sidebar_opened': isShowSidebar }">
            <div class="sidebar-header">
                <div class="sidebar-header__item cursor_pointer">
                    <DoubleArrowRightIcon @click="onClose" />
                </div>
                <div class="sidebar-header__item sidebar-header__item_right">
                    <Button 
                        v-if="!!currentFreelancer && !currentFreelancer?.fake"
                        :type="BUTTON_TYPE.TETRARY" 
                        label="Поделиться" 
                        @on-click="shareFreelancer" 
                    />
                    <Button v-if="isChangeData" label="Сохранить" :disabled="companyLoading" :loading="companyLoading" @on-click="onSave" />
                </div>
            </div>
            <div class="sidebar-content">
                <div class="sidebar-content__name">
                    <template v-if="isChangeData">
                        <InputHeadless placeholder="Имя" v-model="state.first_name" :is-title="true" :is-max="false" />
                        <InputHeadless placeholder="Фамилия" v-model="state.last_name" :is-title="true" :is-max="false" />
                    </template>
                    <template v-else>
                        <h1>{{ state.first_name }} {{ state.last_name }}</h1>
                    </template>
                </div>
                <Quote v-if="currentFreelancer?.link && isChangeData">
                    <template v-if="currentFreelancer.link?.email && currentFreelancer.link.is_email_sending">
                        <span class="text_gray">Отправили ссылку на приглашение</span> {{ currentFreelancer.link?.email }}
                    </template>
                    <template v-else>
                        <span class="text_gray">Создали ссылку на приглашение</span> {{ currentFreelancer.link.link }}
                    </template>
                </Quote>
                <div class="sidebar-content__invite" v-if="isChangeData">
                    <span class="text_gray">Отправим фрилансеру ссылку на почту, и он сам заполнит профиль</span>
                    <Button :type="BUTTON_TYPE.SECONDARY" label="Пригласить" :icon="true" @on-click="sendInvite">
                        <ImportIcon />
                    </Button>
                    
                </div>
                <div class="sidebar-content__anketa">
                    <div class="grid-column grid-column__name text_gray">Загрузка</div>
                    <div class="grid-column grid-column_margin_left" title="Загрузку будет обновлять фрилансер">
                        <Chip 
                            :type="CHIP_TYPE_BY_NAME[state.loading] || CHIP_TYPE.UNKNOWN" 
                            :text="state.loading || 'Не ясно'" 
                            class="cursor_not-allowed" 
                        />
                    </div>
                    <div class="grid-column grid-column__name text_gray">Грейд</div>
                    <div class="grid-column" :class="{ 'grid-column_margin_left': !isChangeData }">
                        <Dropdown v-if="isChangeData" v-model="state.grade" :list="gradeList" placeholder="Выбрать" />
                        <p v-else>{{ state.grade || 'Выбрать' }}</p>
                    </div>
                    <div class="grid-column grid-column__name text_gray">Стоимость 1 часа работы (₽)</div>
                    <div class="grid-column">
                        <InputHeadless placeholder="1000" type="number" :readonly="!isChangeData" v-model="state.price" />
                    </div>
                    <div class="grid-column grid-column__name text_gray">Портфолио</div>
                    <div class="grid-column">
                        <InputHeadless v-if="isChangeData || !state.portfolio" :readonly="!isChangeData" placeholder="https://" v-model="state.portfolio" />
                        <a v-else :href="state.portfolio" target="_blank">{{ state.portfolio }}</a>
                    </div>
                    <div class="grid-column grid-column__name text_gray">Сколько лет опыта</div>
                    <div class="grid-column">
                        <InputHeadless placeholder="Пусто" :readonly="!isChangeData" v-model="state.experience" />
                    </div>
                    <div class="grid-column grid-column__name text_gray">Скилы</div>
                    <div class="grid-column">
                        <InputHeadless placeholder="Пусто" :readonly="!isChangeData" v-model="state.skills" />
                    </div>
                    <div class="grid-column grid-column__name text_gray">Резюме</div>
                    <div class="grid-column">
                        <Textarea placeholder="Пусто" :readonly="!isChangeData" :is-headless="true" v-model="state.summary" />
                    </div>
                    <div class="grid-column grid-column__name text_gray">Email</div>
                    <div class="grid-column">
                        <InputHeadless placeholder="name@email.com" :readonly="!isChangeData" v-model="state.email" />
                    </div>
                    <div class="grid-column grid-column__name text_gray">Телеграм</div>
                    <div class="grid-column">
                        <InputHeadless placeholder="@telegram" :readonly="!isChangeData" v-model="state.telegram" />
                    </div>
                </div>
                <div class="sidebar-content__comment">
                    <div class="sidebar-content__row">
                        <p class="text_gray">
                            Ваши комментарии
                            <span class="font-caption text_gray">(видны только вам)</span>
                        </p>
                        <Button 
                            label="Сохранить" 
                            :type="BUTTON_TYPE.TETRARY" 
                            :loading="commentLoading" 
                            :disabled="commentLoading || currentFreelancer?.fake" 
                            @on-click="onSaveComment"
                        />
                    </div>
                    <div class="sidebar-content__row">
                        <Textarea 
                            placeholder="Оставить комментарий" 
                            :is-headless="false" 
                            v-model="commentFreelancer"
                            :readonly="commentLoading || currentFreelancer?.fake"
                        />
                    </div>
                </div>
                <div class="sidebar-content__actions">
                    <Button :type="BUTTON_TYPE.TETRARY" label="Удалить" :loading="companyLoading" :disabled="companyLoading" @on-click="removeFreelancer" />
                </div>
            </div>
        </div>
    </Backdrop>
</template>

<script setup>
    import { Button, Backdrop, Textarea, Chip, InputHeadless, Quote, Dropdown } from '../../UI';
    import { DoubleArrowRightIcon, ImportIcon, ArrowTopWhiteIcon } from '../../Icons';
    import BUTTON_TYPE from '../../../constants/buttonTypes';
    import CHIP_TYPE from '../../../constants/chipTypes';
    import NOTIFICATION_MESSAGES from '../../../constants/notificationMessages';
    import { computed, inject, reactive, ref, watch } from 'vue';
    import { useCompanyStore } from '../../../stores/company.store';
    import { useNoticeStore } from '../../../stores/notice.store';
    import { useFreelancerStore } from '../../../stores/freelancer.store';
    import { copyToClipboard } from '../../../helpers/clipboard';
    import { generateLink } from '../../../helpers/profile';
    import { storeToRefs } from 'pinia';
import CHIP_TYPE_BY_NAME from '../../../constants/chipTypeByName';

    const openInviteModal = inject('openInviteModal')

    const storeCompany = useCompanyStore();
    const { currentFreelancer, commentFreelancer, commentLoading, companyLoading } = storeToRefs(storeCompany);

    const storeNotice = useNoticeStore();
    const storeFreelancer = useFreelancerStore();
    const { directory } = storeToRefs(storeFreelancer);

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
        grade: '',
        loading: null
    })
    const isShow = ref(false);
    const isShowSidebar = ref(false);
    const isChangeData = ref(false);

    const gradeList = computed(() => directory.value.grade.map(item => ({ value: item.description, active: false })));

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
            await storeCompany.createRowInBase({ ...state }, { setNotification: false });
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
        storeCompany.setComment(null)
        storeCompany.deleteFakeFreelancer();
        isShowSidebar.value = false;
        setTimeout(() => {
            isShow.value = false;
        }, 500)
    }

    watch(currentFreelancer, (value) => {

        if (value) {
            Object.keys(state).map(key => {
                if (key === 'grade' || key === 'loading') {
                    state[key] = currentFreelancer.value?.profile?.[key]?.description || '';
                } else {
                    state[key] = String(currentFreelancer.value?.profile?.[key] || '');
                }
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
                padding: 8px 0px;

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
            }

            &__comment {
                display: flex;
                flex-direction: column;
                gap: 8px;
                border-top: 1px solid var(--black-opacity-10);
                padding-top: 8px;
            }
            &__row {
                display: flex;
                align-items: center;
                justify-content: space-between;
            }

            &__actions {
                display: flex;
                justify-content: flex-start;
                margin-top: auto;
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

        &__name {
            padding: 8px 0px;
            align-items: flex-start;
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