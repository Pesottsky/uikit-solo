import Axios from 'axios';
import { useAuthStore } from "../stores/auth.store";

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
            if (response.data) {
                return response.data;
            }
            return response;
        }  else {
            return Promise.reject("error");
        }
    },
    async (error) => {

        const config = error?.config;
        const res = error.response;
        
        if (res?.status === 401 && !config.sent) {
            config.sent = true;

            const store = useAuthStore();

            await store.refresh();

            if (store.isAuthenticated) {
                config.headers = {
                    ...config.headers,
                    authorization: `Bearer ${store.userData.access}`,
                }
            }

            return httpClient(config);
        }

        if (res?.data) {
            throw res.data;
        }

        throw await Promise.reject(error);
    }
);

export default httpClient;