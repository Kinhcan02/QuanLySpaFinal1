package Model;

import java.time.LocalDate;
import java.math.BigDecimal;

public class ThuNhap {
    private Integer maThu;
    private LocalDate ngayThu;
    private BigDecimal soTien;
    private String noiDung;
    
    // Constructor mặc định
    public ThuNhap() {
        this.ngayThu = LocalDate.now();
    }
    
    // Constructor với số tiền và nội dung
    public ThuNhap(BigDecimal soTien, String noiDung) {
        this();
        this.soTien = soTien;
        this.noiDung = noiDung;
    }
    
    // Constructor với số tiền (không có nội dung)
    public ThuNhap(BigDecimal soTien) {
        this();
        this.soTien = soTien;
    }

    public ThuNhap(Integer maThu, LocalDate ngayThu, BigDecimal soTien, String noiDung) {
        this.maThu = maThu;
        this.ngayThu = ngayThu;
        this.soTien = soTien;
        this.noiDung = noiDung;
    }
    
    // Constructor đầy đủ tham số (KHÔNG có khóa chính MaThu)
    public ThuNhap(LocalDate ngayThu, BigDecimal soTien, String noiDung) {
        this.ngayThu = ngayThu != null ? ngayThu : LocalDate.now();
        this.soTien = soTien;
        this.noiDung = noiDung;
    }
    
    // Getter và Setter
    public Integer getMaThu() {
        return maThu;
    }
    
    public void setMaThu(Integer maThu) {
        this.maThu = maThu;
    }
    
    public LocalDate getNgayThu() {
        return ngayThu;
    }
    
    public void setNgayThu(LocalDate ngayThu) {
        this.ngayThu = ngayThu != null ? ngayThu : LocalDate.now();
    }
    
    public BigDecimal getSoTien() {
        return soTien;
    }
    
    public void setSoTien(BigDecimal soTien) {
        // Kiểm tra số tiền không được âm
        if (soTien != null && soTien.compareTo(BigDecimal.ZERO) >= 0) {
            this.soTien = soTien;
        }
    }
    
    public String getNoiDung() {
        return noiDung;
    }
    
    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }
    
    // Phương thức kiểm tra thu nhập có hợp lệ không
    public boolean isValid() {
        return soTien != null && soTien.compareTo(BigDecimal.ZERO) >= 0;
    }
    
    // Phương thức kiểm tra có nội dung không
    public boolean hasNoiDung() {
        return noiDung != null && !noiDung.trim().isEmpty();
    }
    
    // Phương thức kiểm tra thu nhập trong tháng hiện tại
    public boolean isTrongThangHienTai() {
        if (ngayThu == null) return false;
        LocalDate now = LocalDate.now();
        return ngayThu.getMonth() == now.getMonth() && ngayThu.getYear() == now.getYear();
    }
    
    // Phương thức kiểm tra thu nhập trong năm hiện tại
    public boolean isTrongNamHienTai() {
        if (ngayThu == null) return false;
        return ngayThu.getYear() == LocalDate.now().getYear();
    }
    
    // Phương thức định dạng số tiền
    public String getSoTienFormatted() {
        if (soTien == null) return "0";
        return String.format("%,.0f VND", soTien.doubleValue());
    }
    
    @Override
    public String toString() {
        return "ThuNhap{" +
                "maThu=" + maThu +
                ", ngayThu=" + ngayThu +
                ", soTien=" + soTien +
                ", noiDung='" + noiDung + '\'' +
                '}';
    }
}