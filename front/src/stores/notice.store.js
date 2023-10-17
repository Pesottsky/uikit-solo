import { defineStore } from "pinia";
import { ref } from "vue";

export const useNoticeStore = defineStore('notice', () => {
    const show = ref(false);
    const textMessage = ref('');

    const timer = ref(null);

    function setText(text) {
        if (timer.value) clearTimeout(timer.value);
        if (text) {
            open();
            textMessage.value = text;
            timer.value = setTimeout(() => {
                close();
                clearTimeout(timer.value);
            }, 5000)
        }
    }
    function close() {
        show.value = false;
    }
    function open() {
        show.value = true
    }

    return {
        show,
        textMessage,
        setText,
        close,
        open
    }
})