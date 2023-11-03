<template>
    <RouterLink :to="{ name: ROUTES_NAMES.FREELANCER_EMPLOYMENT }" class="link_reset sidebar-menu__item" exact-active-class="sidebar-menu__item_active">
        Занятость
    </RouterLink>
    <RouterLink :to="{ name: ROUTES_NAMES.FREELANCER_PROFILE }" class="link_reset sidebar-menu__item" exact-active-class="sidebar-menu__item_active">
        Настройки профиля
    </RouterLink>
</template>

<script setup>
    import ROUTES_NAMES from '@/constants/routesNames';
</script>

<style lang="scss" scoped>
    .sidebar {
        &-menu {

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
                    background: var(--tetrary-hover);
                    cursor: pointer;
                }
                &:active{
                    color: var(--tetrary-color-active)
                } 
                &:hover .icon {
                    opacity: 1;
                }
                &:active .icon {
                    opacity: .5;
                }
                &:focus {
                    outline: none;
                    box-shadow: 0 0 0 4px var(--focus);
                }

                &_active {
                    background: var(--tetrary-hover);
                }
            }
        }

    }
</style>