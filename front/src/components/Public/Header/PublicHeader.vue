<template>
    <header class="header">
        <LogoIcon />
        <div class="header__actions">
            <RouterLink :to="{ name: ROUTES_NAMES.LOGIN }">
                <Button :type="BUTTON_TYPE.TETRARY" label="Войти" />
            </RouterLink>
            <RouterLink :to="{ name: ROUTES_NAMES.SIGN_UP }">
                <Button :type="BUTTON_TYPE.SECONDARY" label="Регистрация" />
            </RouterLink>
        </div>
    </header>
</template>

<script setup>
    import { Button } from '@/components/UI';
    import { LogoIcon } from '@/components/Icons';
    import BUTTON_TYPE from '@/constants/buttonTypes';
    import ROUTES_NAMES from "@/constants/routesNames";
</script>

<style lang="scss" scoped>
    .header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        width: 100%;
        padding: 16px 24px 0px;
        min-height: 36px;

        &__actions {
            display: flex;
            gap: 12px;
        }
    }
</style>