package Model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Model class cho bảng DichVu
 */
public class DichVu {

    private Integer maDichVu;
    private String tenDichVu;
    private BigDecimal gia;
    private Integer thoiGian; // Thêm trường thời gian
    private Integer maLoaiDV;
    private String ghiChu;

    // Reference to LoaiDichVu object (optional - for JOIN operations)
    private LoaiDichVu loaiDichVu;

    // Constructor mặc định
    public DichVu() {
    }
// Constructor cho item mặc định

    public DichVu(String displayText) {
        this.tenDichVu = displayText;
    }

    // Constructor với tất cả tham số
    public DichVu(Integer maDichVu, String tenDichVu, BigDecimal gia, Integer thoiGian, Integer maLoaiDV, String ghiChu) {
        this.maDichVu = maDichVu;
        this.tenDichVu = tenDichVu;
        this.gia = gia;
        this.thoiGian = thoiGian;
        this.maLoaiDV = maLoaiDV;
        this.ghiChu = ghiChu;
    }

    // Constructor không có ID (dùng khi insert)
    public DichVu(String tenDichVu, BigDecimal gia, Integer thoiGian, Integer maLoaiDV, String ghiChu) {
        this.tenDichVu = tenDichVu;
        this.gia = gia;
        this.thoiGian = thoiGian;
        this.maLoaiDV = maLoaiDV;
        this.ghiChu = ghiChu;
    }

    // Constructor đơn giản
    public DichVu(String tenDichVu, BigDecimal gia, Integer thoiGian, Integer maLoaiDV) {
        this.tenDichVu = tenDichVu;
        this.gia = gia;
        this.thoiGian = thoiGian;
        this.maLoaiDV = maLoaiDV;
    }

    // Getters and Setters
    public Integer getMaDichVu() {
        return maDichVu;
    }

    public void setMaDichVu(Integer maDichVu) {
        this.maDichVu = maDichVu;
    }

    public String getTenDichVu() {
        return tenDichVu;
    }

    public void setTenDichVu(String tenDichVu) {
        this.tenDichVu = tenDichVu;
    }

    public BigDecimal getGia() {
        return gia;
    }

    public void setGia(BigDecimal gia) {
        this.gia = gia;
    }

    public Integer getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(Integer thoiGian) {
        this.thoiGian = thoiGian;
    }

    public Integer getMaLoaiDV() {
        return maLoaiDV;
    }

    public void setMaLoaiDV(Integer maLoaiDV) {
        this.maLoaiDV = maLoaiDV;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public LoaiDichVu getLoaiDichVu() {
        return loaiDichVu;
    }

    public void setLoaiDichVu(LoaiDichVu loaiDichVu) {
        this.loaiDichVu = loaiDichVu;
    }

    // Validation method
    public boolean isValid() {
        return tenDichVu != null && !tenDichVu.trim().isEmpty()
                && gia != null && gia.compareTo(BigDecimal.ZERO) >= 0;
    }

    // equals và hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DichVu dichVu = (DichVu) o;
        return Objects.equals(maDichVu, dichVu.maDichVu)
                && Objects.equals(tenDichVu, dichVu.tenDichVu)
                && Objects.equals(gia, dichVu.gia)
                && Objects.equals(thoiGian, dichVu.thoiGian)
                && Objects.equals(maLoaiDV, dichVu.maLoaiDV)
                && Objects.equals(ghiChu, dichVu.ghiChu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maDichVu, tenDichVu, gia, thoiGian, maLoaiDV, ghiChu);
    }

    // toString
    @Override
    public String toString() {
        if (maDichVu == null) {
            return "-- Chọn dịch vụ --";
        }
        return tenDichVu + " - " + (gia != null ? String.format("%,d VNĐ", gia.intValue()) : "0 VNĐ")
                + (thoiGian != null ? " (" + thoiGian + " phút)" : "");
    }
}
