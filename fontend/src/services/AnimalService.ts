import axios from 'axios';
import { Animal } from '../models/Animal'; // Import model Animal

const API_URL = 'http://localhost:8080/api/demo';

export const AnimalService = {
    getAll(): Promise<Animal[]> {  // Đảm bảo trả về mảng Animal[]
        return axios.get(API_URL).then(response => response.data);
    }
};
