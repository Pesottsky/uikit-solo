<template>
    <Header :is-company="true">
        <template #title>
            <InputHeadless v-model="nameBase" :isSubTitle="true" :is-max="false" ref="nameBaseRef" @onFocusOut="changeNameBase" />
            <Button :type="BUTTON_TYPE.TINY" size="big" :icon="true" v-if="currentBase" @on-click="setFocusNameBase">
                <EditIcon />
            </Button>
        </template>
        <template #action>
            <Button :type="BUTTON_TYPE.TETRARY" label="Добавить" :icon="true" @on-click="() => openSidebar()">
                <AddUserIcon />
            </Button>
            <Button :type="BUTTON_TYPE.TETRARY" label="Пригласить" :disabled="companyLoading" :loading="companyLoading" :icon="true" @on-click="inviteFreelancer">
                <ImportIcon />
            </Button>
        </template>
    </Header>
</template>

<script setup>
    import { storeToRefs } from 'pinia';
    import { inject, onMounted, ref, watch } from 'vue';
    import Header from '../../Header/Header.vue';
    import { Button, InputHeadless } from '../../UI';
    import { ImportIcon, EditIcon, AddUserIcon } from '../../Icons';
    import BUTTON_TYPE from '@/constants/buttonTypes';
    import { FakeFreelancer } from '../../../constants/FakeFreelancer';
    import { useCompanyStore } from '../../../stores/company.store';

    const storeCompany = useCompanyStore();
    const { currentBase, companyError, companyLoading } = storeToRefs(storeCompany);

    const openRightSidebar = inject('openRightSidebar');
    const openInviteModal = inject('openInviteModal');

    const nameBase = ref(null);
    const nameBaseRef = ref(null);

    function openSidebar(freelancer=null) {
        openRightSidebar();
        storeCompany.setFreelancer(freelancer);
        if (!freelancer) {
            storeCompany.createFakeFreelancer();
        }
    }
    async function inviteFreelancer() {
        const freelancer = new FakeFreelancer({ id: 'fake' });
        const payload = freelancer.profile;
        delete payload.id;

        await storeCompany.createRowInBase({ ...payload }, { setNotification: false });
        if (!companyError.value) {
            const data = await storeCompany.generateInviteLink();
            if (data) {
                openInviteModal(data);
            }
        }
    }
    async function changeNameBase() {
        await storeCompany.renameBase(nameBase.value);
    }

    function setFocusNameBase() {
        nameBaseRef.value?.setFocus();
    }

    watch(currentBase, value => {
        if (value) {
            nameBase.value = value.name;
        }
    })

    onMounted(() => {
        if (currentBase.value) {
            nameBase.value = currentBase.value.name;
        }
    })

</script>

<style lang="scss" scoped>

</style>