<template>
    <div class="dropdown">
        <div class="dropdown__container">
            <input 
                type="text" 
                v-model="inputValue" 
                class="dropdown__element" 
                :class="{ 'dropdown__element_active': inputValue }"
                :placeholder="placeholder"
                readonly 
                @focus="onOpen" 
                @focusout="onClose"
            />
            <div class="dropdown__icon">
                <ArrowCheckBottomIcon />
            </div>
        </div>
        <ul class="dropdown-list" :class="{ 'dropdown-list_open': isOpenList }">
            <li class="dropdown-list__item" v-for="(item, index) in data" :key="item.value" @click="() => selectItemList(index)">
                {{ item.value }}
                <CheckMarkIcon v-if="item.active" />
            </li>
        </ul>
    </div>
</template>

<script setup>
    import { computed, onMounted, ref } from 'vue';
    import { ArrowCheckBottomIcon, CheckMarkIcon } from '../../Icons';

    const props = defineProps({
        modelValue: { type: [String, Number] },
        list: { type: Array, default: () => [] },
        placeholder: { type: String }
    })
    const emits = defineEmits(['update:modelValue']);

    const isOpenList = ref(false);

    const inputValue = computed({
        get() {
            return props.modelValue
        },
        set(value) {
            emits('update:modelValue', value)
        }
    });

    const data = ref([...props.list]);

    function selectItemList(index) {
        data.value = data.value.map((item, i) => {
            item.active = i === index;
            return item;
        });
        emits('update:modelValue', data.value[index].value);
    }

    function onOpen() {
        isOpenList.value = true;
    }
    function onClose() {
        setTimeout(() => {
            isOpenList.value = false;
        }, 150)
    }

    onMounted(() => {
        data.value = data.value.map(item => {
            item.active = false;
            return item;
        })
    })
</script>

<style lang="scss" scoped>
    .dropdown {
        width: 100%;
        position: relative;

        &__container {
            width: 100%;
            position: relative;
            height: 36px;
        }
        &__element {
            width: 100%;
            border: 1px solid transparent;
            border-radius: 6px;
            outline: none;
            color: var(--black);
            opacity: 0.5;
            padding: 8px 30px 8px 6px;
            cursor: pointer;

            &_active {
                opacity: 1;
            }

            &:hover:not(&:focus) {
                background: var(--gray-light-hover);
                opacity: 1;
            }
            &:hover:not(&:focus) ~ .dropdown__icon {
                opacity: 1;
            }

            &:focus {
                border-color: var(--black-opacity-30);
                opacity: 1;
            }
            &:focus ~ .dropdown__icon {
                transform: rotate(180deg) translateY(50%);
                opacity: 1;
            }
            
        }
        &__icon {
            position: absolute;
            right: 6px;
            top: 50%;
            transform: translateY(-50%);
            display: flex;
            align-items: center;
            justify-content: center;
            opacity: 0.5;
        }

        &-list {
            position: absolute;
            top: 44px;
            width: 100%;
            border-radius: 8px;
            background: var(--white);
            box-shadow: 0px 20px 72px 0px rgba(46, 28, 0, 0.10);
            list-style: none;
            padding: 8px;
            display: none;
            flex-direction: column;
            gap: 4px;

            &_open {
                display: flex;
            }

            &__item {
                padding: 8px 6px;
                color: var(--black);
                opacity: 0.5;
                border-radius: 6px;
                display: flex;
                align-items: center;
                justify-content: space-between;
                gap: 4px;

                &:hover {
                    background: var(--gray-light-hover);
                    opacity: 1;
                }
            }
        }
    }
</style>