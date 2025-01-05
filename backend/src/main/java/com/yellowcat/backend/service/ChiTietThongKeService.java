package com.yellowcat.backend.service;

import com.yellowcat.backend.repository.ChiTietThongKeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class ChiTietThongKeService {
    
    @Autowired
    private ChiTietThongKeRepository chiTietThongKeRepository;

    public Map<String, Object> thongKeDoanhThuTheoDichVu(LocalDate startDate, LocalDate endDate) {
        List<Object[]> data = chiTietThongKeRepository.thongKeDoanhThuTheoDichVu(startDate, endDate);
        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        return result;
    }

    public Map<String, Object> thongKeThuCungTheoLoai(LocalDate startDate, LocalDate endDate) {
        List<Object[]> data = chiTietThongKeRepository.thongKeThuCungTheoLoai(startDate, endDate);
        Map<String, Object> result = new HashMap<>();
        for (Object[] row : data) {
            Boolean isCat = (Boolean) row[0];
            Long count = (Long) row[1];
            result.put(isCat ? "Mèo" : "Chó", count);
        }
        return result;
    }

    public Map<String, Object> tyLeHuyLich(LocalDate startDate, LocalDate endDate) {
        Object[] data = chiTietThongKeRepository.tyLeHuyLich(startDate, endDate);
        Map<String, Object> result = new HashMap<>();
        if (data != null && data.length == 2) {
            result.put("tyLeHuy", data[0]);
            result.put("tongSoLich", data[1]);
        }
        return result;
    }

    public Map<String, Object> thongKeGioDatLichPhoBien(LocalDate startDate, LocalDate endDate) {
        List<Object[]> data = chiTietThongKeRepository.thongKeGioDatLichPhoBien(startDate, endDate);
        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        return result;
    }
}
