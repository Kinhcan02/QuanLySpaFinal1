package Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class ChiTietTienDichVuCuaNhanVien {
    private Integer maCTTienDV;
    private Integer maCTHD;
    private Integer maDichVu;
    private Integer maNhanVien;
    private Integer maPhanTram;
    private BigDecimal donGiaThucTe;
    private LocalDateTime ngayTao;

    // Reference objects
    private ChiTietHoaDon chiTietHoaDon;
    private DichVu dichVu;
    private NhanVien nhanVien;
    private PhanTramDichVu phanTramDichVu;

    // Constructors
    public ChiTietTienDichVuCuaNhanVien() {
        this.donGiaThucTe = BigDecimal.ZERO;
        this.ngayTao = LocalDateTime.now();
    }

    public ChiTietTienDichVuCuaNhanVien(Integer maCTHD, Integer maDichVu, Integer maNhanVien, Integer maPhanTram) {
        this();
        this.maCTHD = maCTHD;
        this.maDichVu = maDichVu;
        this.maNhanVien = maNhanVien;
        this.maPhanTram = maPhanTram;
    }

    // Getters and Setters
    public Integer getMaCTTienDV() { return maCTTienDV; }
    public void setMaCTTienDV(Integer maCTTienDV) { this.maCTTienDV = maCTTienDV; }

    public Integer getMaCTHD() { return maCTHD; }
    public void setMaCTHD(Integer maCTHD) { this.maCTHD = maCTHD; }

    public Integer getMaDichVu() { return maDichVu; }
    public void setMaDichVu(Integer maDichVu) { this.maDichVu = maDichVu; }

    public Integer getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(Integer maNhanVien) { this.maNhanVien = maNhanVien; }

    public Integer getMaPhanTram() { return maPhanTram; }
    public void setMaPhanTram(Integer maPhanTram) { this.maPhanTram = maPhanTram; }

    public BigDecimal getDonGiaThucTe() { return donGiaThucTe; }
    public void setDonGiaThucTe(BigDecimal donGiaThucTe) { 
        this.donGiaThucTe = donGiaThucTe != null ? donGiaThucTe : BigDecimal.ZERO;
    }

    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { 
        this.ngayTao = ngayTao != null ? ngayTao : LocalDateTime.now();
    }

    public ChiTietHoaDon getChiTietHoaDon() { return chiTietHoaDon; }
    public void setChiTietHoaDon(ChiTietHoaDon chiTietHoaDon) { this.chiTietHoaDon = chiTietHoaDon; }

    public DichVu getDichVu() { return dichVu; }
    public void setDichVu(DichVu dichVu) { this.dichVu = dichVu; }

    public NhanVien getNhanVien() { return nhanVien; }
    public void setNhanVien(NhanVien nhanVien) { this.nhanVien = nhanVien; }

    public PhanTramDichVu getPhanTramDichVu() { return phanTramDichVu; }
    public void setPhanTramDichVu(PhanTramDichVu phanTramDichVu) { this.phanTramDichVu = phanTramDichVu; }

    // Tính đơn giá thực tế theo công thức mới: Thành tiền (ChiTietHoaDon) * Tỉ lệ phần trăm (PhanTramDichVu)
    public void tinhDonGiaThucTe() {
        if (chiTietHoaDon != null && phanTramDichVu != null) {
            BigDecimal thanhTien = chiTietHoaDon.getThanhTien();
            Double tiLePhanTram = phanTramDichVu.getTiLePhanTram();
            
            if (thanhTien != null && tiLePhanTram != null) {
                this.donGiaThucTe = thanhTien.multiply(BigDecimal.valueOf(tiLePhanTram))
                                             .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
            } else {
                this.donGiaThucTe = BigDecimal.ZERO;
            }
        } else {
            this.donGiaThucTe = BigDecimal.ZERO;
        }
    }

    // Tính thành tiền cho chi tiết này (trong trường hợp cần)
    public BigDecimal getThanhTien() {
        return donGiaThucTe;
    }

    // Validation
    public boolean isValid() {
        return maCTHD != null && maDichVu != null && maNhanVien != null && maPhanTram != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietTienDichVuCuaNhanVien that = (ChiTietTienDichVuCuaNhanVien) o;
        return Objects.equals(maCTTienDV, that.maCTTienDV) &&
               Objects.equals(maCTHD, that.maCTHD) &&
               Objects.equals(maDichVu, that.maDichVu) &&
               Objects.equals(maNhanVien, that.maNhanVien) &&
               Objects.equals(maPhanTram, that.maPhanTram) &&
               Objects.equals(donGiaThucTe, that.donGiaThucTe) &&
               Objects.equals(ngayTao, that.ngayTao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maCTTienDV, maCTHD, maDichVu, maNhanVien, maPhanTram, donGiaThucTe, ngayTao);
    }

    @Override
    public String toString() {
        return "ChiTietTienDichVuCuaNhanVien{" +
                "maCTTienDV=" + maCTTienDV +
                ", maCTHD=" + maCTHD +
                ", maDichVu=" + maDichVu +
                ", maNhanVien=" + maNhanVien +
                ", maPhanTram=" + maPhanTram +
                ", donGiaThucTe=" + donGiaThucTe +
                ", ngayTao=" + ngayTao +
                '}';
    }
}