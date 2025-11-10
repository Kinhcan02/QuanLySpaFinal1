package Model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ThuNhap {
    private Integer maThu;
    private int thang;
    private int nam;
    private BigDecimal tongDoanhThuDichVu;
    private BigDecimal tongLuongNhanVien;
    private BigDecimal thuNhapThuc;
    private LocalDate ngayTinhThuNhap;
    private String ghiChu;

    public ThuNhap() {
        this.tongDoanhThuDichVu = BigDecimal.ZERO;
        this.tongLuongNhanVien = BigDecimal.ZERO;
        this.thuNhapThuc = BigDecimal.ZERO;
        this.ngayTinhThuNhap = LocalDate.now();
    }

    public ThuNhap(int thang, int nam, BigDecimal tongDoanhThuDichVu,
                  BigDecimal tongLuongNhanVien, String ghiChu) {
        this();
        this.thang = thang;
        this.nam = nam;
        this.tongDoanhThuDichVu = tongDoanhThuDichVu != null ? tongDoanhThuDichVu : BigDecimal.ZERO;
        this.tongLuongNhanVien = tongLuongNhanVien != null ? tongLuongNhanVien : BigDecimal.ZERO;
        this.thuNhapThuc = this.tongDoanhThuDichVu.subtract(this.tongLuongNhanVien);
        if (this.thuNhapThuc.compareTo(BigDecimal.ZERO) < 0) {
            this.thuNhapThuc = BigDecimal.ZERO;
        }
        this.ghiChu = ghiChu;
    }

    public ThuNhap(int maThu, int thang, int nam, BigDecimal tongDoanhThuDichVu,
                  BigDecimal tongLuongNhanVien, BigDecimal thuNhapThuc,
                  LocalDate ngayTinhThuNhap, String ghiChu) {
        this.maThu = maThu;
        this.thang = thang;
        this.nam = nam;
        this.tongDoanhThuDichVu = tongDoanhThuDichVu != null ? tongDoanhThuDichVu : BigDecimal.ZERO;
        this.tongLuongNhanVien = tongLuongNhanVien != null ? tongLuongNhanVien : BigDecimal.ZERO;
        this.thuNhapThuc = thuNhapThuc != null ? thuNhapThuc : BigDecimal.ZERO;
        this.ngayTinhThuNhap = ngayTinhThuNhap != null ? ngayTinhThuNhap : LocalDate.now();
        this.ghiChu = ghiChu;
    }

    // Getter và Setter
    public Integer getMaThu() {
        return maThu;
    }

    public void setMaThu(Integer maThu) {
        this.maThu = maThu;
    }

    public int getThang() {
        return thang;
    }

    public void setThang(int thang) {
        if (thang < 1 || thang > 12) {
            throw new IllegalArgumentException("Tháng phải từ 1 đến 12");
        }
        this.thang = thang;
    }

    public int getNam() {
        return nam;
    }

    public void setNam(int nam) {
        this.nam = nam;
    }

    public BigDecimal getTongDoanhThuDichVu() {
        return tongDoanhThuDichVu;
    }

    public void setTongDoanhThuDichVu(BigDecimal tongDoanhThuDichVu) {
        this.tongDoanhThuDichVu = tongDoanhThuDichVu != null ? tongDoanhThuDichVu : BigDecimal.ZERO;
        tinhThuNhapThuc();
    }

    public BigDecimal getTongLuongNhanVien() {
        return tongLuongNhanVien;
    }

    public void setTongLuongNhanVien(BigDecimal tongLuongNhanVien) {
        this.tongLuongNhanVien = tongLuongNhanVien != null ? tongLuongNhanVien : BigDecimal.ZERO;
        tinhThuNhapThuc();
    }

    public BigDecimal getThuNhapThuc() {
        return thuNhapThuc;
    }

    public void setThuNhapThuc(BigDecimal thuNhapThuc) {
        this.thuNhapThuc = thuNhapThuc != null ? thuNhapThuc : BigDecimal.ZERO;
    }

    public LocalDate getNgayTinhThuNhap() {
        return ngayTinhThuNhap;
    }

    public void setNgayTinhThuNhap(LocalDate ngayTinhThuNhap) {
        this.ngayTinhThuNhap = ngayTinhThuNhap != null ? ngayTinhThuNhap : LocalDate.now();
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    // Phương thức tính thu nhập thực
    private void tinhThuNhapThuc() {
        if (this.tongDoanhThuDichVu != null && this.tongLuongNhanVien != null) {
            this.thuNhapThuc = this.tongDoanhThuDichVu.subtract(this.tongLuongNhanVien);
            if (this.thuNhapThuc.compareTo(BigDecimal.ZERO) < 0) {
                this.thuNhapThuc = BigDecimal.ZERO;
            }
        } else {
            this.thuNhapThuc = BigDecimal.ZERO;
        }
    }

    // Validation
    public boolean isValid() {
        return thang >= 1 && thang <= 12 && nam > 0 && nam <= LocalDate.now().getYear() + 1;
    }

    @Override
    public String toString() {
        return String.format("ThuNhap{maThu=%d, thang=%d, nam=%d, doanhThu=%f, luong=%f, thuNhapThuc=%f}",
                maThu, thang, nam, tongDoanhThuDichVu, tongLuongNhanVien, thuNhapThuc);
    }
}