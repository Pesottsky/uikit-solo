<template>
    <section class="profile">
        <Input label="Название компании" placeholder="Введите название" />
        <Input label="Ссылка на сайт" placeholder="https://" />
        <Input label="О компании" placeholder="Описание компании" />
    </section>
</template>

<script setup>
    import { Input } from '../../UI';
</script>

<style lang="scss" scoped>
    .profile {
        width: 556px;
        margin-top: 56px;
        display: flex;
        flex-direction: column;
        gap: 32px;
    }
</style>