package Model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Model class cho bảng ChiTietHoaDon
 */
public class ChiTietHoaDon {
    private Integer maCTHD;
    private Integer maHoaDon;
    private Integer maDichVu;
    private Integer soLuong;
    private BigDecimal donGia;
    private BigDecimal thanhTien;
    
    // Reference objects (optional - for JOIN operations)
    private HoaDon hoaDon;
    private DichVu dichVu;

    // Constructor mặc định
    public ChiTietHoaDon() {
        this.soLuong = 1; // Default value
    }

    // Constructor với tất cả tham số
    public ChiTietHoaDon(Integer maCTHD, Integer maHoaDon, Integer maDichVu, 
                        Integer soLuong, BigDecimal donGia) {
        this.maCTHD = maCTHD;
        this.maHoaDon = maHoaDon;
        this.maDichVu = maDichVu;
        this.soLuong = soLuong != null ? soLuong : 1;
        this.donGia = donGia;
        this.thanhTien = calculateThanhTien();
    }

    // Constructor không có ID (dùng khi insert)
    public ChiTietHoaDon(Integer maHoaDon, Integer maDichVu, Integer soLuong, BigDecimal donGia) {
        this.maHoaDon = maHoaDon;
        this.maDichVu = maDichVu;
        this.soLuong = soLuong != null ? soLuong : 1;
        this.donGia = donGia;
        this.thanhTien = calculateThanhTien();
    }

    // Constructor đơn giản
    public ChiTietHoaDon(Integer maHoaDon, Integer maDichVu, BigDecimal donGia) {
        this.maHoaDon = maHoaDon;
        this.maDichVu = maDichVu;
        this.donGia = donGia;
        this.soLuong = 1;
        this.thanhTien = calculateThanhTien();
    }

    // Method tính thành tiền
    private BigDecimal calculateThanhTien() {
        if (soLuong != null && donGia != null) {
            return donGia.multiply(BigDecimal.valueOf(soLuong));
        }
        return BigDecimal.ZERO;
    }

    // Getters and Setters
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
        this.thanhTien = calculateThanhTien(); // Recalculate when quantity changes
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
        this.thanhTien = calculateThanhTien(); // Recalculate when price changes
    }

    public BigDecimal getThanhTien() {
        return thanhTien != null ? thanhTien : calculateThanhTien();
    }

    // Setter for thanhTien - chỉ để đọc từ database, không set thủ công
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

    // Validation method
    public boolean isValid() {
        return maHoaDon != null && 
               maDichVu != null && 
               soLuong != null && soLuong > 0 &&
               donGia != null && donGia.compareTo(BigDecimal.ZERO) >= 0;
    }

    // Helper method để cập nhật thành tiền
    public void recalculateThanhTien() {
        this.thanhTien = calculateThanhTien();
    }

    // equals và hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietHoaDon that = (ChiTietHoaDon) o;
        return Objects.equals(maCTHD, that.maCTHD) &&
               Objects.equals(maHoaDon, that.maHoaDon) &&
               Objects.equals(maDichVu, that.maDichVu) &&
               Objects.equals(soLuong, that.soLuong) &&
               Objects.equals(donGia, that.donGia) &&
               Objects.equals(thanhTien, that.thanhTien);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maCTHD, maHoaDon, maDichVu, soLuong, donGia, thanhTien);
    }

    // toString
    @Override
    public String toString() {
        return "ChiTietHoaDon{" +
                "maCTHD=" + maCTHD +
                ", maHoaDon=" + maHoaDon +
                ", maDichVu=" + maDichVu +
                ", soLuong=" + soLuong +
                ", donGia=" + donGia +
                ", thanhTien=" + thanhTien +
                '}';
    }
}