import { useTempDataStore } from '~/stores/tempDataStore'
import type DichVuKhachDat from '~/models/DichVuKhachDat';

export const useMauKhachDatDichVu = () => {
  const tempDataStore = useTempDataStore()

  const saveTempData = (data: DichVuKhachDat) => {
    tempDataStore.setDichVuKhachDat(data)
  }

  const getTempData = () => {
    return tempDataStore.getDichVuKhachDat
  }

  const clearTempData = () => {
    tempDataStore.clearDichVuKhachDat()
  }

  return {
    saveTempData,
    getTempData,
    clearTempData
  }
}