<template>
    <Header>
        <template #title>
            <InputHeadless v-model="nameBase" :isSubTitle="true" :is-max="false" />
            <Button :type="BUTTON_TYPE.TINY" :icon="true" v-if="currentBase">
                <EditIcon />
            </Button>
        </template>
        <template #action>
            <Button :type="BUTTON_TYPE.TETRARY" label="Добавить" :icon="true" @on-click="() => openSidebar()">
                <AddUserIcon />
            </Button>
            <Button :type="BUTTON_TYPE.TETRARY" label="Пригласить" :icon="true" @on-click="inviteFreelancer">
                <ImportIcon />
            </Button>
        </template>
    </Header>
</template>

<script setup>
    import { storeToRefs } from 'pinia';
    import { inject, onMounted, ref } from 'vue';
    import Header from '../../Header/Header.vue';
    import { Button, InputHeadless } from '../../UI';
    import { ImportIcon, EditIcon, AddUserIcon } from '../../Icons';
    import BUTTON_TYPE from '@/constants/buttonTypes';
    import { FakeFreelancer } from '../../../constants/hardData';
    import { useCompanyStore } from '../../../stores/company.store';

    const storeCompany = useCompanyStore();
    const { currentBase, companyError } = storeToRefs(storeCompany);

    const openRightSidebar = inject('openRightSidebar');
    const openInviteModal = inject('openInviteModal');

    const nameBase = ref(null);

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

    onMounted(() => {
        if (currentBase.value) {
            nameBase.value = currentBase.value.name;
        }
    })

</script>

<style lang="scss" scoped>

</style>