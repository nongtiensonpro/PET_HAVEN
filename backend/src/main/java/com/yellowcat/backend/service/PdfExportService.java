package com.yellowcat.backend.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfExportService {


    public byte[] generateInvoice(String NgayThanhToan, String MaHoaDon, String PhuongThucThanhToan, String DichVu, double SoTien) {        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(outputStream);
            com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Tiêu đề hóa đơn
            document.add(new Paragraph("HÓA ĐƠN THANH TOÁN")
                    .setBold()
                    .setFontSize(18)
                    .setMarginBottom(20));

            // Tạo bảng với 2 cột
            float[] columnWidths = {3, 7}; // Độ rộng các cột
            Table table = new Table(columnWidths)
                    .setWidth(500)
                    .setMarginBottom(10);

            // Thêm các hàng thông tin
            addTableRowWithBorder(table, "Ngày thanh toán", NgayThanhToan);
            addTableRowWithBorder(table, "Số đơn đặt hàng", MaHoaDon);
            addTableRowWithBorder(table, "Phương thức thanh toán", PhuongThucThanhToan);
            addTableRowWithBorder(table, "Dịch vụ", DichVu);
            addTableRowWithBorder(table, "Số tiền", String.valueOf(SoTien));

            // Thêm bảng vào document
            document.add(table);

            // Đóng document
            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo PDF", e);
        }
    }

    /**
     * Hàm tiện ích để thêm một hàng vào bảng với viền mỏng
     */
    private void addTableRowWithBorder(Table table, String label, String value) {
        // Cell bên trái (nhãn)
        table.addCell(new Cell()
                .add(new Paragraph(label).setBold())
                .setBorderBottom(new SolidBorder(ColorConstants.BLACK, 0.5f))); // Viền mỏng

        // Cell bên phải (giá trị)
        table.addCell(new Cell()
                .add(new Paragraph(value))
                .setBorderBottom(new SolidBorder(ColorConstants.BLACK, 0.5f))); // Viền mỏng
    }
}



