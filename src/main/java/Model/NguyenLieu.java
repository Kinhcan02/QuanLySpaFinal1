package Model;

public class NguyenLieu {
    private Integer maNguyenLieu;
    private String tenNguyenLieu;
    private Integer soLuongTon;
    private String donViTinh;
    private Integer maLoaiNL;
    
    // Constructor mặc định
    public NguyenLieu() {
        this.soLuongTon = 0;
    }
    
    // Constructor với tên nguyên liệu
    public NguyenLieu(String tenNguyenLieu) {
        this();
        this.tenNguyenLieu = tenNguyenLieu;
    }
    
    // Constructor với tên nguyên liệu và số lượng tồn
    public NguyenLieu(String tenNguyenLieu, Integer soLuongTon) {
        this.tenNguyenLieu = tenNguyenLieu;
        this.soLuongTon = soLuongTon != null ? soLuongTon : 0;
    }
    
    // Constructor với tên nguyên liệu, số lượng tồn và đơn vị tính
    public NguyenLieu(String tenNguyenLieu, Integer soLuongTon, String donViTinh) {
        this.tenNguyenLieu = tenNguyenLieu;
        this.soLuongTon = soLuongTon != null ? soLuongTon : 0;
        this.donViTinh = donViTinh;
    }
    
    // Constructor đầy đủ tham số (KHÔNG có khóa chính MaNguyenLieu)
    public NguyenLieu(String tenNguyenLieu, Integer soLuongTon, String donViTinh, Integer maLoaiNL) {
        this.tenNguyenLieu = tenNguyenLieu;
        this.soLuongTon = soLuongTon != null ? soLuongTon : 0;
        this.donViTinh = donViTinh;
        this.maLoaiNL = maLoaiNL;
    }

    public NguyenLieu(Integer maNguyenLieu, String tenNguyenLieu, Integer soLuongTon, String donViTinh, Integer maLoaiNL) {
        this.maNguyenLieu = maNguyenLieu;
        this.tenNguyenLieu = tenNguyenLieu;
        this.soLuongTon = soLuongTon;
        this.donViTinh = donViTinh;
        this.maLoaiNL = maLoaiNL;
    }
    
    // Getter và Setter
    public Integer getMaNguyenLieu() {
        return maNguyenLieu;
    }
    
    public void setMaNguyenLieu(Integer maNguyenLieu) {
        this.maNguyenLieu = maNguyenLieu;
    }
    
    public String getTenNguyenLieu() {
        return tenNguyenLieu;
    }
    
    public void setTenNguyenLieu(String tenNguyenLieu) {
        this.tenNguyenLieu = tenNguyenLieu;
    }
    
    public Integer getSoLuongTon() {
        return soLuongTon;
    }
    
    public void setSoLuongTon(Integer soLuongTon) {
        // Kiểm tra số lượng tồn không được âm
        if (soLuongTon != null && soLuongTon >= 0) {
            this.soLuongTon = soLuongTon;
        } else if (soLuongTon != null) {
            this.soLuongTon = 0; // Nếu âm thì set về 0
        }
    }
    
    public String getDonViTinh() {
        return donViTinh;
    }
    
    public void setDonViTinh(String donViTinh) {
        this.donViTinh = donViTinh;
    }
    
    public Integer getMaLoaiNL() {
        return maLoaiNL;
    }
    
    public void setMaLoaiNL(Integer maLoaiNL) {
        this.maLoaiNL = maLoaiNL;
    }
    
    // Phương thức kiểm tra nguyên liệu có hợp lệ không
    public boolean isValid() {
        return tenNguyenLieu != null && !tenNguyenLieu.trim().isEmpty() && soLuongTon >= 0;
    }
    
    // Phương thức kiểm tra nguyên liệu có thuộc loại không
    public boolean hasLoaiNguyenLieu() {
        return maLoaiNL != null;
    }
    
    // Phương thức kiểm tra còn hàng tồn kho
    public boolean isConHang() {
        return soLuongTon > 0;
    }
    
    // Phương thức kiểm tra hết hàng
    public boolean isHetHang() {
        return soLuongTon == 0;
    }
    
    // Phương thức thêm số lượng tồn kho
    public void themSoLuong(Integer soLuong) {
        if (soLuong != null && soLuong > 0) {
            this.soLuongTon += soLuong;
        }
    }
    
    // Phương thức giảm số lượng tồn kho
    public boolean giamSoLuong(Integer soLuong) {
        if (soLuong != null && soLuong > 0 && this.soLuongTon >= soLuong) {
            this.soLuongTon -= soLuong;
            return true;
        }
        return false;
    }
    
    // Phương thức kiểm tra đủ số lượng để sử dụng
    public boolean duSoLuong(Integer soLuongCan) {
        return soLuongCan != null && this.soLuongTon >= soLuongCan;
    }
    
    @Override
    public String toString() {
        return "NguyenLieu{" +
                "maNguyenLieu=" + maNguyenLieu +
                ", tenNguyenLieu='" + tenNguyenLieu + '\'' +
                ", soLuongTon=" + soLuongTon +
                ", donViTinh='" + donViTinh + '\'' +
                ", maLoaiNL=" + maLoaiNL +
                '}';
    }
}