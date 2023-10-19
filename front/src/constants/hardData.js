export class FakeFreelancer {
    constructor(data) {
        this.profile = {
            id: data.id,
            first_name: "",
            last_name: "",
            price: '',
            portfolio: "",
            experience: "",
            email: "",
            summary: "",
            skills: "",
            telegram: ""
        }
        this.fake = true;
        this.created = data?.created || false
    }
}