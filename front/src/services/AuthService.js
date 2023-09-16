import {axiosApiInstance} from "../axios"

export async function registration(name, email, password) {
    await axiosApiInstance.post("/registration" ,{
        name: name,
        email: email,
        password: password
    })
}

export async function login(email, password) {
    await axiosApiInstance.post("/login" ,{
        email: email,
        password: password
    })
}