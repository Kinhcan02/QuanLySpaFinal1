package Model;

import java.time.LocalDate;
import java.math.BigDecimal;

public class NhanVien {
    private Integer maNhanVien;
    private String hoTen;
    private LocalDate ngaySinh;
    private String soDienThoai;
    private String diaChi;
    private String chucVu;
    private LocalDate ngayVaoLam;
    private BigDecimal heSoLuong;
    
    // Constructor mặc định
    public NhanVien() {
        this.heSoLuong = new BigDecimal("0.3");
    }
    
    // Constructor với họ tên và số điện thoại
    public NhanVien(String hoTen, String soDienThoai) {
        this();
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
    }
    
    // Constructor đầy đủ
    public NhanVien(Integer maNhanVien, String hoTen, LocalDate ngaySinh, 
                   String soDienThoai, String diaChi, String chucVu, 
                   LocalDate ngayVaoLam, BigDecimal heSoLuong) {
        this.maNhanVien = maNhanVien;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.soDienThoai = soDienThoai;
        this.diaChi = diaChi;
        this.chucVu = chucVu;
        this.ngayVaoLam = ngayVaoLam;
        this.heSoLuong = heSoLuong != null ? heSoLuong : new BigDecimal("0.3");
    }

    // Getter và Setter
    public Integer getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(Integer maNhanVien) { this.maNhanVien = maNhanVien; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public String getChucVu() { return chucVu; }
    public void setChucVu(String chucVu) { this.chucVu = chucVu; }

    public LocalDate getNgayVaoLam() { return ngayVaoLam; }
    public void setNgayVaoLam(LocalDate ngayVaoLam) { this.ngayVaoLam = ngayVaoLam; }

    public BigDecimal getHeSoLuong() { return heSoLuong; }
    public void setHeSoLuong(BigDecimal heSoLuong) { 
        if (heSoLuong != null && heSoLuong.compareTo(BigDecimal.ZERO) >= 0) {
            this.heSoLuong = heSoLuong;
        }
    }

    // Phương thức kiểm tra hợp lệ
    public boolean isValid() {
        return hoTen != null && !hoTen.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "NhanVien{" +
                "maNhanVien=" + maNhanVien +
                ", hoTen='" + hoTen + '\'' +
                ", ngaySinh=" + ngaySinh +
                ", soDienThoai='" + soDienThoai + '\'' +
                ", diaChi='" + diaChi + '\'' +
                ", chucVu='" + chucVu + '\'' +
                ", ngayVaoLam=" + ngayVaoLam +
                ", heSoLuong=" + heSoLuong +
                '}';
    }
}