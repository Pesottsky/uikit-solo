<template>
    <section class="profile" v-if="!companyLoading">
        <Input label="Название компании" placeholder="Введите название" v-model="state.name" />
        <Input label="Ссылка на сайт" placeholder="https://" v-model="state.link" />
        <Textarea label="О компании" placeholder="Описание компании" v-model="state.about" />
        <Button label="Сохранить изменения" :type="BUTTON_TYPE.SECONDARY" :disabled="companyLoading" :loading="companyLoading" @on-click="onSave" />
    </section>
</template>

<script setup>
    import { storeToRefs } from 'pinia';
    import { Input, Textarea, Button } from '../../UI';
    import BUTTON_TYPE from '../../../constants/buttonTypes';
    import { useCompanyStore } from '../../../stores/company.store';
    import { onMounted, reactive, watch } from 'vue';

    const storeCompany = useCompanyStore();
    const { companyInfo, companyLoading } = storeToRefs(storeCompany);

    const state = reactive({
        name: '',
        link: '',
        about: ''
    })

    function onSave() {
        storeCompany.updateCompanyInfo({ ...state });
    }
    function setState() {
        state.name = companyInfo.value.name;
        state.link = companyInfo.value.link;
        state.about = companyInfo.value.about;
    }

    watch(companyInfo, value => {
        setState();
    }, { deep: true })

    onMounted(() => {
        setState();
    })

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