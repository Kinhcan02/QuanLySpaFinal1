package Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ThuNhap {
    private Integer maThu;
    private Integer thang;
    private Integer nam;
    private BigDecimal tongDoanhThuDichVu;
    private BigDecimal tongLuongNhanVien;
    private BigDecimal thuNhapThuc;
    private LocalDateTime ngayTinhThuNhap;
    private String ghiChu;

    // Constructor mặc định
    public ThuNhap() {
        this.tongDoanhThuDichVu = BigDecimal.ZERO;
        this.tongLuongNhanVien = BigDecimal.ZERO;
        this.thuNhapThuc = BigDecimal.ZERO;
        this.ngayTinhThuNhap = LocalDateTime.now();
    }

    // Constructor với tháng, năm
    public ThuNhap(Integer thang, Integer nam) {
        this();
        this.thang = thang;
        this.nam = nam;
    }

    // Constructor đầy đủ
    public ThuNhap(Integer maThu, Integer thang, Integer nam, 
                  BigDecimal tongDoanhThuDichVu, BigDecimal tongLuongNhanVien,
                  BigDecimal thuNhapThuc, LocalDateTime ngayTinhThuNhap, String ghiChu) {
        this.maThu = maThu;
        this.thang = thang;
        this.nam = nam;
        this.tongDoanhThuDichVu = tongDoanhThuDichVu != null ? tongDoanhThuDichVu : BigDecimal.ZERO;
        this.tongLuongNhanVien = tongLuongNhanVien != null ? tongLuongNhanVien : BigDecimal.ZERO;
        this.thuNhapThuc = thuNhapThuc != null ? thuNhapThuc : BigDecimal.ZERO;
        this.ngayTinhThuNhap = ngayTinhThuNhap;
        this.ghiChu = ghiChu;
    }

    // Getter và Setter
    public Integer getMaThu() { return maThu; }
    public void setMaThu(Integer maThu) { this.maThu = maThu; }

    public Integer getThang() { return thang; }
    public void setThang(Integer thang) { 
        if (thang != null && thang >= 1 && thang <= 12) {
            this.thang = thang;
        }
    }

    public Integer getNam() { return nam; }
    public void setNam(Integer nam) { this.nam = nam; }

    public BigDecimal getTongDoanhThuDichVu() { return tongDoanhThuDichVu; }
    public void setTongDoanhThuDichVu(BigDecimal tongDoanhThuDichVu) { 
        this.tongDoanhThuDichVu = tongDoanhThuDichVu != null ? tongDoanhThuDichVu : BigDecimal.ZERO; 
    }

    public BigDecimal getTongLuongNhanVien() { return tongLuongNhanVien; }
    public void setTongLuongNhanVien(BigDecimal tongLuongNhanVien) { 
        this.tongLuongNhanVien = tongLuongNhanVien != null ? tongLuongNhanVien : BigDecimal.ZERO; 
    }

    public BigDecimal getThuNhapThuc() { return thuNhapThuc; }
    public void setThuNhapThuc(BigDecimal thuNhapThuc) { 
        this.thuNhapThuc = thuNhapThuc != null ? thuNhapThuc : BigDecimal.ZERO; 
    }

    public LocalDateTime getNgayTinhThuNhap() { return ngayTinhThuNhap; }
    public void setNgayTinhThuNhap(LocalDateTime ngayTinhThuNhap) { this.ngayTinhThuNhap = ngayTinhThuNhap; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    // Phương thức tính thu nhập thực
    public void tinhThuNhapThuc() {
        this.thuNhapThuc = this.tongDoanhThuDichVu.subtract(this.tongLuongNhanVien);
    }

    // Phương thức kiểm tra hợp lệ
    public boolean isValid() {
        return thang != null && nam != null && thang >= 1 && thang <= 12;
    }

    @Override
    public String toString() {
        return "ThuNhap{" +
                "maThu=" + maThu +
                ", thang=" + thang +
                ", nam=" + nam +
                ", tongDoanhThuDichVu=" + tongDoanhThuDichVu +
                ", tongLuongNhanVien=" + tongLuongNhanVien +
                ", thuNhapThuc=" + thuNhapThuc +
                ", ngayTinhThuNhap=" + ngayTinhThuNhap +
                ", ghiChu='" + ghiChu + '\'' +
                '}';
    }
}