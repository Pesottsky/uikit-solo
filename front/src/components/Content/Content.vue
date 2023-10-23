<template>
    <main class="content">
        <slot></slot>
    </main>
</template>

<style lang="scss" scoped>
    .content {
        flex: 1 1 auto;
        display: flex;
        flex-direction: column;
        justify-content: flex-start;
        background-color: var(--biege-light);
        padding: 16px 24px 16px 48px;
        overflow: auto;
    }
</style>