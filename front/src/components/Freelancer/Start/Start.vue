<template>
    <h1 class="start__title">Привет! Это сервис для ведения<br /> базы аутсорса Soloteam</h1>
    <div class="start__description">
        <p class="start__text">Здесь ты можешь отслеживать занятость чтобы заказчики понимали когда можно обращаться и быть с нмим на связи</p>
        <p class="start__text">А еще делить профилем портфолио, чтоб не теряться</p>
    </div>
</template>

<script setup>

</script>

<style lang="scss" scoped>
    .start {
        &__title {
            margin-top: 40px;
        }
        &__text {
            color: var(--black);
            font-family: Inter;
            font-size: 17px;
            font-style: normal;
            font-weight: 400;
            line-height: normal;
        }
        &__description {
            margin-top: 48px;
            width: 661px;
            display: flex;
            flex-direction: column;
            gap: 17px;
        }
    }
</style>