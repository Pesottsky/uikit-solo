import httpClient from "./client";

class AuthService {
    static async registrationCompany(payload) {
        return httpClient.post('/registration', payload);
    }
    static async registrationFreelancer(payload, link=null) {
        const url = link ? `/registration/freel/${link}` : '/registration/freel';
        return await httpClient.post(url, payload);
    }
    static async login(payload) {
        return await httpClient.post('/login', payload);
    }
    static async logout() {
        return await httpClient.delete('/logout');
    }
    static async refresh(refresh) {
        return await httpClient.post('/refresh', { refresh });
    }
}

export default AuthService;