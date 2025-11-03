package Model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class VonDauTu {
    private Integer maVon;
    private String tenVon;
    private LocalDate ngayDauTu;
    private BigDecimal soTien;
    private String mucDich;
    private String nguonVon;
    private String ghiChu;

    // Constructor mặc định
    public VonDauTu() {
        this.ngayDauTu = LocalDate.now();
        this.soTien = BigDecimal.ZERO;
    }

    // Constructor với thông tin cơ bản
    public VonDauTu(String tenVon, BigDecimal soTien, String mucDich) {
        this();
        this.tenVon = tenVon;
        this.soTien = soTien != null ? soTien : BigDecimal.ZERO;
        this.mucDich = mucDich;
    }

    // Constructor đầy đủ
    public VonDauTu(Integer maVon, String tenVon, LocalDate ngayDauTu, 
                   BigDecimal soTien, String mucDich, String nguonVon, String ghiChu) {
        this.maVon = maVon;
        this.tenVon = tenVon;
        this.ngayDauTu = ngayDauTu != null ? ngayDauTu : LocalDate.now();
        this.soTien = soTien != null ? soTien : BigDecimal.ZERO;
        this.mucDich = mucDich;
        this.nguonVon = nguonVon;
        this.ghiChu = ghiChu;
    }

    // Getter và Setter
    public Integer getMaVon() { return maVon; }
    public void setMaVon(Integer maVon) { this.maVon = maVon; }

    public String getTenVon() { return tenVon; }
    public void setTenVon(String tenVon) { this.tenVon = tenVon; }

    public LocalDate getNgayDauTu() { return ngayDauTu; }
    public void setNgayDauTu(LocalDate ngayDauTu) { 
        this.ngayDauTu = ngayDauTu != null ? ngayDauTu : LocalDate.now(); 
    }

    public BigDecimal getSoTien() { return soTien; }
    public void setSoTien(BigDecimal soTien) { 
        if (soTien != null && soTien.compareTo(BigDecimal.ZERO) >= 0) {
            this.soTien = soTien;
        }
    }

    public String getMucDich() { return mucDich; }
    public void setMucDich(String mucDich) { this.mucDich = mucDich; }

    public String getNguonVon() { return nguonVon; }
    public void setNguonVon(String nguonVon) { this.nguonVon = nguonVon; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    // Phương thức kiểm tra hợp lệ
    public boolean isValid() {
        return tenVon != null && !tenVon.trim().isEmpty() &&
               soTien != null && soTien.compareTo(BigDecimal.ZERO) >= 0;
    }

    // Phương thức định dạng số tiền
    public String getSoTienFormatted() {
        if (soTien == null) return "0";
        return String.format("%,.0f VND", soTien.doubleValue());
    }

    @Override
    public String toString() {
        return "VonDauTu{" +
                "maVon=" + maVon +
                ", tenVon='" + tenVon + '\'' +
                ", ngayDauTu=" + ngayDauTu +
                ", soTien=" + soTien +
                ", mucDich='" + mucDich + '\'' +
                ", nguonVon='" + nguonVon + '\'' +
                ", ghiChu='" + ghiChu + '\'' +
                '}';
    }
}