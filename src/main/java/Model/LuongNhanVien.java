package Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LuongNhanVien {
    private Integer maLuong;
    private Integer maNhanVien;
    private Integer thang;
    private Integer nam;
    private BigDecimal tongLuong;
    private LocalDateTime ngayTinhLuong;
    private String trangThai;

    // Reference object
    private NhanVien nhanVien;

    // Constructor mặc định
    public LuongNhanVien() {
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
    public LuongNhanVien(Integer maLuong, Integer maNhanVien, Integer thang, Integer nam,
                        BigDecimal tongLuong, LocalDateTime ngayTinhLuong, String trangThai) {
        this.maLuong = maLuong;
        this.maNhanVien = maNhanVien;
        this.thang = thang;
        this.nam = nam;
        this.tongLuong = tongLuong != null ? tongLuong : BigDecimal.ZERO;
        this.ngayTinhLuong = ngayTinhLuong;
        this.trangThai = trangThai != null ? trangThai : "Chưa thanh toán";
    }

    // Getter và Setter
    public Integer getMaLuong() { return maLuong; }
    public void setMaLuong(Integer maLuong) { this.maLuong = maLuong; }

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

    public NhanVien getNhanVien() { return nhanVien; }
    public void setNhanVien(NhanVien nhanVien) { this.nhanVien = nhanVien; }

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
                "maLuong=" + maLuong +
                ", maNhanVien=" + maNhanVien +
                ", thang=" + thang +
                ", nam=" + nam +
                ", tongLuong=" + tongLuong +
                ", ngayTinhLuong=" + ngayTinhLuong +
                ", trangThai='" + trangThai + '\'' +
                '}';
    }
}