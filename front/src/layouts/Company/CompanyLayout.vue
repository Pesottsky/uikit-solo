<template>
    <div class="wrapper">
        <Sidebar>
            <AccountMenu />
        </Sidebar>
        <Content>
            <RouterView />
        </Content>
    </div>

    <ImportModal ref="importModalRef" />
</template>

<script setup>
    import Sidebar from '@/components/Sidebar/Sidebar.vue';
    import AccountMenu from '@/components/Company/AccountMenu/AccountMenu.vue';
    import Content from '@/components/Content/Content.vue';
    import ImportModal from '@/components/Company/Modals/ImportModal.vue';
    import { onMounted, provide, ref } from 'vue';
    import { useCompanyStore } from '../../stores/company.store';

    const storeCompany = useCompanyStore();

    const importModalRef = ref(null);

    provide('openImportModal', () => importModalRef.value?.open());
    provide('createBase', () => storeCompany.createBase());

    onMounted(() => {
        const id = sessionStorage.getItem('PROFILE_ID_FOR_PUSH_BASE');
        if (id) {
            storeCompany.pushRowInBase(id);
        }
    })
</script>