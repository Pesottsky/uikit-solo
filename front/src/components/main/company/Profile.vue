<script>
import EmptyBaseSideBar from "./EmptyBaseSideBar.vue"
export default {
    name: "CompanyProfile",
    components: {
        EmptyBaseSideBar
    },
    data() {
        return {
            loading: false
        }
    }
}

</script>

<template>
    <div v-if="!loading" class="row">
        <div class="side_bar column">
            <img class="logo" src="../../../assets/icons/Soloteamlogo.svg" alt="Soloteam logo">
            <EmptyBaseSideBar></EmptyBaseSideBar>
        </div>
        Ты на цглавной
    </div>
    <div v-else>
        Загрузка
    </div>
</template>


<style scoped>
.side_bar {
    width: fit-content;
    height: 100vh;
    padding: 24px;
    background-color: var(--white);
}
</style>
