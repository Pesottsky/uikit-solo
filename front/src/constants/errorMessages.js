const ERROR_MESSAGES = {
    REQUIRED: 'Обязательно для заполнения',
    EMAIL: 'Неверный формат email',
    NO_MATCH_PASSWORD: 'Пароли не совпадают',
    MIN_LENGTH: (count) => `Минимальная длинна ${count}`,
}

export default ERROR_MESSAGES;