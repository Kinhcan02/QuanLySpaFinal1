package Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Model class cho bảng SuDungDichVu
 */
public class SuDungDichVu {
    private Integer maSuDung;
    private Integer maKhachHang;
    private Integer maDichVu;
    private Integer maNhanVien;
    private LocalDateTime ngaySuDung;
    private BigDecimal soTien;
    private BigDecimal tienTip;
    
    // Reference objects (optional - for JOIN operations)
    private KhachHang khachHang;
    private DichVu dichVu;
    private NhanVien nhanVien;

    // Constructor mặc định
    public SuDungDichVu() {
        this.ngaySuDung = LocalDateTime.now(); // Default value
    }

    // Constructor với tất cả tham số
    public SuDungDichVu(Integer maSuDung, Integer maKhachHang, Integer maDichVu, 
                       Integer maNhanVien, LocalDateTime ngaySuDung, 
                       BigDecimal soTien, BigDecimal tienTip) {
        this.maSuDung = maSuDung;
        this.maKhachHang = maKhachHang;
        this.maDichVu = maDichVu;
        this.maNhanVien = maNhanVien;
        this.ngaySuDung = ngaySuDung != null ? ngaySuDung : LocalDateTime.now();
        this.soTien = soTien;
        this.tienTip = tienTip;
    }

    // Constructor không có ID (dùng khi insert)
    public SuDungDichVu(Integer maKhachHang, Integer maDichVu, Integer maNhanVien, 
                       LocalDateTime ngaySuDung, BigDecimal soTien, BigDecimal tienTip) {
        this.maKhachHang = maKhachHang;
        this.maDichVu = maDichVu;
        this.maNhanVien = maNhanVien;
        this.ngaySuDung = ngaySuDung != null ? ngaySuDung : LocalDateTime.now();
        this.soTien = soTien;
        this.tienTip = tienTip;
    }

    // Constructor đơn giản - chỉ bắt buộc các field NOT NULL
    public SuDungDichVu(Integer maDichVu, LocalDateTime ngaySuDung) {
        this.maDichVu = maDichVu;
        this.ngaySuDung = ngaySuDung != null ? ngaySuDung : LocalDateTime.now();
    }

    // Constructor với thông tin cơ bản
    public SuDungDichVu(Integer maKhachHang, Integer maDichVu, Integer maNhanVien, BigDecimal soTien) {
        this.maKhachHang = maKhachHang;
        this.maDichVu = maDichVu;
        this.maNhanVien = maNhanVien;
        this.soTien = soTien;
        this.ngaySuDung = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getMaSuDung() {
        return maSuDung;
    }

    public void setMaSuDung(Integer maSuDung) {
        this.maSuDung = maSuDung;
    }

    public Integer getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(Integer maKhachHang) {
        this.maKhachHang = maKhachHang;
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

    public LocalDateTime getNgaySuDung() {
        return ngaySuDung;
    }

    public void setNgaySuDung(LocalDateTime ngaySuDung) {
        this.ngaySuDung = ngaySuDung != null ? ngaySuDung : LocalDateTime.now();
    }

    public BigDecimal getSoTien() {
        return soTien;
    }

    public void setSoTien(BigDecimal soTien) {
        this.soTien = soTien;
    }

    public BigDecimal getTienTip() {
        return tienTip;
    }

    public void setTienTip(BigDecimal tienTip) {
        this.tienTip = tienTip;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
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
        return maDichVu != null && 
               ngaySuDung != null &&
               (soTien == null || soTien.compareTo(BigDecimal.ZERO) >= 0) &&
               (tienTip == null || tienTip.compareTo(BigDecimal.ZERO) >= 0);
    }

    // Tính tổng tiền (số tiền + tip)
    public BigDecimal getTongTien() {
        BigDecimal total = BigDecimal.ZERO;
        if (soTien != null) {
            total = total.add(soTien);
        }
        if (tienTip != null) {
            total = total.add(tienTip);
        }
        return total;
    }

    // Helper method để format ngày
    public String getNgaySuDungFormatted() {
        return ngaySuDung != null ? 
            ngaySuDung.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";
    }

    // Helper method để lấy chỉ ngày (không có giờ)
    public LocalDateTime getNgaySuDungDateOnly() {
        return ngaySuDung != null ? ngaySuDung.toLocalDate().atStartOfDay() : null;
    }

    // equals và hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SuDungDichVu that = (SuDungDichVu) o;
        return Objects.equals(maSuDung, that.maSuDung) &&
               Objects.equals(maKhachHang, that.maKhachHang) &&
               Objects.equals(maDichVu, that.maDichVu) &&
               Objects.equals(maNhanVien, that.maNhanVien) &&
               Objects.equals(ngaySuDung, that.ngaySuDung) &&
               Objects.equals(soTien, that.soTien) &&
               Objects.equals(tienTip, that.tienTip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maSuDung, maKhachHang, maDichVu, maNhanVien, ngaySuDung, soTien, tienTip);
    }

    // toString
    @Override
    public String toString() {
        return "SuDungDichVu{" +
                "maSuDung=" + maSuDung +
                ", maKhachHang=" + maKhachHang +
                ", maDichVu=" + maDichVu +
                ", maNhanVien=" + maNhanVien +
                ", ngaySuDung=" + ngaySuDung +
                ", soTien=" + soTien +
                ", tienTip=" + tienTip +
                '}';
    }
}