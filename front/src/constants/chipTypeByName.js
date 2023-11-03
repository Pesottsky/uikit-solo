import CHIP_TYPES from "./chipTypes"

const CHIP_TYPE_BY_NAME = {
    'Занят(а)': CHIP_TYPES.BUSY,
    'Есть время': CHIP_TYPES.AVAILABLE,
    'Свободен(на)': CHIP_TYPES.FREE,
    'Неизвестно': CHIP_TYPES.UNKNOWN
}

export default CHIP_TYPE_BY_NAME;