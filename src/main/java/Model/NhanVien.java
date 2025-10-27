package Model;

import java.time.LocalDate;

public class NhanVien {
    private Integer maNhanVien;
    private String hoTen;
    private LocalDate ngaySinh;
    private String soDienThoai;
    private String diaChi;
    private String chucVu;
    private LocalDate ngayVaoLam;
    
    // Constructor mặc định
    public NhanVien() {
    }
    
    // Constructor với họ tên và số điện thoại
    public NhanVien(String hoTen, String soDienThoai) {
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
    }
    
    // Constructor đầy đủ tham số (KHÔNG có khóa chính MaNhanVien)
    public NhanVien(String hoTen, LocalDate ngaySinh, String soDienThoai, 
                   String diaChi, String chucVu, LocalDate ngayVaoLam) {
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.soDienThoai = soDienThoai;
        this.diaChi = diaChi;
        this.chucVu = chucVu;
        this.ngayVaoLam = ngayVaoLam;
    }
    
    // Constructor không có ngày sinh và địa chỉ
    public NhanVien(String hoTen, String soDienThoai, String chucVu, LocalDate ngayVaoLam) {
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.chucVu = chucVu;
        this.ngayVaoLam = ngayVaoLam;
    }
    
    // Constructor cho nhân viên mới vào làm
    public NhanVien(String hoTen, String soDienThoai, String chucVu) {
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.chucVu = chucVu;
        this.ngayVaoLam = LocalDate.now(); // Mặc định là ngày hiện tại
    }

    public NhanVien(Integer maNhanVien, String hoTen, LocalDate ngaySinh, String soDienThoai, String diaChi, String chucVu, LocalDate ngayVaoLam) {
        this.maNhanVien = maNhanVien;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.soDienThoai = soDienThoai;
        this.diaChi = diaChi;
        this.chucVu = chucVu;
        this.ngayVaoLam = ngayVaoLam;
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
    
    // Phương thức tính thâm niên (số năm làm việc)
    public Integer getThamNien() {
        if (ngayVaoLam == null) {
            return 0;
        }
        return LocalDate.now().getYear() - ngayVaoLam.getYear();
    }
    
    // Phương thức kiểm tra nhân viên có hợp lệ không
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
                '}';
    }
}