package com.yellowcat.backend.service;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfExportService {

    public byte[] generateInvoice(String customerName, String invoiceId, String details) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(outputStream);
            com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Tiêu đề hóa đơn
            document.add(new Paragraph("HÓA ĐƠN THANH TOÁN").setBold().setFontSize(20));

            // Thông tin khách hàng
            document.add(new Paragraph("Tên khách hàng: " + customerName));
            document.add(new Paragraph("Mã hóa đơn: " + invoiceId));
            document.add(new Paragraph("Chi tiết: " + details));
            document.add(new Paragraph(" "));

            // Bảng chi tiết
            Table table = new Table(UnitValue.createPercentArray(new float[]{4, 4, 2}));
            table.addHeaderCell("Mục");
            table.addHeaderCell("Mô tả");
            table.addHeaderCell("Số tiền");

            // Thêm các dòng vào bảng
            table.addCell("1");
            table.addCell("Spotify Premium");
            table.addCell("$9.99");
            table.addCell("2");
            table.addCell("Thuế VAT");
            table.addCell("$0.99");

            document.add(table);

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo PDF", e);
        }
    }


}
