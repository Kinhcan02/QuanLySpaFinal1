package Model;

public class LoaiNguyenLieu {
    private Integer maLoaiNL;
    private String tenLoaiNL;
    private String moTa;
    
    // Constructor mặc định
    public LoaiNguyenLieu() {
    }
    
    // Constructor với tên loại nguyên liệu
    public LoaiNguyenLieu(String tenLoaiNL) {
        this.tenLoaiNL = tenLoaiNL;
    }
    
    // Constructor đầy đủ tham số (KHÔNG có khóa chính MaLoaiNL)
    public LoaiNguyenLieu(String tenLoaiNL, String moTa) {
        this.tenLoaiNL = tenLoaiNL;
        this.moTa = moTa;
    }

    public LoaiNguyenLieu(Integer maLoaiNL, String tenLoaiNL, String moTa) {
        this.maLoaiNL = maLoaiNL;
        this.tenLoaiNL = tenLoaiNL;
        this.moTa = moTa;
    }
    
    // Getter và Setter
    public Integer getMaLoaiNL() {
        return maLoaiNL;
    }
    
    public void setMaLoaiNL(Integer maLoaiNL) {
        this.maLoaiNL = maLoaiNL;
    }
    
    public String getTenLoaiNL() {
        return tenLoaiNL;
    }
    
    public void setTenLoaiNL(String tenLoaiNL) {
        this.tenLoaiNL = tenLoaiNL;
    }
    
    public String getMoTa() {
        return moTa;
    }
    
    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
    
    // Phương thức kiểm tra loại nguyên liệu có hợp lệ không
    public boolean isValid() {
        return tenLoaiNL != null && !tenLoaiNL.trim().isEmpty();
    }
    
    // Phương thức kiểm tra có mô tả không
    public boolean hasMoTa() {
        return moTa != null && !moTa.trim().isEmpty();
    }
    
    @Override
    public String toString() {
        return "LoaiNguyenLieu{" +
                "maLoaiNL=" + maLoaiNL +
                ", tenLoaiNL='" + tenLoaiNL + '\'' +
                ", moTa='" + moTa + '\'' +
                '}';
    }
}