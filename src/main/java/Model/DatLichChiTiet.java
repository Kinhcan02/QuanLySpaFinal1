package Model;

import java.time.LocalDateTime;

public class DatLichChiTiet {

    private Integer maCTDL;
    private Integer maLich;
    private Integer maDichVu;
    private Integer maNhanVien;
    private Integer soLuongNguoi;
    private String ghiChu;
    private LocalDateTime ngayTao;

    // Reference objects
    private DichVu dichVu;
    private NhanVien nhanVien;

    public DatLichChiTiet() {
        this.soLuongNguoi = 1;
    }

    public DatLichChiTiet(Integer maCTDL, Integer maLich, Integer maDichVu, Integer maNhanVien,
            Integer soLuongNguoi, String ghiChu, LocalDateTime ngayTao) {
        this.maCTDL = maCTDL;
        this.maLich = maLich;
        this.maDichVu = maDichVu;
        this.maNhanVien = maNhanVien;
        this.soLuongNguoi = soLuongNguoi != null ? soLuongNguoi : 1;
        this.ghiChu = ghiChu;
        this.ngayTao = ngayTao;
    }

    // Getter và Setter
    public Integer getMaCTDL() {
        return maCTDL;
    }

    public void setMaCTDL(Integer maCTDL) {
        this.maCTDL = maCTDL;
    }

    public Integer getMaLich() {
        return maLich;
    }

    public void setMaLich(Integer maLich) {
        this.maLich = maLich;
    }

    public Integer getMaDichVu() {
        return maDichVu;
    }

    public void setMaDichVu(Integer maDichVu) {
        this.maDichVu = maDichVu;
    }

    public Integer getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(Integer maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public Integer getSoLuongNguoi() {
        return soLuongNguoi;
    }

    public void setSoLuongNguoi(Integer soLuongNguoi) {
        this.soLuongNguoi = soLuongNguoi != null ? soLuongNguoi : 1;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    public DichVu getDichVu() {
        return dichVu;
    }

    public void setDichVu(DichVu dichVu) {
        this.dichVu = dichVu;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }
// Thêm vào class DatLichChiTiet

    public String getTenNhanVien() {
        return nhanVien != null ? nhanVien.getHoTen() : "Chưa phân công";
    }

    public void setNhanVienThucHien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
        if (nhanVien != null) {
            this.maNhanVien = nhanVien.getMaNhanVien();
        }
    }

    // Validation method
    public boolean isValid() {
        return maLich != null && maDichVu != null;
    }

    @Override
    public String toString() {
        return "DatLichChiTiet{"
                + "maCTDL=" + maCTDL
                + ", maLich=" + maLich
                + ", maDichVu=" + maDichVu
                + ", maNhanVien=" + maNhanVien
                + ", soLuongNguoi=" + soLuongNguoi
                + ", ghiChu='" + ghiChu + '\''
                + ", ngayTao=" + ngayTao
                + '}';
    }
}
