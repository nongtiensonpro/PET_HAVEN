package com.yellowcat.backend.controller;

import com.yellowcat.backend.service.HoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/thong-ke")
@PreAuthorize("hasAnyRole('admin')")
public class ThongKeController {
    @Autowired
    private HoaDonService hoaDonService;

}
