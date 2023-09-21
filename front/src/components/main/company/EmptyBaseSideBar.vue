<script>
import ButtonUI from '../../common/ButtonUI.vue';

export default {
    name: "EmptyBaseSideBar",
    components: {
        ButtonUI
    },
    props: {
        value: { name: String, required: true, default: '123' },
    },
}

</script>

<template>
    <p>Компания</p>
    <p>{{ name }}</p>
    <p>База фрилансеров</p>
    <ButtonUI label="Создать новую базу" color="primary" icon="url(../../../src/assets/icons/plus-white.svg) no-repeat"/>
    <ButtonUI label="Secondary" color="secondary" icon="url(../../../src/assets/icons/import-black.svg) no-repeat" />

</template>


<style scoped>
.side_bar {
    width: fit-content;
    height: 100vh;
    padding: 24px;
    background-color: var(--white);
}
</style>
