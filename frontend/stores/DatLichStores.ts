import { defineStore } from 'pinia';
import DichVu from '../models/DichVu';
import CaHen from '../models/CaHen';

interface DatLichState {
  DichVu: DichVu[];
  CaLichHen: CaHen[];
  date: Date;
}

export const useDatLichStore = defineStore('datLichStore', {
  state: (): DatLichState => ({
    DichVu: [],
    CaLichHen: [],
    date: new Date()
  }),
  actions: {
    async fetchCaHen(this: DatLichState, date: Date = new Date()) {
      const token = sessionStorage.getItem('access_token');
      this.date = date;
      try {
        const response = await fetch(`http://localhost:8080/api/dat-lich/dat-lich-info?ngay=${date.toISOString().split('T')[0]}`, {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`,
          },
        });
        const data = await response.json();
        console.log(data);
        this.DichVu = data.dichVu ;
        this.CaLichHen = data.CaLichHen;
      } catch (error) {
        console.error('Error fetching services:', error);
      }
    },
    async updateDatLichInfo(this: DatLichState, date: Date) {
      const token = sessionStorage.getItem('access_token');
      this.date = date;
      try {
        const response = await fetch(`http://localhost:8080/api/dat-lich/dat-lich-info?ngay=${date}`, {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`,
          },
        });
        const data = await response.json();
        console.log('Updated data:', data);
        this.DichVu = data.dichVu;
        this.CaLichHen = data.CaLichHen;
      } catch (error) {
        console.error('Error updating Dat Lich info:', error);
      }
    }
  }
});