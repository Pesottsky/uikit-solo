<script>
import TabsWrapper from '../components/common/TabsWrapper.vue'
import TabItem from '../components/common/tabsitem.vue'
import Company from '../components/registration/Company.vue'

export default {
    name: "Authorization",
    components: {
        Company,
        TabItem,
        TabsWrapper
    },
    data() {
        return {

        }
    }
}
</script>

<template>
    <div style="display: flex; justify-content: center;">
        <div class="auth_container">
            <h1 style="text-align: center;">Регистрация</h1>
            <TabsWrapper class="wrapper">
                <TabItem title="Как компания">
                        <Company></Company>
                </TabItem>
                <TabItem title="Как фрилансер">Content from Tab 2 Lorem ipsum dolor sit amet.</TabItem>
            </TabsWrapper>
        </div>
    </div>
</template>


<style scoped>
.auth_container {
    min-width: 394px;
    min-height: 506px;
}

.wrapper {
    margin-top: 24px;
}
</style>
