import httpClient from "./client";

class CompanyService {
    static async getAllBases() {
        return await httpClient.get('/table');
    }
    static async createBase(payload) {
        return await httpClient.post(`/table?name=${encodeURI(payload)}`);
    }
    static async createRowInBase(payload) {
        return await httpClient.post('/table/row', payload);
    }
    static async updateRowInBaseById(id, payload) {
        return await httpClient.put(`/table/row/${id}`, payload);
    }
    static async generateLink(id) {
        return await httpClient.get('/link', { params: { row_id: id } });
    }
}

export default CompanyService;