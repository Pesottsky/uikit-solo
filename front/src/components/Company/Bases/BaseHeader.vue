<template>
    <Header>
        <template #title>
            <h2>{{ currentBase?.name || 'Загрузка ...' }}</h2>
            <Button :type="BUTTON_TYPE.TINY" :icon="true" v-if="currentBase">
                <EditIcon />
            </Button>
        </template>
        <template #action>
            <Button :type="BUTTON_TYPE.TETRARY" label="Добавить" :icon="true" @on-click="openRightSidebar">
                <AddUserIcon />
            </Button>
            <Button :type="BUTTON_TYPE.TETRARY" label="Пригласить" :icon="true">
                <ImportIcon />
            </Button>
        </template>
    </Header>
</template>

<script setup>
    import Header from '../../Header/Header.vue';
    import { Button } from '../../UI';
    import { ImportIcon, EditIcon, AddUserIcon } from '../../Icons';
    import BUTTON_TYPE from '@/constants/buttonTypes';
    import { useCompanyStore } from '@/stores/company.store';
    import { storeToRefs } from 'pinia';
    import { inject } from 'vue';

    const storeCompany = useCompanyStore();
    const { currentBase, base } = storeToRefs(storeCompany);

    const openRightSidebar = inject('openRightSidebar');

</script>

<style lang="scss" scoped>

</style>