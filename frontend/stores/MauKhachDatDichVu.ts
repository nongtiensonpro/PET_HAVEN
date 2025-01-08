import { TuyChonDichVu, TuyChonCanNang } from '~/models/DichVu';

interface BookingData {
  date: string;
  dichvu: {
    id: number;
    tendichvu: string;
    mota: string;
    anh: string | null;
    trangthai: boolean;
    hien: boolean;
  };
  tuyChonDichVu: TuyChonDichVu;
  tuyChonCanNang: TuyChonCanNang;
  emailNguoiDat: string;
  id: number;
  idcalichhen: {
    id: number;
    tenca: string;
    thoigianca: string;
    trangthai: boolean;
  };
  idkhachhang: string;
  solanthaydoi: number;
  thoigianhuy: string | null;
  thoigianthaydoi: string | null;
  thucung: {
    id: number;
    ten: string;
    cannang: number;
    tuoi: number;
    giong: string;
  };
  trangthai: number;
  trangthaica: boolean;
}

export const useMauKhachDatDichVu = defineStore('mauKhachDatDichVu', () => {
  const tempData = ref<BookingData | null>(null);

  function setTempData(data: BookingData) {
    tempData.value = data;
  }

  function getTempData(): BookingData | null {
    return tempData.value;
  }

  function updateDataAfterBooking(data: BookingData) {
    tempData.value = data;
  }

  function saveTempData(data: BookingData) {
    tempData.value = data;
  }

  function clearTempData() {
    tempData.value = null;
  }

  return {
    tempData,
    setTempData,
    getTempData,
    updateDataAfterBooking,
    saveTempData,
    clearTempData
  };
});