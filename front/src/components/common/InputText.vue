<template>
  <div class="input__container">
    <div :class="'input__effect ' + filled">
      <label :style="{ color: color }">{{ label }}</label>
      <input type="text" :value="value" :placeholder="placeholder" @input="$emit('input', $event.target.value)"
        :class="'effect ' + has__icon" @focus="show = !show" @blur="show = !show" />
      <span class="focus__border" :style="focus__border"></span>
    </div>
    <span class="input__hint" v-if="show">{{ hint }}</span>
    <i class="material-icons input__icon">{{ icon }}</i>
  </div>
</template>

<script>
export default {
  data: () => ({
    show: false
  }),
  computed: {
    filled() {
      if (!this.show && this.value) {
        return 'has__content'
      }
      return ''
    },
    has__icon() {
      if (this.icon) {
        return 'input__has__icon'
      }
      return
    },
    focus__border() {
      return {
        "background-color": this.color
      }
    }
  },
  emits: ["input"],
  props: {
    value: { type: String, required: false, default: '' },
    label: { type: String, required: false, default: '' },
    hint: { type: String, required: false, default: '' },
    icon: { type: String, required: false, default: '' },
    placeholder: { type: String, required: false, default: '' },
    color: { type: String, required: false, default: 'indigo' }
  },
  model: {
    prop: 'value',
    event: 'input'
  },

}
</script>

<style scoped>
::placeholder {
  color: var(--black-opacity-50);
}

input[type="text"] {
  color: var(--black);
  background: var(--gray-light);
  border-radius: 6px;
  border: 1px solid transparent;
  outline: none;

  display: flex;
  width: 100%;
  padding: 12px 12px;
  gap: 8px;
  font-family: inherit;
}

.input__container {
  width: 100%;
}

.input__effect {
  float: left;
  width: 100%;
  margin: 0 0 12px 0;
  position: relative;
}

/* necessary to give position: relative to parent. */

.input__hint {
  float: left;
  width: 100%;
  margin: -12px 0 0 0;
  position: relative;
  font-size: 12px;
  opacity: .6;
}

.effect {
  border: 0;
  padding: 4px 0;
  background: transparent;
  transition: background 0.2s ease-in-out;
  transition: border 0.2s ease-in-out;
}

.effect:hover {
  background: var(--gray-light-hover);
  border: 1px solid var(--black-opacity-10);
}

.effect:focus {
  background: transparent;
  border: 1px solid var(--black);
}

input:checked {
  color: var(--black);
  background: var(--gray-light);
  border: 1px solid transparent;
}

input:invalid {
  border: 1px solid #FF6B00;
  color: #FF6B00;
}

/* input:valid {
  border: 1px solid #0ce250;
} */

/* скрываем плэйсхолдер по клику */
input:focus::-webkit-input-placeholder {
  color: transparent;
}

input:focus:-moz-placeholder {
  color: transparent;
}

/* FF 4-18 */
input:focus::-moz-placeholder {
  color: transparent;
}

/* FF 19+ */
input:focus:-ms-input-placeholder {
  color: transparent;
}

/* IE 10+ */


/* .effect ~ .focus__border {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 2px;
  background-color: indigo;
  transition: 0.4s;
}

.effect:focus ~ .focus__border ,
.has-content.effect ~ .focus__border {
  width: 100%;
  transition: 0.4s;
} 

.effect ~ label {
  position: absolute;
  left: 0;
  width: 100%;
  top: -1.3rem;
  color: #aaa;
  transition: 0.3s;
  z-index: -1;
  letter-spacing: 0.5px;
} 
.effect:focus ~ label,
.has__content.effect ~ label {
  top: -1rem;
  font-size: 12px;
  color: var(--Black);
  transition: 0.3s;
}

.input__has__icon {
  padding-left: 2rem!important;
}
.input__icon {
  position: relative;
  left: 0rem;
  top: -3.5rem;
  opacity:.3;
} 
*/</style>