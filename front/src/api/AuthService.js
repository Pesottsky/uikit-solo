import httpClient from "./client";

class AuthService {
    static async registrationCompany(payload) {
        return httpClient.post('/registration', payload);
    }
    static async registrationFreelancer(payload, link = null) {
        const url = link ? `/registration/freel?link=${link}` : '/registration/freel';
        return await httpClient.post(url, payload);
    }
    static async login(payload, link) {
        const url = link ? `/login?link=${link}` : '/login'
        return await httpClient.post(url, payload);
    }
    static async logout() {
        return await httpClient.delete('/logout');
    }
    static async refresh(refresh) {
        return await httpClient.post('/refresh', { refresh });
    }
    static async forgetPassword(login) {
        return await httpClient.post('/forget_password', { login })
    }
    static async updatePassword(payload) {
        return await httpClient.post('/update_password', payload)
    }
}

export default AuthService;