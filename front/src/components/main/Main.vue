<script>
import FreelProfile  from './freel/Profile.vue';
import CompanyProfile   from './company/Profile.vue';
import { get, userType, user } from '../../localstorage';

export default {
    name: "Main",
    components: {
        FreelProfile,
        CompanyProfile
    },
    data() {
        return {
            isUser: undefined
        }
    },
    mounted() {
        if (get(userType) == user) {
            this.isUser = true
        } else this.isUser = false
    }
}

</script>

<template>
    <div v-if="isUser">
        <CompanyProfile></CompanyProfile>
    </div>
    <div v-else>
        <FreelProfile></FreelProfile>
    </div>
</template>
