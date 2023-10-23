<template>
    <div class="account-card">
        <div class="account-card__title">
            <template v-if="!freelancerProfile">
                <Skeleton :size="40" />
                <Skeleton :size="15" />
            </template>
            <template v-else>
                <h1>{{ nameUser }}</h1>
                <Chip :text="loadingText" :type="CHIP_TYPE_BY_NAME[loadingText]" />
            </template>
        </div>
        <div class="account-card__action">
            <template v-if="!freelancerProfile">
                <Skeleton :size="75" />
                <Skeleton :size="15" />
            </template>
            <template v-else>
                <p class="account-card__description">
                    Ссылка на профиль: 
                    <RouterLink v-if="freelancerProfile" :to="{ name: ROUTES_NAMES.FREELANCER_PROFILE_PUBLIC, params: { id: freelancerProfile?.link } }">
                        {{ linkUser }}
                    </RouterLink>
                </p>
                <Button label="Скопировать" :type="BUTTON_TYPE.TETRARY" @on-click="onCopy" />
            </template>
        </div>
    </div>
</template>

<script setup>
    import { storeToRefs } from 'pinia';
    import { computed } from 'vue';
    import { useFreelancerStore } from '../../../stores/freelancer.store';
    import { useNoticeStore } from '../../../stores/notice.store';

    import ROUTES_NAMES from '../../../constants/routesNames';
    import NOTIFICATION_MESSAGE from '../../../constants/notificationMessages';
    import CHIP_TYPE_BY_NAME from '../../../constants/chipTypeByName';
    import { EMPLOYMENT_NAME } from '../../../constants/employment'
    import BUTTON_TYPE from '../../../constants/buttonTypes';
    import { generateLink } from '../../../helpers/profile';
    import { copyToClipboard } from '../../../helpers/clipboard';

    import { Chip, Button, Skeleton } from '../../UI'

    const storeFreelancer = useFreelancerStore();
    const storeNotice = useNoticeStore();
    const { freelancerProfile, freelancerLoading } = storeToRefs(storeFreelancer);

    const nameUser = computed(() => {
        return `${freelancerProfile.value?.first_name} ${freelancerProfile.value?.last_name}`
    })
    const linkUser = computed(() => generateLink(freelancerProfile.value));
    const loadingText = computed(() => {
        if (freelancerProfile.value?.loading) {
            return freelancerProfile.value.loading.description;
        }
        return EMPLOYMENT_NAME.UNKNOWN;
    })

    function onCopy() {
        copyToClipboard(linkUser.value, () => storeNotice.setMessage(NOTIFICATION_MESSAGE.COPY_URL_FREELANCER))
    }
</script>

<style lang="scss" scoped>
    .account-card {
        display: flex;
        flex-direction: column;
        gap: 8px;
        min-width: 565px;

        &__title {
            display: flex;
            align-items: flex-end;
            gap: 8px;
        }
        &__action {
            display: flex;
            align-items: center;
            gap: 8px;
        }
        &__description {
            color: var(--black-opacity-50);
        }
    }
</style>