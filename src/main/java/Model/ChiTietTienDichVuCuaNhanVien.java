package Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class ChiTietTienDichVuCuaNhanVien {
    private Integer maCTTienDV;
    private Integer maCTHD;
    private Integer maDichVu;
    private Integer maNhanVien;
    private Integer soLuong;
    private BigDecimal donGiaGoc;
    private Double tiLePhanTram;
    private BigDecimal donGiaThucTe;
    private LocalDateTime ngayTao;

    // Reference objects
    private ChiTietHoaDon chiTietHoaDon;
    private DichVu dichVu;
    private NhanVien nhanVien;

    // Constructors
    public ChiTietTienDichVuCuaNhanVien() {
        this.soLuong = 1;
        this.donGiaGoc = BigDecimal.ZERO;
        this.tiLePhanTram = 0.0;
        this.donGiaThucTe = BigDecimal.ZERO;
        this.ngayTao = LocalDateTime.now();
    }

    public ChiTietTienDichVuCuaNhanVien(Integer maCTHD, Integer maDichVu, Integer maNhanVien, 
                                       Integer soLuong, BigDecimal donGiaGoc, Double tiLePhanTram) {
        this();
        this.maCTHD = maCTHD;
        this.maDichVu = maDichVu;
        this.maNhanVien = maNhanVien;
        this.soLuong = soLuong;
        this.donGiaGoc = donGiaGoc;
        this.tiLePhanTram = tiLePhanTram;
        tinhDonGiaThucTe();
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

    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { 
        this.soLuong = soLuong != null ? soLuong : 1;
        tinhDonGiaThucTe();
    }

    public BigDecimal getDonGiaGoc() { return donGiaGoc; }
    public void setDonGiaGoc(BigDecimal donGiaGoc) { 
        this.donGiaGoc = donGiaGoc != null ? donGiaGoc : BigDecimal.ZERO;
        tinhDonGiaThucTe();
    }

    public Double getTiLePhanTram() { return tiLePhanTram; }
    public void setTiLePhanTram(Double tiLePhanTram) { 
        this.tiLePhanTram = tiLePhanTram != null ? tiLePhanTram : 0.0;
        tinhDonGiaThucTe();
    }

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

    // Tính đơn giá thực tế theo công thức
    public void tinhDonGiaThucTe() {
        if (donGiaGoc != null && tiLePhanTram != null) {
            this.donGiaThucTe = donGiaGoc.multiply(BigDecimal.valueOf(tiLePhanTram))
                                         .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
        } else {
            this.donGiaThucTe = BigDecimal.ZERO;
        }
    }

    // Tính thành tiền
    public BigDecimal getThanhTien() {
        return donGiaThucTe.multiply(BigDecimal.valueOf(soLuong));
    }

    // Validation
    public boolean isValid() {
        return maCTHD != null && maDichVu != null && maNhanVien != null && 
               soLuong != null && soLuong > 0 && donGiaGoc != null;
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
               Objects.equals(soLuong, that.soLuong) &&
               Objects.equals(donGiaGoc, that.donGiaGoc) &&
               Objects.equals(tiLePhanTram, that.tiLePhanTram) &&
               Objects.equals(donGiaThucTe, that.donGiaThucTe) &&
               Objects.equals(ngayTao, that.ngayTao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maCTTienDV, maCTHD, maDichVu, maNhanVien, soLuong, 
                           donGiaGoc, tiLePhanTram, donGiaThucTe, ngayTao);
    }

    @Override
    public String toString() {
        return "ChiTietTienDichVuCuaNhanVien{" +
                "maCTTienDV=" + maCTTienDV +
                ", maCTHD=" + maCTHD +
                ", maDichVu=" + maDichVu +
                ", maNhanVien=" + maNhanVien +
                ", soLuong=" + soLuong +
                ", donGiaGoc=" + donGiaGoc +
                ", tiLePhanTram=" + tiLePhanTram +
                ", donGiaThucTe=" + donGiaThucTe +
                ", ngayTao=" + ngayTao +
                '}';
    }
}