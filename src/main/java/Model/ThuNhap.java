package Model;

import java.math.BigDecimal;
import java.time.LocalDate; // THAY ĐỔI TỪ LocalDateTime SANG LocalDate
import java.time.LocalDateTime;

public class ThuNhap {
    private Integer maThu;
    private LocalDate ngayThu; // THÊM TRƯỜNG NÀY
    private BigDecimal soTien; // THÊM TRƯỜNG NÀY
    private String noiDung; // THÊM TRƯỜNG NÀY
    
    // Các trường cũ
    private Integer thang;
    private Integer nam;
    private BigDecimal tongDoanhThuDichVu;
    private BigDecimal tongLuongNhanVien;
    private BigDecimal thuNhapThuc;
    private LocalDateTime ngayTinhThuNhap;
    private String ghiChu;

    // Constructor mặc định
    public ThuNhap() {
        this.ngayThu = LocalDate.now();
        this.soTien = BigDecimal.ZERO;
        this.tongDoanhThuDichVu = BigDecimal.ZERO;
        this.tongLuongNhanVien = BigDecimal.ZERO;
        this.thuNhapThuc = BigDecimal.ZERO;
        this.ngayTinhThuNhap = LocalDateTime.now();
    }

    // Constructor cho thu nhập đơn giản - THÊM CONSTRUCTOR NÀY
    public ThuNhap(LocalDate ngayThu, BigDecimal soTien, String noiDung) {
        this();
        this.ngayThu = ngayThu;
        this.soTien = soTien;
        this.noiDung = noiDung;
    }

    // Constructor đầy đủ cho thu nhập đơn giản - THÊM CONSTRUCTOR NÀY
    public ThuNhap(Integer maThu, LocalDate ngayThu, BigDecimal soTien, String noiDung) {
        this(ngayThu, soTien, noiDung);
        this.maThu = maThu;
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

    // THÊM GETTER VÀ SETTER CHO CÁC TRƯỜNG MỚI
    public LocalDate getNgayThu() {
        return ngayThu;
    }

    public void setNgayThu(LocalDate ngayThu) {
        this.ngayThu = ngayThu;
    }

    public BigDecimal getSoTien() {
        return soTien;
    }

    public void setSoTien(BigDecimal soTien) {
        this.soTien = soTien;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    // Giữ nguyên các getter/setter cũ
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
                ", ngayThu=" + ngayThu +
                ", soTien=" + soTien +
                ", noiDung='" + noiDung + '\'' +
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