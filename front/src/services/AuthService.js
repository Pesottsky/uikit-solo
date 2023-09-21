import { axiosApiInstance } from "../axios"

export function registration(name, email, password) {
    return axiosApiInstance.post("/registration", {
        name: name,
        login: email,
        password: password
    })
}

export function registration_freel(name, email, password) {
    return axiosApiInstance.post("/registration/freel", {
        login: email,
        password: password,
        name: name
    })
}

export function registration_freel_with_link(name, email, password) {
    return axiosApiInstance.post("/registration_with_link", {
        login: email,
        password: password,
        name: name
    })
}

export function login(email, password) {
    return axiosApiInstance.post("/login", {
        login: email,
        password: password
    })

}