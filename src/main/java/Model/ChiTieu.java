package Model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ChiTieu {
    private Integer maChi;
    private LocalDate ngayChi;
    private String mucDich;
    private BigDecimal soTien;
    private Integer thang;
    private Integer nam;
    private String loaiChi;
    private LocalDate ngayTao;

    // Constructor mặc định
    public ChiTieu() {
        this.ngayChi = LocalDate.now();
        this.ngayTao = LocalDate.now();
    }

    // Constructor cho thêm mới (không có mã)
    public ChiTieu(Integer thang, Integer nam, String mucDich, BigDecimal soTien, String loaiChi) {
        this();
        this.thang = thang;
        this.nam = nam;
        this.mucDich = mucDich;
        this.soTien = soTien;
        this.loaiChi = loaiChi;
    }

    // Constructor cho thêm mới với ngày chi cụ thể
    public ChiTieu(Integer thang, Integer nam, LocalDate ngayChi, String mucDich, BigDecimal soTien, String loaiChi) {
        this.thang = thang;
        this.nam = nam;
        this.ngayChi = ngayChi;
        this.mucDich = mucDich;
        this.soTien = soTien;
        this.loaiChi = loaiChi;
        this.ngayTao = LocalDate.now();
    }

    // Constructor đầy đủ cho cập nhật
    public ChiTieu(Integer maChi, Integer thang, Integer nam, LocalDate ngayChi, 
                  String mucDich, BigDecimal soTien, String loaiChi, LocalDate ngayTao) {
        this.maChi = maChi;
        this.thang = thang;
        this.nam = nam;
        this.ngayChi = ngayChi;
        this.mucDich = mucDich;
        this.soTien = soTien;
        this.loaiChi = loaiChi;
        this.ngayTao = ngayTao;
    }

    // Constructor cho cập nhật (giữ nguyên ngày tạo)
    public ChiTieu(Integer maChi, Integer thang, Integer nam, String mucDich, 
                  BigDecimal soTien, String loaiChi, LocalDate ngayTao) {
        this.maChi = maChi;
        this.thang = thang;
        this.nam = nam;
        this.mucDich = mucDich;
        this.soTien = soTien;
        this.loaiChi = loaiChi;
        this.ngayChi = LocalDate.now();
        this.ngayTao = ngayTao;
    }

    // Kiểm tra tính hợp lệ của đối tượng
    public boolean isValid() {
        return thang != null && thang >= 1 && thang <= 12
                && nam != null && nam >= 2020 && nam <= LocalDate.now().getYear() + 1
                && mucDich != null && !mucDich.trim().isEmpty()
                && soTien != null && soTien.compareTo(BigDecimal.ZERO) > 0
                && loaiChi != null && !loaiChi.trim().isEmpty()
                && ngayChi != null;
    }

    // Kiểm tra xem có phải chi phí nguyên liệu không
    public boolean isNguyenLieu() {
        return "Nguyên liệu".equals(loaiChi);
    }

    // Format thông tin chi tiêu
    public String getThongTin() {
        return String.format("Chi %s: %s - %s VND", 
                loaiChi, mucDich, String.format("%,.0f", soTien));
    }

    // Getter và Setter
    public Integer getMaChi() {
        return maChi;
    }

    public void setMaChi(Integer maChi) {
        this.maChi = maChi;
    }

    public LocalDate getNgayChi() {
        return ngayChi;
    }

    public void setNgayChi(LocalDate ngayChi) {
        this.ngayChi = ngayChi;
    }

    public String getMucDich() {
        return mucDich;
    }

    public void setMucDich(String mucDich) {
        this.mucDich = mucDich;
    }

    public BigDecimal getSoTien() {
        return soTien;
    }

    public void setSoTien(BigDecimal soTien) {
        this.soTien = soTien;
    }

    public Integer getThang() {
        return thang;
    }

    public void setThang(Integer thang) {
        this.thang = thang;
    }

    public Integer getNam() {
        return nam;
    }

    public void setNam(Integer nam) {
        this.nam = nam;
    }

    public String getLoaiChi() {
        return loaiChi;
    }

    public void setLoaiChi(String loaiChi) {
        this.loaiChi = loaiChi;
    }

    public LocalDate getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDate ngayTao) {
        this.ngayTao = ngayTao;
    }

    @Override
    public String toString() {
        return "ChiTieu{" +
                "maChi=" + maChi +
                ", thang=" + thang +
                ", nam=" + nam +
                ", ngayChi=" + ngayChi +
                ", mucDich='" + mucDich + '\'' +
                ", soTien=" + soTien +
                ", loaiChi='" + loaiChi + '\'' +
                ", ngayTao=" + ngayTao +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTieu chiTieu = (ChiTieu) o;
        return maChi != null ? maChi.equals(chiTieu.maChi) : chiTieu.maChi == null;
    }

    @Override
    public int hashCode() {
        return maChi != null ? maChi.hashCode() : 0;
    }
}