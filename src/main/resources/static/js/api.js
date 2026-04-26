const API = '/api';

const api = {
    getToken: () => localStorage.getItem('tukac_jwt'),
    setToken: (t) => localStorage.setItem('tukac_jwt', t),
    clearAuth: () => { localStorage.removeItem('tukac_jwt'); localStorage.removeItem('tukac_user'); },
    getUser: () => { const u = localStorage.getItem('tukac_user'); return u ? JSON.parse(u) : null; },
    setUser: (u) => localStorage.setItem('tukac_user', JSON.stringify(u)),
    isAuth: () => !!localStorage.getItem('tukac_jwt'),
    isAdmin: () => { const u = api.getUser(); return u && (u.role==='ADMIN'||u.role==='EXECUTIVE'); },

    async req(endpoint, opts = {}) {
        const headers = { 'Content-Type': 'application/json' };
        const token = api.getToken();
        if (token) headers['Authorization'] = `Bearer ${token}`;
        if (opts.body && typeof opts.body === 'object') opts.body = JSON.stringify(opts.body);
        const res = await fetch(`${API}${endpoint}`, { ...opts, headers });
        const data = await res.json().catch(() => ({}));
        if (!res.ok) {
            if (res.status === 401 || res.status === 403) { api.clearAuth(); navigate('/login'); }
            throw new Error(data.error || data.message || 'Request failed');
        }
        return data;
    }
};
