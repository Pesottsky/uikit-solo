<template>
    <section class="profile">
        <Input label="Название компании" placeholder="Введите название" v-model="state.name" />
        <Input label="Ссылка на сайт" placeholder="https://" v-model="state.link" />
        <Textarea label="О комании" placeholder="Описание компании" v-model="state.about" />
        <Button label="Сохранить изменения" :type="BUTTON_TYPE.SECONDARY" @on-click="onSave" />
    </section>
</template>

<script setup>
    import { storeToRefs } from 'pinia';
    import { Input, Textarea, Button } from '../../UI';
    import BUTTON_TYPE from '../../../constants/buttonTypes';
    import { useCompanyStore } from '../../../stores/company.store';
    import { reactive } from 'vue';

    const storeCompany = useCompanyStore();
    const { companyInfo } = storeToRefs(storeCompany);

    const state = reactive({
        name: companyInfo.value.name,
        link: companyInfo.value.link,
        about: companyInfo.value.about
    })

    function onSave() {
        storeCompany.updateCompanyInfo({ ...state });
    }
</script>

<style lang="scss" scoped>
    .profile {
        width: 556px;
        margin-top: 56px;
        display: flex;
        flex-direction: column;
        align-items: flex-start;
        gap: 32px;
    }
</style>