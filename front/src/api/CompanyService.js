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
}

export default CompanyService;