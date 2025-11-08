package Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LuongNhanVien {
    private Integer maTinhLuong;
    private Integer maNhanVien;
    private Integer thang;
    private Integer nam;
    private BigDecimal luongCanBan;
    private BigDecimal tongTienDichVu;
    private BigDecimal tongLuong;
    private LocalDateTime ngayTinhLuong;
    private String trangThai;
    private String ghiChu;

    // Reference object
    private NhanVien nhanVien;

    // Constructor mặc định
    public LuongNhanVien() {
        this.luongCanBan = BigDecimal.ZERO;
        this.tongTienDichVu = BigDecimal.ZERO;
        this.tongLuong = BigDecimal.ZERO;
        this.trangThai = "Chưa thanh toán";
        this.ngayTinhLuong = LocalDateTime.now();
    }

    // Constructor với tham số chính
    public LuongNhanVien(Integer maNhanVien, Integer thang, Integer nam) {
        this();
        this.maNhanVien = maNhanVien;
        this.thang = thang;
        this.nam = nam;
    }

    // Constructor đầy đủ
    public LuongNhanVien(Integer maTinhLuong, Integer maNhanVien, Integer thang, Integer nam,
                        BigDecimal luongCanBan, BigDecimal tongTienDichVu, BigDecimal tongLuong,
                        LocalDateTime ngayTinhLuong, String trangThai, String ghiChu) {
        this.maTinhLuong = maTinhLuong;
        this.maNhanVien = maNhanVien;
        this.thang = thang;
        this.nam = nam;
        this.luongCanBan = luongCanBan != null ? luongCanBan : BigDecimal.ZERO;
        this.tongTienDichVu = tongTienDichVu != null ? tongTienDichVu : BigDecimal.ZERO;
        this.tongLuong = tongLuong != null ? tongLuong : BigDecimal.ZERO;
        this.ngayTinhLuong = ngayTinhLuong;
        this.trangThai = trangThai != null ? trangThai : "Chưa thanh toán";
        this.ghiChu = ghiChu;
    }

    // Getter và Setter
    public Integer getMaLuong() { return maTinhLuong; }
    public void setMaLuong(Integer maTinhLuong) { this.maTinhLuong = maTinhLuong; }

    public Integer getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(Integer maNhanVien) { this.maNhanVien = maNhanVien; }

    public Integer getThang() { return thang; }
    public void setThang(Integer thang) { 
        if (thang != null && thang >= 1 && thang <= 12) {
            this.thang = thang;
        }
    }

    public Integer getNam() { return nam; }
    public void setNam(Integer nam) { this.nam = nam; }

    public BigDecimal getLuongCanBan() { return luongCanBan; }
    public void setLuongCanBan(BigDecimal luongCanBan) { 
        this.luongCanBan = luongCanBan != null ? luongCanBan : BigDecimal.ZERO; 
    }

    public BigDecimal getTongTienDichVu() { return tongTienDichVu; }
    public void setTongTienDichVu(BigDecimal tongTienDichVu) { 
        this.tongTienDichVu = tongTienDichVu != null ? tongTienDichVu : BigDecimal.ZERO; 
    }

    public BigDecimal getTongLuong() { return tongLuong; }
    public void setTongLuong(BigDecimal tongLuong) { 
        this.tongLuong = tongLuong != null ? tongLuong : BigDecimal.ZERO; 
    }

    public LocalDateTime getNgayTinhLuong() { return ngayTinhLuong; }
    public void setNgayTinhLuong(LocalDateTime ngayTinhLuong) { this.ngayTinhLuong = ngayTinhLuong; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { 
        this.trangThai = trangThai != null ? trangThai : "Chưa thanh toán"; 
    }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public NhanVien getNhanVien() { return nhanVien; }
    public void setNhanVien(NhanVien nhanVien) { this.nhanVien = nhanVien; }

    // Phương thức tính tổng lương tự động
    public void tinhTongLuong() {
        this.tongLuong = this.luongCanBan.add(this.tongTienDichVu);
    }

    // Phương thức kiểm tra hợp lệ
    public boolean isValid() {
        return maNhanVien != null && thang != null && nam != null && 
               thang >= 1 && thang <= 12;
    }

    // Phương thức kiểm tra đã thanh toán chưa
    public boolean isDaThanhToan() {
        return "Đã thanh toán".equals(trangThai);
    }

    @Override
    public String toString() {
        return "LuongNhanVien{" +
                "maTinhLuong=" + maTinhLuong +
                ", maNhanVien=" + maNhanVien +
                ", thang=" + thang +
                ", nam=" + nam +
                ", luongCanBan=" + luongCanBan +
                ", tongTienDichVu=" + tongTienDichVu +
                ", tongLuong=" + tongLuong +
                ", ngayTinhLuong=" + ngayTinhLuong +
                ", trangThai='" + trangThai + '\'' +
                ", ghiChu='" + ghiChu + '\'' +
                '}';
    }
}