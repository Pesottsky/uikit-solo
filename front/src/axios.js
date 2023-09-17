import axios from "axios"
import { get, accessKey, refreshKey, clear } from "./localstorage"

axios.defaults.baseURL = 'http://localhost:81';
export const axiosApiInstance = axios.create();


// Request interceptor for API calls
axiosApiInstance.interceptors.request.use(
    async config => {
        const accessToken = get(accessKey)
        config.headers = {
            'Authorization': `Bearer ${accessToken}`,
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        }
        return config;
    },
    error => {
        Promise.reject(error)
    });
// Response interceptor for API calls
axiosApiInstance.interceptors.response.use((response) => {
    return response
}, async function (error) {
    const originalRequest = error.config;
    if (error.response.status === 401 && !originalRequest._retry) {
        originalRequest._retry = true;
        const accessToken = await refreshAccessToken();
        axios.default.headers.common['Authorization'] = 'Bearer ' + accessToken;
        return axiosApiInstance(originalRequest);
    }
    return Promise.reject(error);
});

async function refreshAccessToken() {
    const { data, error } = await axiosApiInstance.get("/refresh", {
        token: {
            refresh: get(refreshKey)
        }
    })
    console.log(error)
    if (error != undefined) {
        clear()
        return
    }
    return data.token.accessToken
}