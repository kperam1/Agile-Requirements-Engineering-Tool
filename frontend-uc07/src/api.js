import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' }
});

export const addEstimate = (storyId, payload) => api.post(`/userstories/${storyId}/estimate`, payload);
export const getLatest = (storyId) => api.get(`/userstories/${storyId}/estimate`);
export const getHistory = (storyId) => api.get(`/userstories/${storyId}/estimates`);

export default api;
