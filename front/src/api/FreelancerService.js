import httpClient from "./client";

class FreelancerService {
    static async getLoading() {
        return await httpClient.get('/loading');
    }
    static async getGrade() {
        return await httpClient.get('/grade');
    }
    static async getProfile() {
        return await httpClient.get('/profile');
    }
    static async getProfileById(id) {
        return await httpClient.get(`/profile/${id}`);
    }
    static async updateProfile(payload) {
        return await httpClient.put('/profile', payload);
    }
    static async updateLoading(payload) {
        return await httpClient.post('/loading', payload);
    }
}

export default FreelancerService;