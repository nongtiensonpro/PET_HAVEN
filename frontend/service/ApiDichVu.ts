import axios, { AxiosInstance, AxiosResponse } from 'axios'

const apiClient: AxiosInstance = axios.create({
    baseURL: 'http://localhost:8080/api/dich-vu',
    headers: {
        'Content-Type': 'application/json'
    }
})

import DichVu from '../models/DichVu'

export default {
    getItems(): Promise<AxiosResponse<DichVu[]>> {
        return apiClient.get('http://localhost:8080/api/dich-vu/all')
            .then(response => response)
            .catch(error => {
                console.error("Error fetching items:", error);
                throw error;
            });
    },
    getItem(id: number): Promise<AxiosResponse<DichVu>> {
        return apiClient.get(`/dich-vu/${id}`)
            .then(response => response)
            .catch(error => {
                console.error(`Error fetching item with id ${id}:`, error);
                throw error;
            });
    },
    createItem(data: DichVu): Promise<AxiosResponse<DichVu>> {
        return apiClient.post(`/dich-vu`, data)
            .then(response => response)
            .catch(error => {
                console.error("Error creating item:", error);
                throw error;
            });
    },
    updateItem(id: number, data: DichVu): Promise<AxiosResponse<DichVu>> {
        return apiClient.put(`/dich-vu/${id}`, data)
            .then(response => response)
            .catch(error => {
                console.error(`Error updating item with id ${id}:`, error);
                throw error;
            });
    },
    deleteItem(id: number): Promise<AxiosResponse<void>> {
        return apiClient.delete(`/dich-vu/${id}`)
            .then(response => response)
            .catch(error => {
                console.error(`Error deleting item with id ${id}:`, error);
                throw error;
            });
    }
}

