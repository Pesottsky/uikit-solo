<template>
    <div class="sidebar-menu__title sidebar-menu__title_first">
        <span class="menu-title__name">Компания</span>
    </div>
    <template v-if="companyLoading">
        <Skeleton />
        <Skeleton />
    </template>
    <template v-else>
        <RouterLink :to="{ name: ROUTES_NAMES.COMPANY_PROFILE }" class="link_reset sidebar-menu__item" exact-active-class="sidebar-menu__item_active">
            {{ companyInfo.name }}
        </RouterLink>
    </template>
    <div class="sidebar-menu__title">
        <span class="menu-title__name">База фрилансеров</span>
        <template v-if="bases.length">
            <!-- <Button :type="BUTTON_TYPE.TINY" :icon="true" @on-click="createBase">
                <PlusBlackIcon />
            </Button> -->
            <Button :type="BUTTON_TYPE.TINY" :icon="true" @on-click="onOpenContextMenu">
                <MoreIcon />
            </Button>
        </template>
    </div>
    <template v-if="companyLoading">
        <Skeleton />
        <Skeleton />
    </template>
    <template v-else>
        <RouterLink
            v-for="item in bases"
            :key="item.id"
            :to="{ name: ROUTES_NAMES.COMPANY_BASE, params: { id: item.id } }"
            class="link_reset sidebar-menu__item"
            exact-active-class="sidebar-menu__item_active"
        >
            {{ item.name }}
        </RouterLink>
    </template>
    <template v-if="!bases?.length && !companyLoading">
        <div class="sidebar__start">
            <Button label="Создать новую базу" :icon="true" @on-click="createBase">
                <PlusWhiteIcon />
            </Button>
            <Button label="Импортировать" :type="BUTTON_TYPE.SECONDARY" :icon="true" @on-click="openImportModal">
                <ImportIcon />
            </Button>
        </div>
    </template>

    <ContextMenu ref="contextMenuRef" />
</template>

<script setup>
    import ContextMenu from '../ContextMenu/ContextMenu.vue'; 
    import { Button, Skeleton } from '@/components/UI';
    import { PlusWhiteIcon, PlusBlackIcon, ImportIcon, MoreIcon } from '@/components/Icons'
    import BUTTON_TYPE from '@/constants/buttonTypes';
    import ROUTES_NAMES from '@/constants/routesNames';

    import { inject, onMounted, ref } from 'vue';
    import { storeToRefs } from 'pinia';
    import { useCompanyStore } from '@/stores/company.store';

    const openImportModal = inject('openImportModal');
    const createBase = inject('createBase');

    const storeCompany = useCompanyStore();
    const { companyInfo, bases, companyLoading } = storeToRefs(storeCompany);

    const contextMenuRef = ref(null);

    function onOpenContextMenu(e) {

        const coords = e.target.getBoundingClientRect();

        const obj = {
            x: coords.x + 5,
            y: coords.y + 10
        }

        contextMenuRef.value?.open(obj);
    }

    onMounted(() => {
        storeCompany.getCompanyInfo();
        storeCompany.getBases();
    })

</script>

<style lang="scss" scoped>
    .sidebar {
        &-menu {

            &__title {
                margin-top: 24px;
                display: flex;
                align-items: center;
                justify-content: flex-start;
                gap: 4px;
                
                &_first {
                    margin-top: 0px;
                }
            }

            &__item {
                padding: 8px 12px;
                gap: 4px;
                display: inline-flex;
                justify-content: left;
                align-items: center;

                background: #ffffff00;
                color: var(--black);
                border: 0px;
                border-radius: 8px;

                transition: background, 0.2s ease-in-out;

                &:hover{
                    background: var(--gray-light-hover);
                    cursor: pointer;
                }
                &:active{
                    color: var(--gray-light-hover)
                } 
                &:hover .icon {
                    opacity: 1;
                }
                &:active .icon {
                    opacity: .5;
                }
                &:focus {
                    outline: none;
                    box-shadow: 0 0 0 4px var(--gray-light-hover);
                }

                &_active {
                    background: var(--gray-light-hover);
                }
            }
        }
        &__start {
            margin-top: 8px;
            margin-left: 10px;
            display: flex;
            flex-direction: column;
            gap: 8px;
        }
    }
    .menu {
        &-title {
            &__name {
                flex: 1 1 auto;
                color: var(--black-opacity-50);
                font-size: 13px;
                margin-left: 10px;
            }
        }
    }
</style>