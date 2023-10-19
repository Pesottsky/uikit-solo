<template>
    <div class="notice" :class="[typeNotice, positionNotice]" v-if="show">
        {{ textMessage }}
    </div>
</template>

<script setup>
    import { useNoticeStore } from '@/stores/notice.store';
    import { storeToRefs } from 'pinia';

    const store = useNoticeStore();
    const { show, textMessage, typeNotice, positionNotice } = storeToRefs(store);
</script>

<style lang="scss" scoped>
    .notice {
        position: fixed;
        right: 50%;
        transform: translateX(50%);

        border-radius: 6px;
        padding: 11px;
        box-shadow: 0px 20px 72px 0px rgba(46, 28, 0, 0.10);
        font-family: Golos Text;
        font-size: 14px;
        font-weight: 400;
        line-height: 140%;
        z-index: 10;

        &_position {
            &_top {
                top: 40px;
            }
            &_bottom {
                bottom: 40px;
            }
        }

        &_error {
            background-color: var(--white);
            color: var(--black);
            border: 1px solid var(--input-error);
        }

        &_message {
            background-color: var(--black-opacity-50);
            color: var(--white);
        }
    }
</style>