const API_BASE = 'http://localhost:5000';

class BountyAPI {
    constructor() {
        this.token = localStorage.getItem('token');
        this.user = JSON.parse(localStorage.getItem('user') || 'null');
    }

    setAuth(token, user) {
        this.token = token;
        this.user = user;
        localStorage.setItem('token', token);
        localStorage.setItem('user', JSON.stringify(user));
    }

    clearAuth() {
        this.token = null;
        this.user = null;
        localStorage.removeItem('token');
        localStorage.removeItem('user');
    }

    getHeaders() {
        const headers = {'Content-Type': 'application/json'};
        if (this.token) {
            headers['Authorization'] = `Bearer ${this.token}`;
        }
        return headers;
    }

    async request(endpoint, options = {}) {
        const url = `${API_BASE}${endpoint}`;
        const config = {
            ...options,
            headers: this.getHeaders()
        };

        try {
            const response = await fetch(url, config);
            const data = await response.json();
            
            if (!response.ok) {
                throw new Error(data.error || 'Request failed');
            }
            
            return data;
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    }

    async register(username, email, password) {
        return this.request('/api/register', {
            method: 'POST',
            body: JSON.stringify({username, email, password})
        });
    }

    async login(username, password) {
        const data = await this.request('/api/login', {
            method: 'POST',
            body: JSON.stringify({username, password})
        });
        this.setAuth(data.token, data.user);
        return data;
    }

    async getBounties(status = null) {
        let endpoint = '/api/bounties';
        if (status) {
            endpoint += `?status=${status}`;
        }
        return this.request(endpoint);
    }

    async createBounty(bountyData) {
        return this.request('/api/bounties', {
            method: 'POST',
            body: JSON.stringify(bountyData)
        });
    }

    async claimBounty(bountyId) {
        return this.request(`/api/bounties/${bountyId}/claim`, {
            method: 'POST'
        });
    }

    async submitProof(claimId, proofDescription, proofUrl = null) {
        return this.request(`/api/claims/${claimId}/submit`, {
            method: 'POST',
            body: JSON.stringify({
                proof_description: proofDescription,
                proof_url: proofUrl
            })
        });
    }

    async getMyClaims() {
        return this.request('/api/claims/my');
    }

    isAuthenticated() {
        return !!this.token;
    }

    getUser() {
        return this.user;
    }
}

const api = new BountyAPI();
