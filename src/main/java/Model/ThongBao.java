package Model;

import java.time.LocalDateTime;

public class ThongBao {
    private int maThongBao;
    private String loaiThongBao; // SINH_NHAT, DAT_LICH, HE_THONG
    private String tieuDe;
    private String noiDung;
    private LocalDateTime thoiGian;
    private String trangThai; // MOI, DA_DOC
    private Integer maLich;
    private Integer maNhanVien; // THÊM TRƯỜNG NÀY
    private Integer maNguyenLieu; // THÊM TRƯỜNG NÀY

    // Constructors
    public ThongBao() {}

    public ThongBao(String loaiThongBao, String tieuDe, String noiDung, LocalDateTime thoiGian) {
        this.loaiThongBao = loaiThongBao;
        this.tieuDe = tieuDe;
        this.noiDung = noiDung;
        this.thoiGian = thoiGian;
        this.trangThai = "MOI";
    }

    // Getters and Setters
    public int getMaThongBao() { return maThongBao; }
    public void setMaThongBao(int maThongBao) { this.maThongBao = maThongBao; }

    public String getLoaiThongBao() { return loaiThongBao; }
    public void setLoaiThongBao(String loaiThongBao) { this.loaiThongBao = loaiThongBao; }

    public String getTieuDe() { return tieuDe; }
    public void setTieuDe(String tieuDe) { this.tieuDe = tieuDe; }

    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }

    public LocalDateTime getThoiGian() { return thoiGian; }
    public void setThoiGian(LocalDateTime thoiGian) { this.thoiGian = thoiGian; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public Integer getMaLich() { return maLich; }
    public void setMaLich(Integer maLich) { this.maLich = maLich; }

    // THÊM GETTER VÀ SETTER CHO MA_NHAN_VIEN
    public Integer getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(Integer maNhanVien) { this.maNhanVien = maNhanVien; }

    // THÊM GETTER VÀ SETTER CHO MA_NGUYEN_LIEU
    public Integer getMaNguyenLieu() { return maNguyenLieu; }
    public void setMaNguyenLieu(Integer maNguyenLieu) { this.maNguyenLieu = maNguyenLieu; }
}