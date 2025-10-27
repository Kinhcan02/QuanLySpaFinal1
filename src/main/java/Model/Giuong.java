package Model;

public class Giuong {
    private Integer maGiuong;
    private String soHieu;
    private Boolean trangThai;
    
    // Constructor mặc định
    public Giuong() {
        this.trangThai = false; // Mặc định là trống
    }
    
    // Constructor với số hiệu
    public Giuong(String soHieu) {
        this();
        this.soHieu = soHieu;
    }
    
    // Constructor với số hiệu và trạng thái
    public Giuong(String soHieu, Boolean trangThai) {
        this.soHieu = soHieu;
        this.trangThai = trangThai != null ? trangThai : false;
    }
    
    // Constructor đầy đủ tham số (KHÔNG có khóa chính MaGiuong)
    public Giuong(Integer maGiuong, String soHieu, Boolean trangThai) {
        this.maGiuong = maGiuong;
        this.soHieu = soHieu;
        this.trangThai = trangThai != null ? trangThai : false;
    }
    
    // Getter và Setter
    public Integer getMaGiuong() {
        return maGiuong;
    }
    
    public void setMaGiuong(Integer maGiuong) {
        this.maGiuong = maGiuong;
    }
    
    public String getSoHieu() {
        return soHieu;
    }
    
    public void setSoHieu(String soHieu) {
        this.soHieu = soHieu;
    }
    
    public Boolean getTrangThai() {
        return trangThai;
    }
    
    public void setTrangThai(Boolean trangThai) {
        this.trangThai = trangThai != null ? trangThai : false;
    }
    
    // Phương thức kiểm tra giường có hợp lệ không
    public boolean isValid() {
        return soHieu != null && !soHieu.trim().isEmpty();
    }
    
    // Phương thức kiểm tra giường có trống không
    public boolean isTrong() {
        return !trangThai;
    }
    
    // Phương thức kiểm tra giường đang được sử dụng
    public boolean isDangSuDung() {
        return trangThai;
    }
    
    // Phương thức đánh dấu giường là đang sử dụng
    public void markDangSuDung() {
        this.trangThai = true;
    }
    
    // Phương thức đánh dấu giường là trống
    public void markTrong() {
        this.trangThai = false;
    }
    
    // Phương thức chuyển đổi trạng thái
    public void toggleTrangThai() {
        this.trangThai = !this.trangThai;
    }
    
    // Phương thức lấy trạng thái dạng text
    public String getTrangThaiText() {
        return trangThai ? "Đang dùng" : "Trống";
    }
    
    @Override
    public String toString() {
        return "Giuong{" +
                "maGiuong=" + maGiuong +
                ", soHieu='" + soHieu + '\'' +
                ", trangThai=" + trangThai +
                ", trangThaiText='" + getTrangThaiText() + '\'' +
                '}';
    }
}