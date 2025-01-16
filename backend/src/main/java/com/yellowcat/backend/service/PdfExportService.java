package com.yellowcat.backend.service;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfExportService {


    public byte[] generateInvoice(String NgayThanhToan, String MaHoaDon, String PhuongThucThanhToan, String GiamGia, String DichVu, String SoTienBanDau, String SoTien, String ThoiGian, String TenKhachHang) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(outputStream);
            com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Load font hỗ trợ tiếng Việt
            String fontPath = "fonts/arial.ttf";
            PdfFont vietnameseFont = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H);

            // Tiêu đề hóa đơn
            document.add(
                    new Paragraph("HÓA ĐƠN THANH TOÁN")
                            .setFont(vietnameseFont) // Cài đặt font tiếng Việt
                            .setBold()
                            .setFontSize(18)
                            .setMarginBottom(20)
                            .setTextAlignment(TextAlignment.CENTER) // Căn giữa đoạn văn
            );

            // Tạo bảng với 2 cột
            float[] columnWidths = {3, 7}; // Độ rộng các cột
            Table table = new Table(columnWidths)
                    .setWidth(500)
                    .setMarginBottom(10);

            // Thêm các hàng thông tin
            addTableRowWithBorder(table, "Ngày thanh toán", NgayThanhToan, vietnameseFont);
            addTableRowWithBorder(table, "Mã hóa đơn", MaHoaDon, vietnameseFont);
            addTableRowWithBorder(table, "Phương thức thanh toán", PhuongThucThanhToan, vietnameseFont);
            addTableRowWithBorder(table, "Dịch vụ", DichVu, vietnameseFont);
            addTableRowWithBorder(table, "Thành tiền", String.format(SoTienBanDau), vietnameseFont);
            addTableRowWithBorder(table, "Giảm giá", GiamGia, vietnameseFont);
            addTableRowWithBorder(table, "Tổng tiền", String.format( SoTien), vietnameseFont);
            addTableRowWithBorder(table, "Thời gian lịch", ThoiGian, vietnameseFont);

            // Thêm bảng vào document
            document.add(table);

            // Thêm nội dung bên dưới bảng
            document.add(new Paragraph("PetHaven")
                    .setFont(vietnameseFont)
                    .setBold()
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(10)
            );

            document.add(new Paragraph("Tên khách hàng: " + TenKhachHang)
                    .setFont(vietnameseFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(5)
            );

            document.add(new Paragraph("PetHaven xin cảm ơn!")
                    .setFont(vietnameseFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(20)
            );

            // Đóng document
            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo PDF", e);
        }
    }


    public byte[] genHdDoiDV(String soTienTTDVDau,String tenKhachHang,String giaDVDau,String GiaDVSau,String NgayThanhToan, String MaHoaDon, String PhuongThucThanhToan, String DichVu, String DichVuDoi, String SoTien, String ThoiGian) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(outputStream);
            com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Load font hỗ trợ tiếng Việt
            String fontPath = "fonts/arial.ttf";
            PdfFont vietnameseFont = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H);

            // Tiêu đề hóa đơn
            document.add(
                    new Paragraph("HÓA ĐƠN THANH TOÁN")
                            .setFont(vietnameseFont) // Cài đặt font tiếng Việt
                            .setBold()
                            .setFontSize(18)
                            .setMarginBottom(20)
                            .setTextAlignment(TextAlignment.CENTER) // Căn giữa đoạn văn
            );


            // Tạo bảng với 2 cột
            float[] columnWidths = {3, 7}; // Độ rộng các cột
            Table table = new Table(columnWidths)
                    .setWidth(500)
                    .setMarginBottom(10);

            // Thêm các hàng thông tin
            addTableRowWithBorder(table, "Ngày thanh toán", NgayThanhToan, vietnameseFont);
            addTableRowWithBorder(table, "Mã hóa đơn", MaHoaDon, vietnameseFont);
            addTableRowWithBorder(table, "Phương thức thanh toán", PhuongThucThanhToan, vietnameseFont);
            addTableRowWithBorder(table, "Dịch vụ đầu", DichVu, vietnameseFont);
            addTableRowWithBorder(table, "Giá tiền", String.valueOf(giaDVDau), vietnameseFont);
            addTableRowWithBorder(table, "Số tiền thanh toán dịch vụ đầu", String.valueOf(soTienTTDVDau), vietnameseFont);
            addTableRowWithBorder(table, "Dịch vụ đổi", DichVuDoi, vietnameseFont);
            addTableRowWithBorder(table, "Giá tiền", String.valueOf(GiaDVSau), vietnameseFont);
            addTableRowWithBorder(table, "Số tiền thanh toán", String.valueOf(SoTien), vietnameseFont);
            addTableRowWithBorder(table, "Thời gian lịch", ThoiGian, vietnameseFont);

            // Thêm bảng vào document
            document.add(table);
// Thêm nội dung bên dưới bảng
            document.add(new Paragraph("PetHaven")
                    .setFont(vietnameseFont)
                    .setBold()
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(10)
            );

            document.add(new Paragraph("Tên khách hàng: " + tenKhachHang)
                    .setFont(vietnameseFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(5)
            );

            document.add(new Paragraph("PetHaven xin cảm ơn!")
                    .setFont(vietnameseFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(20)
            );
            // Đóng document
            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo PDF", e);
        }
    }

    // Hàm hỗ trợ thêm hàng vào bảng
    private void addTableRowWithBorder(Table table, String column1, String column2, PdfFont font) {
        table.addCell(new Cell().add(new Paragraph(column1).setFont(font))); // Cột 1
        table.addCell(new Cell().add(new Paragraph(column2).setFont(font))); // Cột 2
    }
}



