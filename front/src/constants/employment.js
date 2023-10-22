export const EMPLOYMENT_NAME = {
    BUSY: 'Занят',
    AVAILABLE: 'Есть время',
    FREE: 'Свободен',
    UNKNOWN: 'Неизвестно'
}

export const EMPLOYMENT_LEVEL = {
    [EMPLOYMENT_NAME.UNKNOWN]: 0,
    [EMPLOYMENT_NAME.FREE]: 1,
    [EMPLOYMENT_NAME.AVAILABLE]: 2,
    [EMPLOYMENT_NAME.BUSY]: 3,
}

export const EMPLOYMENT_VALUE = [
    { value: EMPLOYMENT_NAME.FREE, level: 1 },
    { value: EMPLOYMENT_NAME.AVAILABLE, level: 2 },
    { value: EMPLOYMENT_NAME.BUSY, level: 3 }
]