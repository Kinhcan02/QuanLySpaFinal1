package Model;

import java.time.LocalDate;
import java.math.BigDecimal;

public class ChiTieu {

    private Integer maChi;
    private LocalDate ngayChi;
    private String mucDich;
    private BigDecimal soTien;

    // Constructor mặc định
    public ChiTieu() {
        this.ngayChi = LocalDate.now();
    }

    // Constructor với mục đích và số tiền
    public ChiTieu(String mucDich, BigDecimal soTien) {
        this();
        this.mucDich = mucDich;
        this.soTien = soTien;
    }

    // Constructor chỉ với số tiền
    public ChiTieu(BigDecimal soTien) {
        this();
        this.soTien = soTien;
    }

    // Constructor đầy đủ tham số (KHÔNG có khóa chính MaChi)
    public ChiTieu(LocalDate ngayChi, String mucDich, BigDecimal soTien) {
        this.ngayChi = ngayChi != null ? ngayChi : LocalDate.now();
        this.mucDich = mucDich;
        this.soTien = soTien;
    }

    public ChiTieu(Integer maChi, LocalDate ngayChi, String mucDich, BigDecimal soTien) {
        this.maChi = maChi;
        this.ngayChi = ngayChi;
        this.mucDich = mucDich;
        this.soTien = soTien;
    }

    // Getter và Setter
    public Integer getMaChi() {
        return maChi;
    }

    public void setMaChi(Integer maChi) {
        this.maChi = maChi;
    }

    public LocalDate getNgayChi() {
        return ngayChi;
    }

    public void setNgayChi(LocalDate ngayChi) {
        this.ngayChi = ngayChi != null ? ngayChi : LocalDate.now();
    }

    public String getMucDich() {
        return mucDich;
    }

    public void setMucDich(String mucDich) {
        this.mucDich = mucDich;
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

    // Phương thức kiểm tra chi tiêu có hợp lệ không
    public boolean isValid() {
        return soTien != null && soTien.compareTo(BigDecimal.ZERO) >= 0;
    }

    // Phương thức kiểm tra có mục đích không
    public boolean hasMucDich() {
        return mucDich != null && !mucDich.trim().isEmpty();
    }

    // Phương thức kiểm tra chi tiêu trong tháng hiện tại
    public boolean isTrongThangHienTai() {
        if (ngayChi == null) {
            return false;
        }
        LocalDate now = LocalDate.now();
        return ngayChi.getMonth() == now.getMonth() && ngayChi.getYear() == now.getYear();
    }

    // Phương thức kiểm tra chi tiêu trong năm hiện tại
    public boolean isTrongNamHienTai() {
        if (ngayChi == null) {
            return false;
        }
        return ngayChi.getYear() == LocalDate.now().getYear();
    }

    // Phương thức kiểm tra chi tiêu trong tuần hiện tại
    public boolean isTrongTuanHienTai() {
        if (ngayChi == null) {
            return false;
        }
        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.minusDays(now.getDayOfWeek().getValue() - 1);
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        return !ngayChi.isBefore(startOfWeek) && !ngayChi.isAfter(endOfWeek);
    }

    // Phương thức định dạng số tiền
    public String getSoTienFormatted() {
        if (soTien == null) {
            return "0";
        }
        return String.format("%,.0f VND", soTien.doubleValue());
    }

    // Phương thức lấy mục đích mặc định nếu null
    public String getMucDichDisplay() {
        return hasMucDich() ? mucDich : "Không có mục đích";
    }

    @Override
    public String toString() {
        return "ChiTieu{"
                + "maChi=" + maChi
                + ", ngayChi=" + ngayChi
                + ", mucDich='" + mucDich + '\''
                + ", soTien=" + soTien
                + '}';
    }
}
