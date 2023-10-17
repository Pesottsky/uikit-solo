<template>
    <div class="table-wrapper">
        <Table>
            <TableRow :is-header="true" :count="10">
                <TableColumn :span="2">Имя</TableColumn>
                <TableColumn>Загрузка</TableColumn>
                <TableColumn>Грейд</TableColumn>
                <TableColumn>Ставка</TableColumn>
                <TableColumn :span="3">Описание</TableColumn>
                <TableColumn :span="2">Портфолио</TableColumn>
            </TableRow>
            <TableRow :count="10" v-for="item in freelancers" :key="item.id" @click="openRightSidebar">
                <TableColumn :span="2">{{ item.first_name }} {{ item.last_name }}</TableColumn>
                <TableColumn>
                    <Chip :type="CHIP_TYPE_BY_NAME['Неизвестно']" text="Не ясно" />
                </TableColumn>
                <TableColumn>{{ item.experience }}</TableColumn>
                <TableColumn>{{ item.price }}</TableColumn>
                <TableColumn :span="3">{{ item.summary }}</TableColumn>
                <TableColumn :span="2">{{ item.portfolio }}</TableColumn>
            </TableRow>
        </Table>
        <div class="table-wrapper__add" @click="openRightSidebar">+</div>
    </div>
</template>

<script setup>
    import { Table, TableRow, TableColumn, Chip } from '../../UI';
    import CHIP_TYPE_BY_NAME from '@/constants/chipTypeByName';
    import { FREELANCER_TEMPLATE } from '@/constants/hardData';
    import { useCompanyStore } from '../../../stores/company.store';
    import { storeToRefs } from 'pinia';
    import { computed, inject } from 'vue';

    const storeCompany = useCompanyStore();
    const { currentBase } = storeToRefs(storeCompany);

    const freelancers = computed(() => {
        console.log(currentBase.value);
        return currentBase.value?.rows?.length 
            ? currentBase.value.rows 
            : [{ ...FREELANCER_TEMPLATE }, { ...FREELANCER_TEMPLATE }, { ...FREELANCER_TEMPLATE }]
    })

    const openRightSidebar = inject('openRightSidebar');

</script>

<style lang="scss" scoped>
    .table-wrapper {
        width: 100%;
        margin-top: 48px;
        display: flex;
        flex-direction: column;
        gap: 10px;

        &__add {
            width: 100%;
            border-radius: 8px;
            padding: 8px 12px;
            text-align: center;
            border: 1px dashed rgba(0, 0, 0, 0.10);
            cursor: pointer;

            &:hover {
                border: none;
                background-color: var(--gray-light-hover);
            }
        }
    }
</style>