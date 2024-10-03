
import { defineStore } from 'pinia';
import axios from 'axios';
import DichVu from '../models/DichVu';
import Pageable from '../models/Pageable';

export const useServiceStore = defineStore('serviceStore', {
    state: () => ({
        services: [] as DichVu[],  // Sử dụng interface DichVu
        pageable: {} as Pageable,
    }),
    actions: {
        async fetchServices() {
            try {
                const response = await axios.get('http://localhost:8080/api/dich-vu/all');
                this.services = response.data.content;
                this.pageable = response.data.pageable;
            } catch (error) {
                console.error('Error fetching services:', error);
            }
        },
    },
});
