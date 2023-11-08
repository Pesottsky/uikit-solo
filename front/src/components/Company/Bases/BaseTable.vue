<template>
    <div class="table-wrapper">
        <Table>
            <TableRow :is-header="true" :count="11">
                <TableColumn :span="2">Имя</TableColumn>
                <TableColumn :span="2">Загрузка</TableColumn>
                <TableColumn>Грейд</TableColumn>
                <TableColumn>Ставка ₽/час</TableColumn>
                <TableColumn :span="3">О себе</TableColumn>
                <TableColumn :span="2">Портфолио</TableColumn>
            </TableRow>
            <TableRow :count="11" v-for="item in [1, 2, 3]" :is-hover="false" v-if="companyLoading">
                <TableColumn :span="2">
                    <div class="flex flex_row">
                        <Skeleton :size="30" />
                        <Skeleton :size="70" />
                    </div>
                </TableColumn>
                <TableColumn :span="2">
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
                :count="11" 
                :key="item.profile.id" 
                :is-fake="item?.fake || isNullFreelancer(item.profile)"
                :selected="item.profile.id === currentFreelancer?.profile?.id"
                @click="(e) => openSidebar(e, item)"
            >
                <TableColumn :span="2">{{ getUserName(item.profile) }}</TableColumn>
                <TableColumn :span="2">
                    <Chip 
                    :type="CHIP_TYPE_BY_NAME[item.profile.loading?.description] || CHIP_TYPES.UNKNOWN" 
                    :text="item.profile.loading?.description || 'Не ясно'"
                />
                </TableColumn>
                <TableColumn>{{ item.profile.grade?.description || '--' }}</TableColumn>
                <TableColumn>{{ item.profile.price || '--' }}</TableColumn>
                <TableColumn :span="3"><div class="truncate-text">{{ item.profile.summary || '--' }}</div></TableColumn>
                <TableColumn :span="2">
                    <a v-if="item.profile.portfolio" :href="item.profile.portfolio" class="stripe" target="_blank">{{ item.profile.portfolio }}</a>
                    <span v-else >---</span>
                </TableColumn>
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
    import { isNullFreelancer, getUserName } from '../../../helpers/profile';

    const storeCompany = useCompanyStore();
    const { currentBase, companyLoading, fakeFreelancers, currentFreelancer } = storeToRefs(storeCompany);


    const freelancers = computed(() => {
        return currentBase.value?.rows?.length 
            ? currentBase.value.rows 
            : fakeFreelancers.value
    })

    const openRightSidebar = inject('openRightSidebar');

    function openSidebar(e, freelancer=null) {
        if (e && e.target.tagName === 'A') return;
        
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
        min-width: 975px;
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