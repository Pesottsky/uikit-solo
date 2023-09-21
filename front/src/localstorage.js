export function set(obj) {
    for (const [key, value] of Object.entries(obj)) {
        localStorage.setItem(key, value);
    }
}

export function get(key) {
    return localStorage.getItem(key);
}

export function clear() {
    localStorage.clear()
}

export const accessKey = "accessKey"
export const refreshKey = "refreshKey"
export const userType = "userType"

export const freel = "freel"
export const user = "user"
