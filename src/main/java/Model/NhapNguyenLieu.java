package Model;

import java.time.LocalDate;
import java.math.BigDecimal;

public class NhapNguyenLieu {
    private Integer maNhap;
    private Integer maNguyenLieu;
    private LocalDate ngayNhap;
    private String tenNguyenLieu;
    private String donViTinh;
    private Integer soLuong;
    private BigDecimal donGia;
    private String nguonNhap;
    
    // Constructor mặc định
    public NhapNguyenLieu() {
        this.ngayNhap = LocalDate.now();
    }
    
    // Constructor với thông tin cơ bản
    public NhapNguyenLieu(Integer maNguyenLieu, String tenNguyenLieu, Integer soLuong, BigDecimal donGia) {
        this();
        this.maNguyenLieu = maNguyenLieu;
        this.tenNguyenLieu = tenNguyenLieu;
        this.soLuong = soLuong;
        this.donGia = donGia;
    }
    
    // Constructor với đơn vị tính
    public NhapNguyenLieu(Integer maNguyenLieu, String tenNguyenLieu, String donViTinh, 
                         Integer soLuong, BigDecimal donGia) {
        this(maNguyenLieu, tenNguyenLieu, soLuong, donGia);
        this.donViTinh = donViTinh;
    }
    
    // Constructor đầy đủ tham số (KHÔNG có khóa chính MaNhap)
    public NhapNguyenLieu(Integer maNguyenLieu, LocalDate ngayNhap, String tenNguyenLieu, 
                         String donViTinh, Integer soLuong, BigDecimal donGia, String nguonNhap) {
        this.maNguyenLieu = maNguyenLieu;
        this.ngayNhap = ngayNhap != null ? ngayNhap : LocalDate.now();
        this.tenNguyenLieu = tenNguyenLieu;
        this.donViTinh = donViTinh;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.nguonNhap = nguonNhap;
    }

    public NhapNguyenLieu(Integer maNhap, Integer maNguyenLieu, LocalDate ngayNhap, String tenNguyenLieu, String donViTinh, Integer soLuong, BigDecimal donGia, String nguonNhap) {
        this.maNhap = maNhap;
        this.maNguyenLieu = maNguyenLieu;
        this.ngayNhap = ngayNhap;
        this.tenNguyenLieu = tenNguyenLieu;
        this.donViTinh = donViTinh;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.nguonNhap = nguonNhap;
    }
    
    // Constructor không có nguồn nhập
    public NhapNguyenLieu(Integer maNguyenLieu, LocalDate ngayNhap, String tenNguyenLieu, 
                         String donViTinh, Integer soLuong, BigDecimal donGia) {
        this(maNguyenLieu, ngayNhap, tenNguyenLieu, donViTinh, soLuong, donGia, null);
    }
    
    // Getter và Setter
    public Integer getMaNhap() {
        return maNhap;
    }
    
    public void setMaNhap(Integer maNhap) {
        this.maNhap = maNhap;
    }
    
    public Integer getMaNguyenLieu() {
        return maNguyenLieu;
    }
    
    public void setMaNguyenLieu(Integer maNguyenLieu) {
        this.maNguyenLieu = maNguyenLieu;
    }
    
    public LocalDate getNgayNhap() {
        return ngayNhap;
    }
    
    public void setNgayNhap(LocalDate ngayNhap) {
        this.ngayNhap = ngayNhap != null ? ngayNhap : LocalDate.now();
    }
    
    public String getTenNguyenLieu() {
        return tenNguyenLieu;
    }
    
    public void setTenNguyenLieu(String tenNguyenLieu) {
        this.tenNguyenLieu = tenNguyenLieu;
    }
    
    public String getDonViTinh() {
        return donViTinh;
    }
    
    public void setDonViTinh(String donViTinh) {
        this.donViTinh = donViTinh;
    }
    
    public Integer getSoLuong() {
        return soLuong;
    }
    
    public void setSoLuong(Integer soLuong) {
        // Kiểm tra số lượng phải lớn hơn 0
        if (soLuong != null && soLuong > 0) {
            this.soLuong = soLuong;
        }
    }
    
    public BigDecimal getDonGia() {
        return donGia;
    }
    
    public void setDonGia(BigDecimal donGia) {
        // Kiểm tra đơn giá không được âm
        if (donGia != null && donGia.compareTo(BigDecimal.ZERO) >= 0) {
            this.donGia = donGia;
        }
    }
    
    public String getNguonNhap() {
        return nguonNhap;
    }
    
    public void setNguonNhap(String nguonNhap) {
        this.nguonNhap = nguonNhap;
    }
    
    // Phương thức tính thành tiền
    public BigDecimal getThanhTien() {
        if (soLuong != null && donGia != null) {
            return donGia.multiply(BigDecimal.valueOf(soLuong));
        }
        return BigDecimal.ZERO;
    }
    
    // Phương thức kiểm tra phiếu nhập có hợp lệ không
    public boolean isValid() {
        return maNguyenLieu != null && 
               tenNguyenLieu != null && !tenNguyenLieu.trim().isEmpty() &&
               soLuong != null && soLuong > 0 &&
               donGia != null && donGia.compareTo(BigDecimal.ZERO) >= 0;
    }
    
    // Phương thức kiểm tra có nguồn nhập không
    public boolean hasNguonNhap() {
        return nguonNhap != null && !nguonNhap.trim().isEmpty();
    }
    
    // Phương thức kiểm tra có đơn vị tính không
    public boolean hasDonViTinh() {
        return donViTinh != null && !donViTinh.trim().isEmpty();
    }
    
    // Phương thức kiểm tra nhập hàng trong tháng hiện tại
    public boolean isNhapTrongThangHienTai() {
        if (ngayNhap == null) return false;
        LocalDate now = LocalDate.now();
        return ngayNhap.getMonth() == now.getMonth() && ngayNhap.getYear() == now.getYear();
    }
    
    // Phương thức kiểm tra nhập hàng trong khoảng thời gian
    public boolean isNhapTrongKhoangThoiGian(LocalDate fromDate, LocalDate toDate) {
        if (ngayNhap == null || fromDate == null || toDate == null) return false;
        return !ngayNhap.isBefore(fromDate) && !ngayNhap.isAfter(toDate);
    }
    
    @Override
    public String toString() {
        return "NhapNguyenLieu{" +
                "maNhap=" + maNhap +
                ", maNguyenLieu=" + maNguyenLieu +
                ", ngayNhap=" + ngayNhap +
                ", tenNguyenLieu='" + tenNguyenLieu + '\'' +
                ", donViTinh='" + donViTinh + '\'' +
                ", soLuong=" + soLuong +
                ", donGia=" + donGia +
                ", nguonNhap='" + nguonNhap + '\'' +
                '}';
    }
}