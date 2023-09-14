export function set(obj) {
    for (const [key, value] of Object.entries(obj)) {
        localStorage.setItem(key, value);
    }
}

export function get(key) {
    localStorage.getItem(key);
}

export function clear() {
    localStorage.clear()
}

export const accessKey = "access"
export const refreshKey = "refresh"
