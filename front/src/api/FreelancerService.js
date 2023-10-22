import httpClient from "./client";

class FreelancerService {
    static async getProfile() {
        return await httpClient.get('/profile');
    }
    static async updateProfile(payload) {
        return await httpClient.put('/profile', payload);
    }
    static async updateLoading(payload) {
        return await httpClient.post('/update_loading', payload);
    }
}

export default FreelancerService;