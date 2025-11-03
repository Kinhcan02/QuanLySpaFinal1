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
    private Integer maNhanVien;
    private Integer soLuong;
    private BigDecimal donGiaDichVu;
    private BigDecimal tienLuong;
    private BigDecimal donGiaBan;
    private BigDecimal tienTip;
    private BigDecimal thanhTien;
    
    // Reference objects (optional - for JOIN operations)
    private HoaDon hoaDon;
    private DichVu dichVu;
    private NhanVien nhanVien;

    // Constructor mặc định
    public ChiTietHoaDon() {
        this.soLuong = 1; // Default value
        this.tienTip = BigDecimal.ZERO;
    }

    // Constructor với tất cả tham số
    public ChiTietHoaDon(Integer maCTHD, Integer maHoaDon, Integer maDichVu, Integer maNhanVien,
                        Integer soLuong, BigDecimal donGiaDichVu, BigDecimal tienLuong,
                        BigDecimal donGiaBan, BigDecimal tienTip) {
        this.maCTHD = maCTHD;
        this.maHoaDon = maHoaDon;
        this.maDichVu = maDichVu;
        this.maNhanVien = maNhanVien;
        this.soLuong = soLuong != null ? soLuong : 1;
        this.donGiaDichVu = donGiaDichVu;
        this.tienLuong = tienLuong != null ? tienLuong : BigDecimal.ZERO;
        this.donGiaBan = donGiaBan;
        this.tienTip = tienTip != null ? tienTip : BigDecimal.ZERO;
        this.thanhTien = calculateThanhTien();
    }

    // Constructor không có ID (dùng khi insert)
    public ChiTietHoaDon(Integer maHoaDon, Integer maDichVu, Integer maNhanVien, 
                        Integer soLuong, BigDecimal donGiaDichVu) {
        this.maHoaDon = maHoaDon;
        this.maDichVu = maDichVu;
        this.maNhanVien = maNhanVien;
        this.soLuong = soLuong != null ? soLuong : 1;
        this.donGiaDichVu = donGiaDichVu;
        this.tienLuong = BigDecimal.ZERO;
        this.donGiaBan = BigDecimal.ZERO;
        this.tienTip = BigDecimal.ZERO;
        this.thanhTien = calculateThanhTien();
    }

    // Method tính thành tiền
    private BigDecimal calculateThanhTien() {
        if (soLuong != null && donGiaBan != null) {
            return donGiaBan.multiply(BigDecimal.valueOf(soLuong));
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

    public Integer getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(Integer maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public Integer getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong != null ? soLuong : 1;
    }

    public BigDecimal getDonGiaDichVu() {
        return donGiaDichVu;
    }

    public void setDonGiaDichVu(BigDecimal donGiaDichVu) {
        this.donGiaDichVu = donGiaDichVu;
    }

    public BigDecimal getTienLuong() {
        return tienLuong != null ? tienLuong : BigDecimal.ZERO;
    }

    public void setTienLuong(BigDecimal tienLuong) {
        this.tienLuong = tienLuong;
    }

    public BigDecimal getDonGiaBan() {
        return donGiaBan;
    }

    public void setDonGiaBan(BigDecimal donGiaBan) {
        this.donGiaBan = donGiaBan;
    }

    public BigDecimal getTienTip() {
        return tienTip != null ? tienTip : BigDecimal.ZERO;
    }

    public void setTienTip(BigDecimal tienTip) {
        this.tienTip = tienTip != null ? tienTip : BigDecimal.ZERO;
    }

    public BigDecimal getThanhTien() {
        return thanhTien != null ? thanhTien : calculateThanhTien();
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

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    // Validation method
    public boolean isValid() {
        return maHoaDon != null && 
               maDichVu != null && 
               soLuong != null && soLuong > 0 &&
               donGiaDichVu != null && donGiaDichVu.compareTo(BigDecimal.ZERO) >= 0;
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
               Objects.equals(maNhanVien, that.maNhanVien) &&
               Objects.equals(soLuong, that.soLuong) &&
               Objects.equals(donGiaDichVu, that.donGiaDichVu) &&
               Objects.equals(tienLuong, that.tienLuong) &&
               Objects.equals(donGiaBan, that.donGiaBan) &&
               Objects.equals(tienTip, that.tienTip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maCTHD, maHoaDon, maDichVu, maNhanVien, soLuong, 
                           donGiaDichVu, tienLuong, donGiaBan, tienTip);
    }

    // toString
    @Override
    public String toString() {
        return "ChiTietHoaDon{" +
                "maCTHD=" + maCTHD +
                ", maHoaDon=" + maHoaDon +
                ", maDichVu=" + maDichVu +
                ", maNhanVien=" + maNhanVien +
                ", soLuong=" + soLuong +
                ", donGiaDichVu=" + donGiaDichVu +
                ", tienLuong=" + tienLuong +
                ", donGiaBan=" + donGiaBan +
                ", tienTip=" + tienTip +
                ", thanhTien=" + thanhTien +
                '}';
    }
}