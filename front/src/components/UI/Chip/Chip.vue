<template>
    <div class="chip" :class="chipClass">
        <span class="chip__indicator"></span>
        <span class="chip__text">{{ text }}</span>
    </div>
</template>

<script setup>
    import { computed } from 'vue';

    const props = defineProps({
        text: String,
        type: String
    })

    const chipClass = computed(() => `chip_${props.type}`);

</script>

<style lang="scss" scoped>
    .chip {
        width: auto;
        padding: 2px 6px;
        display: flex;
        align-items: center;
        justify-content: flex-start;
        gap: 4px;
        border-radius: 16px;
        white-space: nowrap;

        &__indicator {
            min-width: 8px;
            min-height: 8px;
            border-radius: 50%;
        }

        &_busy {
            background: rgba(255, 188, 150, 0.40);
            color: #D84100;

            .chip__indicator {
                opacity: 0.7;
                background: #D84100;
            }
        }
        &_available {
            background: rgba(255, 215, 73, 0.40);
            color: #B57800;

            .chip__indicator {
                opacity: 0.7;
                background: #D18A00;
            }
        }
        &_free {
            background: rgba(167, 255, 54, 0.40);
            color: #427400;
            
            .chip__indicator {
                opacity: 0.7;
                background: #5FA800;
            }
        }
        &_unknown {
            background: var(--gray-light);
            color: var(--black-opacity-50);

            .chip__indicator {
                background: #787878;
                opacity: 0.7;
            }
        }
    }
</style>