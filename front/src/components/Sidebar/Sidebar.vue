<template>
    <aside class="sidebar">
        <div class="sidebar__icon">
            <LogoIcon />
        </div>
        <div class="sidebar-menu">
            <slot></slot>
        </div>
        <div class="sidebar__action">
            <a class="sidebar__action__link" href="https://t.me/vviiktoor">Задать вопрос</a>
            <Button label="Выйти" class="sidebar__action__btn" :type="BUTTON_TYPE.TETRARY" @on-click="onLogout" />
        </div>
    </aside>
</template>

<script setup>
    import { LogoIcon } from '../Icons';
    import { Button } from '../UI';
    import BUTTON_TYPE from '../../constants/buttonTypes';

    import { useAuthStore } from '../../stores/auth.store';

    const storeAuth = useAuthStore();

    async function onLogout() {
        await storeAuth.logout();
    }
</script>

<style lang="scss" scoped>
    .sidebar {
        width: 233px;
        min-width: 233px;
        padding: 24px 16px 24px 14px;
        background-color: var(--white);
        display: flex;
        flex-direction: column;
        gap: 40px;
        justify-content: space-between;
        height: 100vh;

        &__icon {
            margin-left: 10px;
        }
        &-menu {
            flex: 1 1 auto;
            display: flex;
            flex-direction: column;
            gap: 8px;
        }
        &__action {
            width: fit-content;
            text-align: start;
            display: flex;
            flex-direction: column;
            gap: 8px;

            &__link {
                margin-left: 12px;
            }
            &__btn {
                width: fit-content;
            }
        }

    }
</style>