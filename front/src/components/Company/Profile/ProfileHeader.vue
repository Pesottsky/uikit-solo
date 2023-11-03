<template>
    <Header :is-company="true">
        <template #title>
            <h2>{{ companyInfo?.name || 'Загрузка ...' }}</h2>
        </template>
       <!--  <template #action>
            <Button :type="BUTTON_TYPE.TETRARY" label="Пригласить" :icon="true">
                <ImportIcon />
            </Button>
        </template> -->
    </Header>
</template>

<script setup>
    import { storeToRefs } from 'pinia';
    import Header from '../../Header/Header.vue';
    import { Button } from '../../UI';
    import { ImportIcon } from '../../Icons';
    import BUTTON_TYPE from '@/constants/buttonTypes';

    import { useCompanyStore } from '../../../stores/company.store';
    
    const storeCompany = useCompanyStore();
    const { companyInfo } = storeToRefs(storeCompany);
    
</script>