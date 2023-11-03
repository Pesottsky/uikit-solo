<template>
    <Backdrop @on-close="closeWindow" v-if="isShowModal">
        <div class="modal-window">
            <div class="invite__email">
                <template v-if="!successEmail">
                    <div class="invite__item">
                        <h2 class="modal-window__title">Пригласить по ссылке</h2>
                        <p>Отправим фрилансеру на почту пригласительную ссылку, он зарегистрируется и заполнит профиль</p>
                    </div>
                    <div class="invite__item">
                        <Input 
                            label="Почта фрилансера"
                            placeholder="example@gmail.com" 
                            v-model="state.email" 
                            :error="validate.email.$errors[0]?.$message"
                            @on-focus="validate.email.$reset"
                        />
                        <Button label="Отправить приглашение" :disabled="companyLoading" :loading="companyLoading" @on-click="sendInvite" />
                    </div>
                </template>
                <template v-else>
                    <div class="invite__item invite__item_small">
                        <SuccessIcon />
                        <h3 class="modal-window__title">Готово! Отправили приглашение</h3>
                        <p>Расскажем фрилансеру, что нужно сделать. Его профиль скоро появиться в вашей таблице.</p>
                        
                    </div>
                </template>
            </div>
            <div class="invite__link">
                <div class="invite__item">
                    <h3 class="modal-window__title">Или отправьте ссылку сами</h3>
                </div>
                <div class="invite__item">
                    <Input placeholder="Ссылка" v-model="linkInvite" />
                    <Button :type="BUTTON_TYPE.SECONDARY" label="Скопировать" :icon="true" @on-click="copyLink">
                        <ImportIcon />
                    </Button>
                </div>
            </div>
        </div>
    </Backdrop>
</template>

<script setup>
    import { reactive, ref } from 'vue';
    import { storeToRefs } from 'pinia';
    import { Backdrop, Button, Input } from '../../UI';
    import { ImportIcon } from '../../Icons';
    import SuccessIcon from '../../Icons/Success/SuccessIcon.vue';
    import { copyToClipboard } from '../../../helpers/clipboard';
    import BUTTON_TYPE from '../../../constants/buttonTypes';
    import NOTIFICATION_MESSAGES from '../../../constants/notificationMessages';
    import ERROR_MESSAGES from '../../../constants/errorMessages';
    import { useNoticeStore } from '../../../stores/notice.store';
    import { useCompanyStore } from '../../../stores/company.store';
    import { useVuelidate } from '@vuelidate/core';
    import { required, email, helpers } from '@vuelidate/validators';


    const storeNotice = useNoticeStore();
    const storeCompany = useCompanyStore();
    const { companyError, companyLoading } = storeToRefs(storeCompany)

    const isShowModal = ref(false);
    const successEmail = ref(false);
    
    const linkInvite = ref(null);
    const linkInfo = ref(null);

    const clearFrrelancer = ref(true);

    const state = reactive({
        email: ''
    })
    const rules = {
        email: {
            required: helpers.withMessage(ERROR_MESSAGES.REQUIRED, required),
            email: helpers.withMessage(ERROR_MESSAGES.EMAIL, email)
        }
    }
    const validate = useVuelidate(rules, state);

    function copyLink() {
        const cb = () => storeNotice.setMessage(NOTIFICATION_MESSAGES.COPY_LINK_INVITE_FREELANCER);
        copyToClipboard(linkInvite.value, cb);
    }
    async function sendInvite() {
        const result = await validate.value.$validate();
        if (!result) return;

        await storeCompany.sendInviteByEmail({
            link_id: linkInfo.value.id,
            email: state.email
        })
        if (!companyError.value) {
            successEmail.value = true;
        }
    }

    function closeWindow() {
        isShowModal.value = false;
        if (clearFrrelancer.value) {
            storeCompany.setFreelancer(null);
        }
    }
    function openWindow(data, { isClearFreelancer=true }={}) {
        linkInvite.value = data?.link;
        linkInfo.value = data;
        isShowModal.value = true;
        successEmail.value = data?.is_email_sending || false;

        state.email = '';
        validate.value.$reset();

        clearFrrelancer.value = isClearFreelancer;
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

        display: flex;
        width: 442px;
        padding: 12px 12px 52px 12px;
        flex-direction: column;
        align-items: center;
        gap: 24px;
        border-radius: 6px;
        background: var(--white, #FFF);
        box-shadow: 0px 4px 40px 0px rgba(0, 0, 0, 0.20);

        &__title {
            font-family: Antonym;
            font-weight: 500;
            line-height: 120%;
        }
    }

    .invite {
        &__email {
            display: flex;
            flex-direction: column;
            gap: 24px;
            padding: 16px 36px 36px 36px;
            background: var(--biege-light);
        }
        &__link {
            width: 100%;
            display: flex;
            flex-direction: column;
            gap: 16px;
            padding: 0px 36px;
        }
        &__item {
            display: flex;
            flex-direction: column;
            gap: 16px;

            &_small {
                gap: 12px;
            }
        }
    }
</style>