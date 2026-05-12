// ============================================================
// api.js - Fetch wrapper with JWT auth + helpers
// ============================================================

const API_BASE = '/api';

function getToken() {
  return localStorage.getItem('tukac_token');
}

function getUser() {
  const u = localStorage.getItem('tukac_user');
  return u ? JSON.parse(u) : null;
}

function saveSession(token, user) {
  localStorage.setItem('tukac_token', token);
  localStorage.setItem('tukac_user', JSON.stringify(user));
}

function clearSession() {
  localStorage.removeItem('tukac_token');
  localStorage.removeItem('tukac_user');
}

function requireAuth() {
  if (!getToken()) {
    window.location.href = '/login.html';
    return false;
  }
  return true;
}

function requireRole(...roles) {
  const user = getUser();
  if (!user || !roles.includes(user.role)) {
    window.location.href = '/dashboard.html';
    return false;
  }
  return true;
}

async function apiCall(endpoint, options = {}) {
  const token = getToken();
  const headers = {
    'Content-Type': 'application/json',
    ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
    ...(options.headers || {})
  };

  try {
    const response = await fetch(`${API_BASE}${endpoint}`, {
      ...options,
      headers
    });

    if (response.status === 401) {
      clearSession();
      window.location.href = '/login.html';
      return null;
    }

    if (options.responseType === 'blob') {
      const blob = await response.blob();
      return { ok: response.ok, status: response.status, data: blob };
    }

    const data = await response.json();
    return { ok: response.ok, status: response.status, data };
  } catch (error) {
    console.error('API Error:', error);
    return { ok: false, status: 0, data: { success: false, message: 'Network error. Is the server running?' } };
  }
}

const api = {
  get: (endpoint, options = {}) => apiCall(endpoint, { method: 'GET', ...options }),
  post: (endpoint, body) => apiCall(endpoint, { method: 'POST', body: JSON.stringify(body) }),
  put: (endpoint, body) => apiCall(endpoint, { method: 'PUT', body: JSON.stringify(body) }),
  delete: (endpoint) => apiCall(endpoint, { method: 'DELETE' }),
};

// ============================================================
// UI Helpers
// ============================================================

function showAlert(el, type, message) {
  if (!el) return;
  el.className = `alert alert-${type}`;
  el.textContent = message;
  el.classList.remove('hidden');
  if (type === 'success') {
    setTimeout(() => el.classList.add('hidden'), 4000);
  }
}

function formatDate(dateStr) {
  if (!dateStr) return '—';
  try {
    return new Date(dateStr).toLocaleDateString('en-KE', {
      year: 'numeric', month: 'short', day: 'numeric'
    });
  } catch { return dateStr; }
}

function formatCurrency(amount) {
  return 'KES ' + Number(amount).toLocaleString('en-KE', { minimumFractionDigits: 2 });
}

function formatDateTime(dateStr) {
  if (!dateStr) return '—';
  try {
    return new Date(dateStr).toLocaleDateString('en-KE', {
      year: 'numeric', month: 'short', day: 'numeric',
      hour: '2-digit', minute: '2-digit'
    });
  } catch { return dateStr; }
}

function setLoading(container, text = 'Loading...') {
  container.innerHTML = `<div class="loading"><div class="spinner"></div>${text}</div>`;
}

function setEmpty(container, icon, title, subtitle = '') {
  container.innerHTML = `
    <div class="empty-state">
      <div class="empty-icon">${icon}</div>
      <h3>${title}</h3>
      ${subtitle ? `<p>${subtitle}</p>` : ''}
    </div>`;
}

// Render sidebar user info + active nav
function initSidebar() {
  const user = getUser();
  if (!user) return;

  const nameEl = document.getElementById('userName');
  const roleEl = document.getElementById('userRole');
  const avatarEl = document.getElementById('userAvatar');

  if (nameEl) nameEl.textContent = user.name;
  if (roleEl) roleEl.textContent = user.role;
  if (avatarEl) avatarEl.textContent = user.name.charAt(0).toUpperCase();

  // Highlight active nav
  const currentPage = window.location.pathname.split('/').pop() || 'index.html';
  document.querySelectorAll('.nav-item[data-page]').forEach(item => {
    if (item.dataset.page === currentPage) {
      item.classList.add('active');
    }
  });

  // Management roles: can create/edit events, finances, blog
  const MANAGEMENT_ROLES = ['chairperson','vice-chairperson','secretary','treasurer'];
  const CHAIRPERSON_ONLY = ['chairperson'];

  // Show/hide management and admin sections
  const execSection = document.getElementById('execSection');
  const adminSection = document.getElementById('adminSection');
  if (execSection) {
    execSection.classList.toggle('hidden', !MANAGEMENT_ROLES.includes(user.role));
  }
  if (adminSection) {
    adminSection.classList.toggle('hidden', !CHAIRPERSON_ONLY.includes(user.role));
  }
}

function logout() {
  clearSession();
  window.location.href = '/login.html';
}
