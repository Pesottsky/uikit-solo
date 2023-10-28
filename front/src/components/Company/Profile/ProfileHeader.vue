<template>
    <Header :is-company="true">
        <template #title>
            <h2>Название компании</h2>
        </template>
        <template #action>
            <Button :type="BUTTON_TYPE.TETRARY" label="Пригласить" :icon="true">
                <ImportIcon />
            </Button>
        </template>
    </Header>
</template>

<script setup>
    import Header from '../../Header/Header.vue';
    import { Button } from '../../UI';
    import { ImportIcon, EditIcon } from '../../Icons';
    import BUTTON_TYPE from '@/constants/buttonTypes';
</script>