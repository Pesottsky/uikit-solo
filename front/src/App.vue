<script>

export default {
  name: "App",

  methods: {
    open() {
      console.log("Hueta")
      this.$router.push("/components")
    }
  }

}

</script>

<template>
  <router-view></router-view>
</template>


<style scoped>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
