<template>
	<RouterView />
	<Notice />
</template>

<script setup>
	import { RouterView } from 'vue-router';
	import { Notice } from './components/UI';
</script>