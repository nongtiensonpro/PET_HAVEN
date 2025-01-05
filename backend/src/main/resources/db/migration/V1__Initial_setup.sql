CREATE TABLE GiamGia
(
    ID           SERIAL PRIMARY KEY,
    PhanTramGiam INT       not null,
    NgayBatDau   TIMESTAMP not null,
    NgayKetThuc  TIMESTAMP not null,
    MoTa         TEXT,
    TrangThai    BOOLEAN
);

CREATE TABLE ThongTinCaNhan
(
    ID         SERIAL PRIMARY KEY,
    HoTen      VARCHAR(100),
    Tuoi       INT,
    GioiTinh   VARCHAR(10),
    idTaiKhoan varchar(255) not null,
    SDT        VARCHAR(20),
    Image      VARCHAR(100),
    Email      varchar(255),
    Role       varchar(100)
);

CREATE TABLE DichVu
(
    ID        SERIAL PRIMARY KEY,
    TenDichVu VARCHAR(100) NOT NULL,
    MoTa      TEXT         NOT NULL,
    Anh       TEXT,
    TrangThai BOOLEAN      NOT NULL,
    Hien      BOOLEAN      NOT NULL
);

CREATE TABLE TuyChonDichVu
(
    ID         SERIAL PRIMARY KEY,
    IDDichVu   INT REFERENCES DichVu (ID),
    TenTuyChon VARCHAR(100) NOT NULL,
    MoTa       TEXT,
    TrangThai  BOOLEAN      NOT NULL
);

CREATE TABLE TuyChonCanNang
(
    ID              SERIAL PRIMARY KEY,
    IDTuyChonDichVu INT REFERENCES TuyChonDichVu (ID),
    CanNangMin      FLOAT,
    CanNangMax      FLOAT,
    GiaTien         INT     NOT NULL,
    TrangThai       BOOLEAN NOT NULL
);

CREATE TABLE ThuCung
(
    ID               SERIAL PRIMARY KEY,
    Ten              VARCHAR(100) NOT NULL,
    CanNang          FLOAT CHECK (CanNang >= 0),
    Tuoi             INT CHECK (Tuoi >= 0),
    Giong            VARCHAR(100),
    IDTaiKhoan       VARCHAR(255) not null,
    Image            TEXT,
    GioiTinh         BOOLEAN      NOT NULL,
    CoPhaiMeoKhong   BOOLEAN      NOT NULL,
    TinhTrangSucKhoe TEXT,
    MoTa             TEXT
);


CREATE TABLE CaLichHen
(
    ID         SERIAL PRIMARY KEY,
    ThoiGianCa TIME NOT NULL,
    TrangThai  BOOLEAN
);

CREATE TABLE LichHen
(
    ID              SERIAL PRIMARY KEY,
    idKhachHang     VARCHAR(255) NOT NULL,
    idThuCung       INT REFERENCES ThuCung (ID),
    idTuyChonCanNang        INT REFERENCES TuyChonCanNang (ID),
    Date            DATE         NOT NULL,
    TrangThai       INT          NOT NULL,
    EmailNguoiDat   VARCHAR(255) NOT NULL default '123@gmail.com',
    idCaLichHen     INT REFERENCES CaLichHen (ID),
    TrangThaiCa     BOOLEAN,
    ThoiGianHuy     TIMESTAMP,
    ThoiGianThayDoi TIMESTAMP,
    SoLanThayDoi    INT          not null default 0,
    SoLanNhacNho    INT
);
-- quy ước trang thai:
-- 0 : thành công
-- 1 : đã đổi
-- 2 : đã hủy
-- 3 : chờ thanh toán
-- 4 : chờ xác nhận
-- 5 : Rỗng
-- 6 : Thanh toán thành công
-- 7 : Đã hoàn tiền
-- 8 : Chờ sử dụng

CREATE TABLE HoaDon
(
    ID                  SERIAL PRIMARY KEY,
    MaGiaoDich          VARCHAR(255)   Not null UNIQUE,
    idLichHen           INT REFERENCES LichHen (ID),
    Date                TIMESTAMP,
    SoTienBanDau        DECIMAL(10, 2) NOT NULL,
    SoTien              DECIMAL(10, 2) NOT NULL,
    NgayThanhToan       TIMESTAMP,
    PhuongThucThanhToan VARCHAR(50),
    TrangThai           INT            not null,
    NguoiThanhToan      VARCHAR(255),
    idGiamGia           INT REFERENCES GiamGia (ID)
);
-- Quy ước:
-- 1: Chờ thanh toán
-- 2: Thành công
-- 3: Thất bại
-- 4: Đã hoàn tiền

CREATE TABLE HoaDonDoiDichVu
(
    ID                  SERIAL PRIMARY KEY,
    MaGiaoDich          VARCHAR(255)   Not null UNIQUE,
    IdHoaDOn            INT REFERENCES HoaDon (ID),
    IDTuyChonCanNang    INT REFERENCES  TuyChonCanNang(ID),
    SoTien              DECIMAL(10, 2) NOT NULL,
    NgayThanhToan       TIMESTAMP,
    GhiChu              VARCHAR(1000),
    TrangThai           INT            not null,
    NguoiThanhToan      VARCHAR(255),
    TrangThaiThanhToan         BOOLEAN
);
-- 1: Hoàn tiền
-- 2: Nhận tiền

CREATE TABLE DanhGia
(
    ID         SERIAL PRIMARY KEY,
    idTaiKhoan VARCHAR(255) not null,
    SoSao      VARCHAR(5)   not null,
    MoTa       TEXT,
    Date       TIMESTAMP,
    TrangThai  BOOLEAN,
    idHoaDon   INT REFERENCES HoaDon (ID)
);

CREATE TABLE NgayNghi
(
    ID        SERIAL PRIMARY KEY,
    NgayNghi  TIMESTAMP not null,
    TrangThai BOOLEAN
);


-- Thêm dịch vụ Spa cho Chó Mèo
INSERT INTO DichVu (TenDichVu, MoTa, TrangThai, Hien)
VALUES ('Dịch vụ Spa cho Mèo', 'Dịch vụ chăm sóc và làm đẹp toàn diện cho thú cưng', TRUE, TRUE);

-- Lấy ID của dịch vụ vừa thêm
DO
$$
    DECLARE
        dichvu_id  INT;
        tuychon_id INT;
    BEGIN
        SELECT ID INTO dichvu_id FROM DichVu WHERE TenDichVu = 'Dịch vụ Spa cho Mèo';

        -- Thêm các tùy chọn cho dịch vụ
        INSERT INTO TuyChonDichVu (IDDichVu, TenTuyChon, MoTa, TrangThai)
        VALUES (dichvu_id, 'Spa Chọn gói', '<ul>
    <li>
        <strong>Tắm gội:</strong>
        Dịch vụ làm sạch lông với các sản phẩm chuyên dụng, phù hợp với từng loại da và lông của thú cưng.
    </li>
    <li>
        <strong>Cắt tỉa lông:</strong>
        Tạo kiểu lông theo yêu cầu hoặc gọn gàng để thú cưng cảm thấy thoải mái và dễ dàng chăm sóc.
    </li>
    <li>
        <strong>Chăm sóc móng:</strong>
        Cắt tỉa và làm sạch móng để đảm bảo sức khỏe và hạn chế trầy xước khi di chuyển.
    </li>
    <li>
        <strong>Vệ sinh tai:</strong>
        Làm sạch tai để loại bỏ bụi bẩn và ngăn ngừa các bệnh về tai.
    </li>
    <li>
        <strong>Massage thư giãn:</strong>
        Giúp thú cưng giảm căng thẳng, cải thiện tuần hoàn máu và tăng cường sức khỏe.
    </li>
    <li>
        <strong>Kiểm tra sức khỏe cơ bản:</strong>
        Quan sát và báo cáo các dấu hiệu bất thường như da, lông, mắt, mũi.
    </li>
</ul>
', TRUE),
               (dichvu_id, 'Combo Spa Cắt tạo kiểu', '<ul>
    <li>
        <strong>Tư vấn tạo kiểu lông:</strong>
        Nhân viên sẽ tư vấn kiểu dáng phù hợp với giống loài, tính cách và yêu cầu của chủ nuôi.
    </li>
    <li>
        <strong>Cắt và tạo kiểu lông:</strong>
        Sử dụng kỹ thuật chuyên nghiệp để tạo dáng lông theo phong cách yêu cầu hoặc gọn gàng hơn.
    </li>
    <li>
        <strong>Tắm gội làm sạch:</strong>
        Dùng sản phẩm chuyên dụng để làm sạch lông, loại bỏ bụi bẩn và mang lại sự thoải mái.
    </li>
    <li>
        <strong>Chăm sóc móng:</strong>
        Cắt tỉa và vệ sinh móng, đảm bảo an toàn và tránh trầy xước khi di chuyển.
    </li>
    <li>
        <strong>Vệ sinh tai:</strong>
        Làm sạch tai để loại bỏ bụi bẩn và ngăn ngừa các vấn đề sức khỏe.
    </li>
    <li>
        <strong>Massage thư giãn:</strong>
        Giúp thú cưng thư giãn sau khi cắt tạo kiểu, giảm căng thẳng và tăng cường sức khỏe.
    </li>
    <li>
        <strong>Nước hoa cao cấp:</strong>
        Xịt nước hoa phù hợp, lưu lại hương thơm dễ chịu và sang trọng.
    </li>
</ul>
', TRUE),
               (dichvu_id, 'Combo Cạo Lông + Spa', '<ul>
    <li>
        <strong>Cạo lông toàn thân:</strong>
        Làm sạch và loại bỏ lớp lông cũ, giúp thú cưng mát mẻ, thoải mái và dễ chăm sóc hơn.
    </li>
    <li>
        <strong>Tắm gội chuyên sâu:</strong>
        Sử dụng các sản phẩm cao cấp, an toàn và phù hợp với da để làm sạch và khử mùi hiệu quả.
    </li>
    <li>
        <strong>Massage thư giãn:</strong>
        Giúp thú cưng thư giãn cơ bắp, giảm căng thẳng và tăng cường tuần hoàn máu.
    </li>
    <li>
        <strong>Chăm sóc móng:</strong>
        Cắt tỉa và vệ sinh móng, đảm bảo an toàn khi di chuyển và hạn chế tổn thương.
    </li>
    <li>
        <strong>Vệ sinh tai:</strong>
        Làm sạch tai, loại bỏ bụi bẩn và ngăn ngừa các bệnh về tai cho thú cưng.
    </li>
    <li>
        <strong>Nước hoa đặc biệt:</strong>
        Xịt nước hoa dịu nhẹ, lưu giữ hương thơm dễ chịu sau khi hoàn tất dịch vụ.
    </li>
</ul>
', TRUE);

        -- Thêm các tùy chọn cân nặng cho mỗi tùy chọn dịch vụ
        FOR tuychon_id IN (SELECT ID FROM TuyChonDichVu WHERE IDDichVu = dichvu_id)
            LOOP
                INSERT INTO TuyChonCanNang (IDTuyChonDichVu, CanNangMin, CanNangMax, GiaTien, TrangThai)
                VALUES (tuychon_id, 0, 5,
                        CASE
                            WHEN (SELECT TenTuyChon FROM TuyChonDichVu WHERE ID = tuychon_id) = 'Spa Chọn gói'
                                THEN 20
                            WHEN (SELECT TenTuyChon FROM TuyChonDichVu WHERE ID = tuychon_id) = 'Combo Spa Cắt tạo kiểu'
                                THEN 30
                            WHEN (SELECT TenTuyChon FROM TuyChonDichVu WHERE ID = tuychon_id) = 'Combo Cạo Lông + Spa'
                                THEN 35
                            END,
                        TRUE),
                       (tuychon_id, 5, 10,
                        CASE
                            WHEN (SELECT TenTuyChon FROM TuyChonDichVu WHERE ID = tuychon_id) = 'Spa Chọn gói'
                                THEN 25
                            WHEN (SELECT TenTuyChon FROM TuyChonDichVu WHERE ID = tuychon_id) = 'Combo Spa Cắt tạo kiểu'
                                THEN 35
                            WHEN (SELECT TenTuyChon FROM TuyChonDichVu WHERE ID = tuychon_id) = 'Combo Cạo Lông + Spa'
                                THEN 40
                            END,
                        TRUE),
                       (tuychon_id, 10, NULL,
                        CASE
                            WHEN (SELECT TenTuyChon FROM TuyChonDichVu WHERE ID = tuychon_id) = 'Spa Chọn gói'
                                THEN 30
                            WHEN (SELECT TenTuyChon FROM TuyChonDichVu WHERE ID = tuychon_id) = 'Combo Spa Cắt tạo kiểu'
                                THEN 40
                            WHEN (SELECT TenTuyChon FROM TuyChonDichVu WHERE ID = tuychon_id) = 'Combo Cạo Lông + Spa'
                                THEN 45
                            END,
                        TRUE);
            END LOOP;
    END
$$;



-- Thêm dịch vụ Vệ sinh cho Chó Mèo
INSERT INTO DichVu (TenDichVu, MoTa, TrangThai, Hien)
VALUES ('Dịch vụ Vệ sinh cho Chó Mèo', 'Dịch vụ vệ sinh chuyên nghiệp cho thú cưng', TRUE, TRUE);

-- Lấy ID của dịch vụ vừa thêm
DO
$$
    DECLARE
        dichvu_id  INT;
        tuychon_id INT;
    BEGIN
        SELECT ID INTO dichvu_id FROM DichVu WHERE TenDichVu = 'Dịch vụ Vệ sinh cho Chó Mèo';

        -- Thêm các tùy chọn cho dịch vụ
        INSERT INTO TuyChonDichVu (IDDichVu, TenTuyChon, MoTa, TrangThai)
        VALUES (dichvu_id, 'Vệ sinh cơ bản', '<ul>
    <li>
        <strong>Tắm:</strong>
        Làm sạch toàn thân bằng sữa tắm chuyên dụng, phù hợp với từng loại da và lông.
    </li>
    <li>
        <strong>Sấy:</strong>
        Sấy khô lông bằng máy sấy chuyên nghiệp, đảm bảo an toàn và không gây tổn thương da.
    </li>
    <li>
        <strong>Vệ sinh tai:</strong>
        Làm sạch tai, loại bỏ ráy tai và ngăn ngừa các bệnh về tai.
    </li>
    <li>
        <strong>Cắt móng:</strong>
        Cắt tỉa móng gọn gàng, đảm bảo an toàn và tránh trầy xước.
    </li>
</ul>
', TRUE),
               (dichvu_id, 'Vệ sinh nâng cao', '<ul>
    <li>
        <strong>Tắm:</strong>
        Làm sạch sâu với sữa tắm đặc trị, giúp loại bỏ bụi bẩn, vi khuẩn và nấm.
    </li>
    <li>
        <strong>Sấy:</strong>
        Sấy khô và tạo kiểu lông cơ bản bằng máy sấy chuyên nghiệp.
    </li>
    <li>
        <strong>Vệ sinh tai:</strong>
        Làm sạch tai bằng dung dịch vệ sinh chuyên dụng, loại bỏ ráy tai và ngăn ngừa viêm nhiễm.
    </li>
    <li>
        <strong>Cắt móng:</strong>
        Cắt tỉa móng và mài dũa, tạo độ nhẵn mịn và an toàn cho thú cưng.
    </li>
    <li>
        <strong>Nặn tuyến hôi:</strong>
        Loại bỏ tuyến hôi, giúp thú cưng thơm tho và tự tin hơn.
    </li>
</ul>
', TRUE);

        -- Thêm các tùy chọn cân nặng cho mỗi tùy chọn dịch vụ
        FOR tuychon_id IN (SELECT ID FROM TuyChonDichVu WHERE IDDichVu = dichvu_id)
            LOOP
                INSERT INTO TuyChonCanNang (IDTuyChonDichVu, CanNangMin, CanNangMax, GiaTien, TrangThai)
                VALUES (tuychon_id, 0, 5,
                        CASE
                            WHEN (SELECT TenTuyChon FROM TuyChonDichVu WHERE ID = tuychon_id) = 'Vệ sinh cơ bản'
                                THEN 10
                            WHEN (SELECT TenTuyChon FROM TuyChonDichVu WHERE ID = tuychon_id) = 'Vệ sinh nâng cao'
                                THEN 15
                            END,
                        TRUE),
                       (tuychon_id, 5, 10,
                        CASE
                            WHEN (SELECT TenTuyChon FROM TuyChonDichVu WHERE ID = tuychon_id) = 'Vệ sinh cơ bản'
                                THEN 15
                            WHEN (SELECT TenTuyChon FROM TuyChonDichVu WHERE ID = tuychon_id) = 'Vệ sinh nâng cao'
                                THEN 20
                            END,
                        TRUE),
                       (tuychon_id, 10, NULL,
                        CASE
                            WHEN (SELECT TenTuyChon FROM TuyChonDichVu WHERE ID = tuychon_id) = 'Vệ sinh cơ bản'
                                THEN 20
                            WHEN (SELECT TenTuyChon FROM TuyChonDichVu WHERE ID = tuychon_id) = 'Vệ sinh nâng cao'
                                THEN 25
                            END,
                        TRUE);
            END LOOP;
    END
$$;


-- Thêm dịch vụ Khám bệnh cho Chó Mèo
INSERT INTO DichVu (TenDichVu, MoTa, TrangThai, Hien)
VALUES ('Dịch vụ Khám bệnh cho Chó Mèo', 'Dịch vụ khám và điều trị bệnh cho thú cưng', TRUE, TRUE);

-- Lấy ID của dịch vụ vừa thêm
DO
$$
    DECLARE
        dichvu_id  INT;
        tuychon_id INT;
    BEGIN
        SELECT ID INTO dichvu_id FROM DichVu WHERE TenDichVu = 'Dịch vụ Khám bệnh cho Chó Mèo';

        -- Thêm các tùy chọn cho dịch vụ
        INSERT INTO TuyChonDichVu (IDDichVu, TenTuyChon, MoTa, TrangThai)
        VALUES (dichvu_id, 'Khám tổng quát', '<ul>
    <li>
        <strong>Kiểm tra sức khỏe tổng thể:</strong>
        Đánh giá tình trạng sức khỏe chung, bao gồm kiểm tra thân nhiệt, nhịp tim, hô hấp.
    </li>
    <li>
        <strong>Khám lâm sàng:</strong>
        Quan sát các triệu chứng lâm sàng, phát hiện các bất thường về da, lông, mắt, mũi, miệng.
    </li>
    <li>
        <strong>Tư vấn sức khỏe:</strong>
        Cung cấp lời khuyên về chế độ dinh dưỡng, chăm sóc và phòng ngừa bệnh cho thú cưng.
    </li>
</ul>
', TRUE),
               (dichvu_id, 'Khám chuyên khoa', '<ul>
    <li>
        <strong>Khám chuyên sâu:</strong>
        Tập trung vào các vấn đề sức khỏe cụ thể, như da liễu, hô hấp, tiêu hóa, tim mạch.
    </li>
    <li>
        <strong>Chẩn đoán hình ảnh:</strong>
        Sử dụng các phương tiện chẩn đoán như siêu âm, X-quang để xác định bệnh.
    </li>
    <li>
        <strong>Xét nghiệm:</strong>
        Tiến hành các xét nghiệm máu, nước tiểu để phân tích và chẩn đoán bệnh.
    </li>
    <li>
        <strong>Điều trị:</strong>
        Kê đơn thuốc và đưa ra phác đồ điều trị phù hợp với tình trạng bệnh của thú cưng.
    </li>
</ul>
', TRUE);

        -- Thêm các tùy chọn cân nặng cho mỗi tùy chọn dịch vụ
        FOR tuychon_id IN (SELECT ID FROM TuyChonDichVu WHERE IDDichVu = dichvu_id)
            LOOP
                INSERT INTO TuyChonCanNang (IDTuyChonDichVu, CanNangMin, CanNangMax, GiaTien, TrangThai)
                VALUES (tuychon_id, 1, NULL,
                        CASE
                            WHEN (SELECT TenTuyChon FROM TuyChonDichVu WHERE ID = tuychon_id) = 'Khám tổng quát'
                                THEN 50
                            WHEN (SELECT TenTuyChon FROM TuyChonDichVu WHERE ID = tuychon_id) = 'Khám chuyên khoa'
                                THEN 100
                            END,
                        TRUE);
            END LOOP;
    END
$$;

-- -- Thêm dữ liệu vào bảng GiamGia
-- INSERT INTO GiamGia (PhanTramGiam, NgayBatDau, NgayKetThuc, MoTa, TrangThai)
-- VALUES
--        (20, '2024-11-01 00:00:00', '2024-11-30 23:59:59', 'Giảm giá tháng 11', TRUE),
--        (15, '2024-12-01 00:00:00', '2024-12-31 23:59:59', 'Giảm giá tháng 12', TRUE);


-- -- Thêm dữ liệu vào bảng ThuCung
-- INSERT INTO ThuCung (Ten, CanNang, Tuoi, Giong, IDTaiKhoan, Image)
-- VALUES ('Dog 1', 10.5, 2, 'Golden', 1, NULL),
--        ('Dog 2', 8.3, 3, 'Bulldog', 2, NULL),
--        ('Dog 3', 12.7, 4, 'Labrador', 3, NULL);

INSERT INTO CaLichHen (ThoiGianCa, TrangThai)
VALUES ('07:00:00', TRUE),
       ('08:00:00', TRUE),
       ('09:00:00', TRUE),
       ('10:00:00', TRUE),
       ('11:00:00', TRUE),
       ('12:00:00', TRUE),
       ('13:00:00', TRUE),
       ('14:00:00', TRUE);

-- -- Thêm dữ liệu vào bảng LichHen
-- INSERT INTO LichHen (idKhachHang, idThuCung, idDichVu, Date, TrangThai , idCaLichHen,TrangThaiCa)
-- VALUES (1, 1, 1, '2024-10-02 ', 1 , 1,TRUE),
--        (2, 2, 2, '2024-10-03 ', 2, 2,TRUE),
--        (3, 3, 3, '2024-10-04 ', 3 , 3,TRUE);

-- -- Thêm dữ liệu vào bảng HoaDon
-- INSERT INTO HoaDon (idLichHen, Date,SoTienBanDau, SoTien, NgayThanhToan, PhuongThucThanhToan, TrangThai,MaGiaoDich,idGiamGia)
-- VALUES (1, '2024-10-02 10:00:00',20, 20, '2024-10-02 11:00:00', 'Offline', 1,'1',2),
--        (2, '2024-10-03 14:00:00',20, 20, '2024-10-03 15:00:00', 'Online', 2,'2',2),
--        (3, '2024-10-04 09:00:00',20, 20, '2024-10-04 10:00:00', 'Online', 3,'3',2);

-- -- Thêm dữ liệu vào bảng DanhGia
-- INSERT INTO DanhGia (idTaiKhoan, SoSao, MoTa, Date, TrangThai,idHoaDon)
-- VALUES (1, 5, 'Rất tốt', '2024-10-01 12:00:00', TRUE,'1'),
--        (2, 4, 'Tốt', '2024-09-30 11:00:00', TRUE,'1'),
--        (3, 3, 'Bình thường', '2024-09-29 10:00:00', FALSE,'1');

