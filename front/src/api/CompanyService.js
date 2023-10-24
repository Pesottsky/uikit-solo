import httpClient from "./client";

class CompanyService {
    static async getAllBases() {
        return await httpClient.get('/table');
    }
    static async createBase(payload) {
        return await httpClient.post(`/table?name=${encodeURI(payload)}`);
    }
    static async renameBase(tableId, name) {
        return await httpClient.post(`/table/name/${tableId}?name=${encodeURI(name)}`);
    }
    static async createRowInBase(payload) {
        return await httpClient.post('/table/row', payload);
    }
    static async updateRowInBaseById(id, payload) {
        return await httpClient.put(`/table/row/${id}`, payload);
    }
    static async pushRowInBaseByProfileId(id) {
        return await httpClient.post(`/table/row/profile?profile_id=${id}`);
    }
    static async removeRowInBase(id) {
        return await httpClient.delete(`/table/row/${id}`);
    }
    static async generateLink(id) {
        return await httpClient.get('/link', { params: { row_id: id } });
    }
    static async sendInviteByEmail(payload) {
        return await httpClient.post('/send/email', payload);
    }
    static async getCompanyInfo() {
        return await httpClient.get('/company');
    }
    static async updateCompanyInfo(payload) {
        return await httpClient.put('/company', payload);
    }
    static async getComment(id) {
        return await httpClient.get('/comment', { params: { profile_id: id } });
    }
    static async createComment(payload) {
        return await httpClient.post('/comment', payload)
    }
}

export default CompanyService;