<script>
import TabsWrapper from '../components/common/TabsWrapper.vue'
import TabItem from '../components/common/tabsitem.vue'
import Company from '../components/registration/Company.vue'
import Freel from '../components/registration/Freel.vue'
import HeaderPublicUI from '../components/common/HeaderPublicUI.vue'

export default {
    name: "Registration",
    components: {
        Company,
        TabItem,
        TabsWrapper,
        HeaderPublicUI,
        Freel
    },
    data() {
        return {

        }
    }
}
</script>

<template>
    <HeaderPublicUI></HeaderPublicUI>
    <div style="display: flex; justify-content: center;">
        <div class="auth_container">
            <h1 style="text-align: center;">Регистрация</h1>
            <TabsWrapper class="wrapper">
                <TabItem title="Как компания">
                    <Company></Company>
                </TabItem>
                <TabItem title="Как фрилансер">
                    <Freel></Freel>
                </TabItem>
            </TabsWrapper>
        </div>
    </div>
</template>


<style scoped>
.auth_container {
    margin-top: 48px;
    width: 394px;
    min-width: 394px;
}

.wrapper {
    margin-top: 24px;
}
</style>
