package Model;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.time.Period;

public class NhanVien {

    private Integer maNhanVien;
    private String hoTen;
    private LocalDate ngaySinh;
    private String soDienThoai;
    private String diaChi;
    private String chucVu;
    private LocalDate ngayVaoLam;
    private BigDecimal luongCanBan;

    // Constructor mặc định
    public NhanVien() {
        this.luongCanBan = BigDecimal.ZERO;
    }

    // Constructor cho thêm mới nhân viên
    public NhanVien(String hoTen, LocalDate ngaySinh, String soDienThoai,
            String diaChi, String chucVu, LocalDate ngayVaoLam) {
        this();
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.soDienThoai = soDienThoai;
        this.diaChi = diaChi;
        this.chucVu = chucVu;
        this.ngayVaoLam = ngayVaoLam;
    }

    // Constructor đầy đủ
    public NhanVien(Integer maNhanVien, String hoTen, LocalDate ngaySinh,
            String soDienThoai, String diaChi, String chucVu,
            LocalDate ngayVaoLam, BigDecimal luongCanBan) {
        this.maNhanVien = maNhanVien;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.soDienThoai = soDienThoai;
        this.diaChi = diaChi;
        this.chucVu = chucVu;
        this.ngayVaoLam = ngayVaoLam;
        this.luongCanBan = luongCanBan != null ? luongCanBan : BigDecimal.ZERO;
    }

    // Getter và Setter
    public Integer getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(Integer maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getChucVu() {
        return chucVu;
    }

    public void setChucVu(String chucVu) {
        this.chucVu = chucVu;
    }

    public LocalDate getNgayVaoLam() {
        return ngayVaoLam;
    }

    public void setNgayVaoLam(LocalDate ngayVaoLam) {
        this.ngayVaoLam = ngayVaoLam;
    }

    public BigDecimal getLuongCanBan() {
        return luongCanBan;
    }

    public void setLuongCanBan(BigDecimal luongCanBan) {
        if (luongCanBan != null && luongCanBan.compareTo(BigDecimal.ZERO) >= 0) {
            this.luongCanBan = luongCanBan;
        } else {
            this.luongCanBan = BigDecimal.ZERO;
        }
    }

    // Phương thức để tương thích với code cũ (nếu có)
    public BigDecimal getHeSoLuong() {
        // Trong DB mới không có hệ số lương, trả về 1.0 làm giá trị mặc định
        return new BigDecimal("10.0");
    }

    public void setHeSoLuong(BigDecimal heSoLuong) {
        // Trong DB mới không có hệ số lương, không làm gì cả
        // Hoặc có thể map sang luongCanBan nếu cần
    }

    // Tính thâm niên
    public int getThamNien() {
        if (ngayVaoLam == null) {
            return 0;
        }
        return Period.between(ngayVaoLam, LocalDate.now()).getYears();
    }

    // Tính tuổi
    public int getTuoi() {
        if (ngaySinh == null) {
            return 0;
        }
        return Period.between(ngaySinh, LocalDate.now()).getYears();
    }

    // Phương thức kiểm tra hợp lệ
    public boolean isValid() {
        return hoTen != null && !hoTen.trim().isEmpty()
                && soDienThoai != null && !soDienThoai.trim().isEmpty()
                && chucVu != null && !chucVu.trim().isEmpty()
                && ngayVaoLam != null;
    }

    // Kiểm tra nhân viên có đang làm việc không
    public boolean isDangLamViec() {
        return maNhanVien != null && ngayVaoLam != null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(hoTen != null ? hoTen : "N/A");
        
        if (chucVu != null && !chucVu.isEmpty()) {
            sb.append(" - ").append(chucVu);
        }
        
        if (soDienThoai != null && !soDienThoai.isEmpty()) {
            sb.append(" (").append(soDienThoai).append(")");
        }
        
        return sb.toString();
    }

    // Phương thức hiển thị thông tin đầy đủ
    public String getThongTinDayDu() {
        return String.format(
            "Mã NV: %d | Họ tên: %s | Chức vụ: %s | Lương cơ bản: %s | SĐT: %s",
            maNhanVien != null ? maNhanVien : "N/A",
            hoTen != null ? hoTen : "N/A",
            chucVu != null ? chucVu : "N/A",
            luongCanBan != null ? luongCanBan.toString() : "0",
            soDienThoai != null ? soDienThoai : "N/A"
        );
    }
}