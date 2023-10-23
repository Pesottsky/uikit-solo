import Axios from 'axios';
import { useAuthStore } from "@/stores/auth.store";

const httpClient = Axios.create({
    baseURL: import.meta.env.VITE_API_URL,
    headers: {
        'Content-Type': 'application/json'
    },
    timeout: 60000
});

httpClient.interceptors.request.use(
    config => {
        const store = useAuthStore()
        if (store.isAuthenticated) {
            config.headers.Authorization = `Bearer ${store.userData.access}`;
        } else {
            delete config.headers.Authorization;
        }
        return config;
    }
);


httpClient.interceptors.response.use(
    (response) => {
        if (response.status >= 200 && response.status < 300) {
            return response.data;
        }  else {
            return Promise.reject("error");
        }
    },
    async (error) => {

        const config = error.config;
        const res = error.response;
        
        if (res?.status === 401 && !config._retry) {
            config._retry = true;
            const store = useAuthStore();
            await store.refresh();

            config.headers.Authorization = `Bearer ${store.userData.access}`;

            return httpClient(config);
        }

        if (res?.data) {
            throw res.data;
        }

        throw Promise.reject(error);
    }
    // todo: future refresh jwt functionality
    // async (error) => {
    //     try {
    //         const res = error.response;
    //         console.debug(res)
    //
    //         if(res.code === 403){
    //            throw error;
    //         }
    //         const originalRequest = error.config;
    //         if (res.code === 401) {
    //
    //             refresh jwt function
    //             await store.dispatch(REFRESH_TOKEN);
    //             originalRequest.headers["Authorization"] = store.getAuth()
    //             const r = await axios(originalRequest);
    //             return r.data;
    //         }
    //     } catch (e) {
    //         console.log("err" + e); // for debug
    //         return Promise.reject(e);
    //     }
    // }
);

export default httpClient;