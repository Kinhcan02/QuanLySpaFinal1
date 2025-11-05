package Model;

import java.math.BigDecimal;
import java.util.Objects;

public class ChiTietHoaDon {
    private Integer maCTHD;
    private Integer maHoaDon;
    private Integer maDichVu;
    private Integer maNhanVien; // THÊM TRƯỜNG NÀY
    private Integer soLuong;
    private BigDecimal donGia;
    private BigDecimal thanhTien; // GIỮ LẠI VÌ CÓ TRONG DATABASE (PERSISTED)
    
    // Reference objects
    private HoaDon hoaDon;
    private DichVu dichVu;
    private NhanVien nhanVien; // THÊM REFERENCE

    // Constructor mặc định
    public ChiTietHoaDon() {
        this.soLuong = 1;
    }

    // Constructor với tham số - THÊM maNhanVien
    public ChiTietHoaDon(Integer maCTHD, Integer maHoaDon, Integer maDichVu, 
                        Integer maNhanVien, Integer soLuong, BigDecimal donGia, 
                        BigDecimal thanhTien) {
        this.maCTHD = maCTHD;
        this.maHoaDon = maHoaDon;
        this.maDichVu = maDichVu;
        this.maNhanVien = maNhanVien;
        this.soLuong = soLuong != null ? soLuong : 1;
        this.donGia = donGia;
        this.thanhTien = thanhTien;
    }

    // GETTER VÀ SETTER MỚI CHO maNhanVien
    public Integer getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(Integer maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    // Giữ nguyên các getter/setter khác...
    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }

    public Integer getMaCTHD() {
        return maCTHD;
    }

    public void setMaCTHD(Integer maCTHD) {
        this.maCTHD = maCTHD;
    }

    public Integer getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(Integer maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public Integer getMaDichVu() {
        return maDichVu;
    }

    public void setMaDichVu(Integer maDichVu) {
        this.maDichVu = maDichVu;
    }

    public Integer getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong != null ? soLuong : 1;
    }

    public BigDecimal getThanhTien() {
        return thanhTien != null ? thanhTien : BigDecimal.ZERO;
    }

    public void setThanhTien(BigDecimal thanhTien) {
        this.thanhTien = thanhTien;
    }

    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }

    public DichVu getDichVu() {
        return dichVu;
    }

    public void setDichVu(DichVu dichVu) {
        this.dichVu = dichVu;
    }

    // Phương thức tính lại thành tiền
    public void recalculateThanhTien() {
        if (soLuong != null && donGia != null) {
            this.thanhTien = donGia.multiply(BigDecimal.valueOf(soLuong));
        } else {
            this.thanhTien = BigDecimal.ZERO;
        }
    }

    // Validation method - CẬP NHẬT
    public boolean isValid() {
        return maHoaDon != null && 
               maDichVu != null && 
               soLuong != null && soLuong > 0 &&
               donGia != null && donGia.compareTo(BigDecimal.ZERO) >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietHoaDon that = (ChiTietHoaDon) o;
        return Objects.equals(maCTHD, that.maCTHD) &&
               Objects.equals(maHoaDon, that.maHoaDon) &&
               Objects.equals(maDichVu, that.maDichVu) &&
               Objects.equals(maNhanVien, that.maNhanVien) &&
               Objects.equals(soLuong, that.soLuong) &&
               Objects.equals(donGia, that.donGia);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maCTHD, maHoaDon, maDichVu, maNhanVien, soLuong, donGia);
    }

    @Override
    public String toString() {
        return "ChiTietHoaDon{" +
                "maCTHD=" + maCTHD +
                ", maHoaDon=" + maHoaDon +
                ", maDichVu=" + maDichVu +
                ", maNhanVien=" + maNhanVien +
                ", soLuong=" + soLuong +
                ", donGia=" + donGia +
                ", thanhTien=" + thanhTien +
                '}';
    }
}