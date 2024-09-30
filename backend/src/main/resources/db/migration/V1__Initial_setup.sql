CREATE TABLE DichVu(
    id_DichVu bigserial  PRIMARY KEY,
    ten VARCHAR(100) NOT NULL,
    moTa TEXT NOT NULL,
    anh TEXT,
    giaTien INT NOT NULL
);