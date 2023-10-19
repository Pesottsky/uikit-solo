import { defineStore } from "pinia";
import { ref } from "vue";
import { NOTIFICATION_TYPE, NOTIFICATION_POSITION } from "../constants/notification";

export const useNoticeStore = defineStore('notice', () => {
    const show = ref(false);
    const textMessage = ref('');
    const typeNotice = ref(NOTIFICATION_TYPE.ERROR);
    const positionNotice = ref(NOTIFICATION_POSITION.BOTTOM);

    const timer = ref(null);

    // Универсальная штука, создавать можно любое уведомление
    function setNotification(text, { type=null, position=null, durationMs=5000 }={}) {
        if (timer.value) clearTimeout(timer.value);
        if (text) {
            open();
            textMessage.value = text;

            typeNotice.value = type || NOTIFICATION_TYPE.ERROR;
            positionNotice.value = position || NOTIFICATION_POSITION.BOTTOM;

            timer.value = setTimeout(() => {
                close();
                clearTimeout(timer.value);
            }, durationMs)
        }
    }

    function setMessage(text) {
        setNotification(text, { type: NOTIFICATION_TYPE.MESSAGE, position: NOTIFICATION_POSITION.TOP });
    }
    function setError(text) {
        setNotification(text, { type: NOTIFICATION_TYPE.ERROR, position: NOTIFICATION_POSITION.BOTTOM });
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
        typeNotice,
        positionNotice,
        setNotification,
        setMessage,
        setError
    }
})