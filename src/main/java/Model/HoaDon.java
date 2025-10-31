package Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class cho bảng HoaDon
 */
public class HoaDon {

    private List<ChiTietHoaDon> chiTietHoaDon;
    private Integer maHoaDon;
    private Integer maKhachHang;
    private LocalDateTime ngayLap;
    private BigDecimal tongTien;
    private Integer maNhanVienLap;
    private String ghiChu;

    // Reference objects (optional - for JOIN operations)
    private KhachHang khachHang;
    private NhanVien nhanVienLap;

    // Constructor mặc định
    public HoaDon() {
        this.ngayLap = LocalDateTime.now(); // Default value
    }

    // Constructor với tất cả tham số
    public HoaDon(Integer maHoaDon, Integer maKhachHang, LocalDateTime ngayLap,
            BigDecimal tongTien, Integer maNhanVienLap, String ghiChu) {
        this.maHoaDon = maHoaDon;
        this.maKhachHang = maKhachHang;
        this.ngayLap = ngayLap != null ? ngayLap : LocalDateTime.now();
        this.tongTien = tongTien;
        this.maNhanVienLap = maNhanVienLap;
        this.ghiChu = ghiChu;
    }

    // Constructor không có ID (dùng khi insert)
    public HoaDon(Integer maKhachHang, LocalDateTime ngayLap, BigDecimal tongTien,
            Integer maNhanVienLap, String ghiChu) {
        this.maKhachHang = maKhachHang;
        this.ngayLap = ngayLap != null ? ngayLap : LocalDateTime.now();
        this.tongTien = tongTien;
        this.maNhanVienLap = maNhanVienLap;
        this.ghiChu = ghiChu;
    }

    // Constructor đơn giản
    public HoaDon(Integer maKhachHang, BigDecimal tongTien, Integer maNhanVienLap) {
        this.maKhachHang = maKhachHang;
        this.ngayLap = LocalDateTime.now();
        this.tongTien = tongTien;
        this.maNhanVienLap = maNhanVienLap;
    }

    public List<ChiTietHoaDon> getChiTietHoaDon() {
        if (chiTietHoaDon == null) {
            chiTietHoaDon = new ArrayList<>();
        }
        return chiTietHoaDon;
    }

    public void setChiTietHoaDon(List<ChiTietHoaDon> chiTietHoaDon) {
        this.chiTietHoaDon = chiTietHoaDon;
    }

// Thêm phương thức kiểm tra có chi tiết không
    public boolean hasChiTiet() {
        return chiTietHoaDon != null && !chiTietHoaDon.isEmpty();
    }

// Thêm phương thức thêm chi tiết
    public void addChiTiet(ChiTietHoaDon chiTiet) {
        if (chiTietHoaDon == null) {
            chiTietHoaDon = new ArrayList<>();
        }
        chiTietHoaDon.add(chiTiet);
    }

    // Getters and Setters
    public Integer getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(Integer maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public Integer getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(Integer maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public LocalDateTime getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDateTime ngayLap) {
        this.ngayLap = ngayLap != null ? ngayLap : LocalDateTime.now();
    }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    public Integer getMaNhanVienLap() {
        return maNhanVienLap;
    }

    public void setMaNhanVienLap(Integer maNhanVienLap) {
        this.maNhanVienLap = maNhanVienLap;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public NhanVien getNhanVienLap() {
        return nhanVienLap;
    }

    public void setNhanVienLap(NhanVien nhanVienLap) {
        this.nhanVienLap = nhanVienLap;
    }

    // Validation method
    public boolean isValid() {
        return ngayLap != null
                && tongTien != null && tongTien.compareTo(BigDecimal.ZERO) >= 0;
    }

    // Helper method để format ngày
    public String getNgayLapFormatted() {
        return ngayLap != null
                ? ngayLap.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";
    }

    // equals và hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HoaDon hoaDon = (HoaDon) o;
        return Objects.equals(maHoaDon, hoaDon.maHoaDon)
                && Objects.equals(maKhachHang, hoaDon.maKhachHang)
                && Objects.equals(ngayLap, hoaDon.ngayLap)
                && Objects.equals(tongTien, hoaDon.tongTien)
                && Objects.equals(maNhanVienLap, hoaDon.maNhanVienLap)
                && Objects.equals(ghiChu, hoaDon.ghiChu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maHoaDon, maKhachHang, ngayLap, tongTien, maNhanVienLap, ghiChu);
    }

    // toString
    @Override
    public String toString() {
        return "HoaDon{"
                + "maHoaDon=" + maHoaDon
                + ", maKhachHang=" + maKhachHang
                + ", ngayLap=" + ngayLap
                + ", tongTien=" + tongTien
                + ", maNhanVienLap=" + maNhanVienLap
                + ", ghiChu='" + ghiChu + '\''
                + '}';
    }
}
