<template>
    <div class="card">
        <template v-if="isEdit">
            <div class="card__name">Имя</div>
            <div class="card__value">
                <Input placeholder="Ваше имя" v-model="state.first_name" />
            </div>

            <div class="card__name">Фамилия</div>
            <div class="card__value">
                <Input placeholder="Ваша фамилия" v-model="state.last_name" />
            </div>
        </template>
        <template v-else>
            <div class="card__name">Загрузка</div>
            <div class="card__value">
                <Chip :text="state.loading || 'Не ясно'" :type="CHIP_TYPE_BY_NAME[state.loading] || CHIP_TYPES.UNKNOWN" />
            </div>
        </template>

        <div class="card__name">Грейд</div>
        <div class="card__value">
            <Radiobutton v-if="isEdit" :data="gradeList" :is-background="true" v-model="state.grade" />
            <p v-else>{{ state.grade || DEFAULT_VALUE }}</p>
        </div>

        <div class="card__name">Стоимость 1 часа работы (₽)</div>
        <div class="card__value">
            <Input v-if="isEdit" type="number" placeholder="1000"  v-model="state.price" />
            <p v-else>{{ state.price || DEFAULT_VALUE }}</p>
        </div>

        <div class="card__name">Портфолио</div>
        <div class="card__value">
            <Input v-if="isEdit" placeholder="https://Behance.com"  v-model="state.portfolio" />
            <a :href="state.portfolio" target="_blank" v-else-if="state.portfolio">{{ state.portfolio }}</a>
            <p v-else>{{ DEFAULT_VALUE }}</p>
        </div>

        <div class="card__name">Сколько лет опыта</div>
        <div class="card__value">
            <Input v-if="isEdit" placeholder="3 года" v-model="state.experience" />
            <p v-else>{{ state.experience || DEFAULT_VALUE }}</p>
        </div>

        <div class="card__name">Скилы</div>
        <div class="card__value">
            <Input v-if="isEdit" placeholder="Веб-дизайн, логотипы, верстка" hint="На чем специализируетесь, перечислите 1-5 направлений, например: Веб-дизайн, логотипы, верстка" v-model="state.skills" />
            <p v-else>{{ state.skills || DEFAULT_VALUE }}</p>
        </div>

        <div class="card__name">О себе</div>
        <div class="card__value">
            <template v-if="isEdit">
                <Textarea v-if="visibleTextarea" placeholder="Привет! Последние 3 года фокусируюсь на UI/UX проектах, работала в такой компании...                 " hint="Расскажите подробнее о себе, про ваш опыт или чем можете помочь" v-model="state.summary" />
            </template>
            <p v-else>{{ state.summary || DEFAULT_VALUE }}</p>
        </div>

        <div class="card__name">Email</div>
        <div class="card__value">
            <Input v-if="isEdit" placeholder="name@email.com" v-model="state.email" />
            <p v-else>{{ state.email || DEFAULT_VALUE }}</p>
        </div>

        <div class="card__name">Телеграм</div>
        <div class="card__value">
            <Input v-if="isEdit" placeholder="@your_name" v-model="state.telegram" />
            <p v-else>{{ state.telegram || DEFAULT_VALUE }}</p>
        </div>
    </div>
</template>

<script setup>
    import { storeToRefs } from 'pinia';
    import { computed, onMounted, reactive, ref, watch } from 'vue';
    import { Input, Textarea, Radiobutton, Chip } from '../../UI';
    import { useFreelancerStore } from '../../../stores/freelancer.store';
    
    import { FakeFreelancer } from '../../../constants/FakeFreelancer';
    import CHIP_TYPE_BY_NAME from '../../../constants/chipTypeByName';
    import CHIP_TYPES from '../../../constants/chipTypes';

    const props = defineProps({
        freelancer: { type: Object, default: () => {} },
        isEdit: { type: Boolean, default: true }
    })

    const DEFAULT_VALUE = '-';

    const storeFreelancer = useFreelancerStore();
    const { directory } = storeToRefs(storeFreelancer);

    const state = reactive({ ...new FakeFreelancer({ id: 'f1' }).profile, grade: '' });

    // Чтобы textarea появлялась только когда туда устанавливается реальное значение, иначе не будет расширяться
    const visibleTextarea = ref(false);

    const gradeList = computed(() => directory.value.grade.map(({ level_key, description }) => ({ key: level_key, value: description })))

    function setState() {
        Object.keys(state).map(key => {
            if (key === 'loading' || key === 'grade') {
                if (props.freelancer?.[key]) state[key] = props.freelancer[key].description;
            } else {
                if (props.freelancer?.[key]) state[key] = props.freelancer[key];
            }
        })

        visibleTextarea.value = true;
    }

    watch(props, value => {
        visibleTextarea.value = false;
        setState();
    })

    onMounted(() => {
        setState();
    })

    defineExpose({
        modelData: state
    })
</script>

<style lang="scss" scoped>
    .card {
        display: grid;
        grid-template-columns: 2fr 5fr;
        gap: 18px;
        width: 100%;

        &__value, &__name {
            width: 100%;
            min-height: 36px;
            height: auto;
            display: flex;
            justify-content: flex-start;
            align-items: center;
        }
        &__name {
            align-items: flex-start;
            color: var(--black-opacity-50);
            font-family: Golos Text;
            font-size: 14px;
            font-weight: 400;
            line-height: 140%;
            padding: 8px 0px;
        }
    }
</style>