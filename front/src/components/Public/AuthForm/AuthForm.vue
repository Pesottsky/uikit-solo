<template>
    <div class="auth">
        <div class="auth__title">
            <slot name="title"></slot>
        </div>
        <slot name="switcher"></slot>
        <div class="auth-form">
            <div class="auth-form__data">
                <slot name="form-data"></slot>
            </div>
            <div class="auth-form__action">
                <slot name="form-action"></slot>
            </div>
        </div>
        <div class="auth__links">
            <slot name="links"></slot>
        </div>
    </div>
</template>

<script setup>

</script>

<style lang="scss" scoped>
    .auth {
        width: 394px;
        margin-top: 48px;
        display: flex;
        flex-direction: column;
        gap: 16px;

        &__title {
            margin-bottom: 8px;
            text-align: center;
        }
        &-form {
            display: flex;
            flex-direction: column;
            gap: 24px;
            padding: 32px;
            background-color: var(--white);

            &__data {
                display: flex;
                flex-direction: column;
                gap: 16px;
            }
            &__action {
                display: flex;
                flex-direction: column;
                gap: 5px;
                align-items: center;
            }
        }
        &__links {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 12px;
        }
    }
</style>