export class FakeFreelancer {
    constructor(data) {
        this.profile = {
            id: data.id,
            first_name: "",
            last_name: null,
            price: null,
            portfolio: null,
            experience: null,
            email: null,
            summary: null,
            skills: null,
            telegram: null,
            grade: null,
            loading: null
        }
        this.can_change = true;
        this.fake = true;
        this.created = data?.created || false;
    }
}