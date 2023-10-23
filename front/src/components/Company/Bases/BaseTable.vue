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
            <TableRow :count="10" v-for="item in [1, 2, 3]" :is-hover="false" v-if="companyLoading">
                <TableColumn :span="2">
                    <div class="flex flex_row">
                        <Skeleton :size="30" />
                        <Skeleton :size="70" />
                    </div>
                </TableColumn>
                <TableColumn>
                    <Skeleton :size="60" />
                </TableColumn>
                <TableColumn>
                    <Skeleton :size="60" />
                </TableColumn>
                <TableColumn>
                    <Skeleton :size="60" />
                </TableColumn>
                <TableColumn :span="3">
                    <div class="flex flex_column">
                        <Skeleton :size="85" />
                        <Skeleton :size="65" />
                    </div>
                </TableColumn>
                <TableColumn :span="2">
                    <Skeleton :size="90" />
                </TableColumn>
            </TableRow>
            <TableRow 
                v-else
                v-for="item in freelancers"
                :count="10" 
                :key="item.profile.id" 
                :is-fake="item?.fake || isNullFreelancer(item.profile)"
                :selected="item.profile.id === currentFreelancer?.profile?.id"
                @click="() => openSidebar(item)"
            >
                <TableColumn :span="2">{{ item.profile.first_name || 'Имя' }} {{ item.profile.last_name || 'Фамилия' }}</TableColumn>
                <TableColumn>
                    <Chip 
                    :type="CHIP_TYPE_BY_NAME[item.profile.loading?.description] || CHIP_TYPES.UNKNOWN" 
                    :text="item.profile.loading?.description || 'Не ясно'"
                />
                </TableColumn>
                <TableColumn>{{ item.profile.experience || '--' }}</TableColumn>
                <TableColumn>{{ item.profile.price || '--' }}</TableColumn>
                <TableColumn :span="3">{{ item.profile.summary || '--' }}</TableColumn>
                <TableColumn :span="2"><span class="stripe">{{ item.profile.portfolio || '--' }}</span></TableColumn>
            </TableRow>
        </Table>
        <div class="table-wrapper__add" @click="() => openSidebar()">+</div>
    </div>
</template>

<script setup>
    import { storeToRefs } from 'pinia';
    import { computed, inject } from 'vue';
    import { Table, TableRow, TableColumn, Chip, Skeleton } from '../../UI';
    import CHIP_TYPE_BY_NAME from '@/constants/chipTypeByName';
    import CHIP_TYPES from '../../../constants/chipTypes';
    import { useCompanyStore } from '../../../stores/company.store';
    import { isNullFreelancer } from '../../../helpers/profile';

    const storeCompany = useCompanyStore();
    const { currentBase, companyLoading, fakeFreelancers, currentFreelancer } = storeToRefs(storeCompany);


    const freelancers = computed(() => {
        return currentBase.value?.rows?.length 
            ? currentBase.value.rows 
            : fakeFreelancers.value
    })

    const openRightSidebar = inject('openRightSidebar');

    function openSidebar(freelancer=null) {
        openRightSidebar();

        storeCompany.setFreelancer(freelancer);
        
        if (!freelancer) {
            storeCompany.createFakeFreelancer();
        }

    }

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

    .flex {
        width: 100%;
        display: flex;

        &_column {
            flex-direction: column;
            gap: 10px;
        }
        &_row {
            flex-direction: row;
            gap: 5px;
        }
    }
</style>