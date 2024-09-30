create table dich_vu(
    id_dichvu bigserial  primary key,
    ten varchar(100) not null ,
    mo_ta text not null ,
    anh text,
    gia_tien float not null
);