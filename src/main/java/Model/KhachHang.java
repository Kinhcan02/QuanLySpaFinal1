package Model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class KhachHang {
    private Integer maKhachHang;
    private String hoTen;
    private LocalDate ngaySinh;
    private String loaiKhach;
    private String soDienThoai;
    private String ghiChu;
    private LocalDateTime ngayTao;
    
    // Constructor mặc định
    public KhachHang() {
        this.ngayTao = LocalDateTime.now();
    }
    
    // Constructor với họ tên
    public KhachHang(String hoTen) {
        this();
        this.hoTen = hoTen;
    }
    
    // Constructor với họ tên và số điện thoại
    public KhachHang(String hoTen, String soDienThoai) {
        this();
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
    }
    
    // Constructor với họ tên, số điện thoại và loại khách
    public KhachHang(String hoTen, String soDienThoai, String loaiKhach) {
        this();
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.loaiKhach = loaiKhach;
    }
    
    // Constructor đầy đủ tham số (KHÔNG có khóa chính MaKhachHang)
    public KhachHang(String hoTen, LocalDate ngaySinh, String loaiKhach, 
                    String soDienThoai, String ghiChu, LocalDateTime ngayTao) {
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.loaiKhach = loaiKhach;
        this.soDienThoai = soDienThoai;
        this.ghiChu = ghiChu;
        this.ngayTao = ngayTao != null ? ngayTao : LocalDateTime.now();
    }

    public KhachHang(Integer maKhachHang, String hoTen, LocalDate ngaySinh, String loaiKhach, String soDienThoai, String ghiChu, LocalDateTime ngayTao) {
        this.maKhachHang = maKhachHang;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.loaiKhach = loaiKhach;
        this.soDienThoai = soDienThoai;
        this.ghiChu = ghiChu;
        this.ngayTao = ngayTao;
    }
    
    // Constructor không có ngày tạo (tự động set ngày hiện tại)
    public KhachHang(String hoTen, LocalDate ngaySinh, String loaiKhach, 
                    String soDienThoai, String ghiChu) {
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.loaiKhach = loaiKhach;
        this.soDienThoai = soDienThoai;
        this.ghiChu = ghiChu;
        this.ngayTao = LocalDateTime.now();
    }
    
    // Getter và Setter
    public Integer getMaKhachHang() {
        return maKhachHang;
    }
    
    public void setMaKhachHang(Integer maKhachHang) {
        this.maKhachHang = maKhachHang;
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
    
    public String getLoaiKhach() {
        return loaiKhach;
    }
    
    public void setLoaiKhach(String loaiKhach) {
        this.loaiKhach = loaiKhach;
    }
    
    public String getSoDienThoai() {
        return soDienThoai;
    }
    
    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }
    
    public String getGhiChu() {
        return ghiChu;
    }
    
    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
    
    public LocalDateTime getNgayTao() {
        return ngayTao;
    }
    
    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao != null ? ngayTao : LocalDateTime.now();
    }
    
    // Phương thức kiểm tra khách hàng có hợp lệ không
    public boolean isValid() {
        return hoTen != null && !hoTen.trim().isEmpty();
    }
    
    // Phương thức kiểm tra có số điện thoại không
    public boolean hasSoDienThoai() {
        return soDienThoai != null && !soDienThoai.trim().isEmpty();
    }
    
    // Phương thức kiểm tra có ngày sinh không
    public boolean hasNgaySinh() {
        return ngaySinh != null;
    }
    
    // Phương thức kiểm tra có loại khách không
    public boolean hasLoaiKhach() {
        return loaiKhach != null && !loaiKhach.trim().isEmpty();
    }
    
    // Phương thức kiểm tra có ghi chú không
    public boolean hasGhiChu() {
        return ghiChu != null && !ghiChu.trim().isEmpty();
    }
    
    // Phương thức tính tuổi
    public Integer getTuoi() {
        if (ngaySinh == null) {
            return null;
        }
        return LocalDate.now().getYear() - ngaySinh.getYear();
    }
    
    // Phương thức kiểm tra có phải khách hàng thân thiết không
    public boolean isKhachThanThiet() {
        return "Thân thiết".equals(loaiKhach);
    }
    
    // Phương thức kiểm tra có phải khách hàng thường xuyên không
    public boolean isKhachThuongXuyen() {
        return "Thường xuyên".equals(loaiKhach);
    }
    
    // Phương thức kiểm tra khách hàng mới (tạo trong vòng 30 ngày)
    public boolean isKhachMoi() {
        if (ngayTao == null) return false;
        return ngayTao.isAfter(LocalDateTime.now().minusDays(30));
    }
    
    @Override
    public String toString() {
        return "KhachHang{" +
                "maKhachHang=" + maKhachHang +
                ", hoTen='" + hoTen + '\'' +
                ", ngaySinh=" + ngaySinh +
                ", loaiKhach='" + loaiKhach + '\'' +
                ", soDienThoai='" + soDienThoai + '\'' +
                ", ghiChu='" + ghiChu + '\'' +
                ", ngayTao=" + ngayTao +
                '}';
    }
}