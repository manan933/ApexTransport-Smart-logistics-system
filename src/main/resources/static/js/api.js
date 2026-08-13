/**
 * APEX TRANSPORT - UNIFIED API CLIENT
 * Handles JSON and FormData requests to Spring Boot REST endpoints with session cookies.
 */
function escapeHtml(str) {
  if (str === null || str === undefined) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}
const API_BASE = '/api';

// Toast Notification System
function showToast(message, type = 'success') {
  let container = document.getElementById('toast-container');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toast-container';
    document.body.appendChild(container);
  }

  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  toast.innerHTML = `
    <span>${type === 'success' ? '✓' : '⚠️'}</span>
    <span>${message}</span>
  `;

  container.appendChild(toast);
  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateX(50px)';
    toast.style.transition = 'all 0.3s ease';
    setTimeout(() => toast.remove(), 300);
  }, 4000);
}

 function formatRating(rating, count) {
  if (!count || count === 0) return 'New';
  return '⭐ ' + rating.toFixed(1);
}

const api = {
  async request(endpoint, options = {}) {
    const url = `${API_BASE}${endpoint}`;
    const defaultHeaders = {};

    if (options.body && !(options.body instanceof FormData)) {
      defaultHeaders['Content-Type'] = 'application/json';
    }

    const config = {
      ...options,
      headers: {
        ...defaultHeaders,
        ...options.headers,
      },
      credentials: 'include',
    };

    try {
      const response = await fetch(url, config);
      const data = await response.json().catch(() => ({}));

      if (!response.ok) {
        throw new Error(data.error || data.message || `Request failed (${response.status})`);
      }
      return data;
    } catch (err) {
      console.warn(`[API] ${endpoint} error:`, err.message);
      throw err;
    }
  },

  // Auth Endpoints
  async login(email, password) {
    return this.request('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
    });
  },

  async register(payload) {
    return this.request('/auth/register', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
  },

  async getMe() {
    try {
      return await this.request('/auth/me', { method: 'GET' });
    } catch (err) {
      return { authenticated: false };
    }
  },

  async logout() {
    await this.request('/auth/logout', { method: 'POST' }).catch(() => { });
    window.location.href = '/login.html';
  },

  async updateProfile(formData) {
    return this.request('/auth/profile', {
      method: 'POST',
      body: formData,
    });
  },

  // Transporter Endpoints
  async getTransporterDashboard() {
    return this.request('/transporter/dashboard', { method: 'GET' });
  },

  async getTransporterOrders() {
    return this.request('/transporter/orders', { method: 'GET' });
  },

  async getTransporterLogs() {
    return this.request('/transporter/logs', { method: 'GET' });
  },

  async createOrder(formData) {
    return this.request('/transporter/orders', {
      method: 'POST',
      body: formData,
    });
  },

  async cancelOrder(id, reason) {
    return this.request(`/transporter/orders/${id}/cancel?reason=${encodeURIComponent(reason || 'Cancelled by Shipper')}`, {
      method: 'POST',
    });
  },

  async rateDriver(orderId, data) {
    const rating = typeof data === 'object' ? data.rating : data;
    const review = typeof data === 'object' ? data.review : arguments[2];
    const params = new URLSearchParams({ rating, review: review || '' });
    return this.request(`/transporter/orders/${orderId}/rate-driver`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: params,
    });
  },

  async rateDriverAndPay(orderId, rating, review) {
    return this.rateDriver(orderId, { rating, review });
  },

  // Driver Endpoints
  async getDriverDashboard() {
    return this.request('/driver/dashboard', { method: 'GET' });
  },

  async getAvailableOrders() {
    return this.request('/driver/orders/available', { method: 'GET' });
  },

  async getDriverAvailableOrders() {
    return this.getAvailableOrders();
  },

  async getCurrentJob() {
    const res = await this.request('/driver/orders/current', { method: 'GET' });
    return res && res.job ? res.job : null;
  },

  async getDriverActiveOrder() {
    return this.getCurrentJob();
  },

  async acceptOrder(id) {
    return this.request(`/driver/orders/${id}/accept`, { method: 'POST' });
  },

  async startTransit(id) {
    return this.request(`/driver/orders/${id}/start`, { method: 'POST' });
  },

  async startOrderTransit(id) {
    return this.startTransit(id);
  },

  async completeOrderWithPod(id, formDataOrPayload) {
    let body = formDataOrPayload;
    if (!(formDataOrPayload instanceof FormData)) {
      body = new FormData();
      if (formDataOrPayload && formDataOrPayload.podImageUrl) {
        body.append('cameraBase64', formDataOrPayload.podImageUrl);
      }
    } else {
      // In case podImageUrl was passed in FormData, rename or add cameraBase64
      if (formDataOrPayload.has('podImageUrl') && !formDataOrPayload.has('cameraBase64')) {
        body.append('cameraBase64', formDataOrPayload.get('podImageUrl'));
      }
    }
    const res = await this.request(`/driver/orders/${id}/complete`, {
      method: 'POST',
      body: body,
    });

    // Also send shipper rating if provided
    if (formDataOrPayload instanceof FormData && formDataOrPayload.has('shipperRating')) {
      const rating = formDataOrPayload.get('shipperRating');
      const review = formDataOrPayload.get('shipperReview') || '';
      await this.rateShipper(id, rating, review).catch(() => { });
    }

    return res;
  },

  async rateShipper(orderId, rating, review) {
    return this.request(`/driver/orders/${orderId}/rate-shipper?rating=${rating}&review=${encodeURIComponent(review || '')}`, {
      method: 'POST',
    });
  },

  async getDriverLogs() {
    return this.request('/driver/logs', { method: 'GET' });
  },

  async updateFleetVehicles(vehicleType, additionalVehicles) {
    return this.request('/driver/fleet-vehicles', {
      method: 'POST',
      body: JSON.stringify({ vehicleType, additionalVehicles }),
    });
  },

  async reportEmergency(type, notes) {
    return this.request('/driver/emergency', {
      method: 'POST',
      body: JSON.stringify({ type, notes }),
    });
  },

  async resolveEmergency() {
    return this.request('/driver/emergency/resolve', { method: 'POST' });
  },

  async getBackhauls(city) {
    const q = city ? `?city=${encodeURIComponent(city)}` : '';
    return this.request(`/driver/backhauls${q}`, { method: 'GET' });
  },

  // Admin Endpoints
  async getAdminStats() {
    return this.request('/admin/stats', { method: 'GET' });
  },

  async getAllOrders() {
    return this.request('/admin/orders', { method: 'GET' });
  },

  async getAdminOrders() {
    return this.getAllOrders();
  },

  async getAllDrivers() {
    return this.request('/admin/drivers', { method: 'GET' });
  },

  async getAllTransporters() {
    return this.request('/admin/transporters', { method: 'GET' });
  },

  async getAuditLogs() {
    return this.request('/admin/logs', { method: 'GET' });
  },

  async getAdminLogs() {
    return this.getAuditLogs();
  },

  async createUser(formData) {
    return this.request('/admin/users/create', {
      method: 'POST',
      body: formData,
    });
  },

  async toggleUserStatus(id) {
    return this.request(`/admin/users/${id}/toggle-status`, { method: 'POST' });
  },

  async deleteUser(id) {
    return this.request(`/admin/users/${id}`, { method: 'DELETE' });
  },

  // Helper for Google Maps Redirection
  getGoogleMapsUrl(pickup, drop, pLat, pLng, dLat, dLng) {
    if (pLat && pLng && dLat && dLng) {
      return `https://www.google.com/maps/dir/?api=1&origin=${pLat},${pLng}&destination=${dLat},${dLng}&travelmode=driving`;
    }
    return `https://www.google.com/maps/dir/?api=1&origin=${encodeURIComponent(pickup || '')}&destination=${encodeURIComponent(drop || '')}&travelmode=driving`;
  }
};
