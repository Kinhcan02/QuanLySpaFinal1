package Model;

import java.util.Objects;

/**
 * Model class cho bảng LoaiDichVu
 */
public class LoaiDichVu {
    private Integer maLoaiDV;
    private String tenLoaiDV;
    private String moTa;

    // Constructor mặc định
    public LoaiDichVu() {
    }

    // Constructor với tham số
    public LoaiDichVu(Integer maLoaiDV, String tenLoaiDV, String moTa) {
        this.maLoaiDV = maLoaiDV;
        this.tenLoaiDV = tenLoaiDV;
        this.moTa = moTa;
    }

    // Constructor không có ID (dùng khi insert)
    public LoaiDichVu(String tenLoaiDV, String moTa) {
        this.tenLoaiDV = tenLoaiDV;
        this.moTa = moTa;
    }

    // Getters and Setters
    public Integer getMaLoaiDV() {
        return maLoaiDV;
    }

    public void setMaLoaiDV(Integer maLoaiDV) {
        this.maLoaiDV = maLoaiDV;
    }

    public String getTenLoaiDV() {
        return tenLoaiDV;
    }

    public void setTenLoaiDV(String tenLoaiDV) {
        this.tenLoaiDV = tenLoaiDV;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    // equals và hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoaiDichVu that = (LoaiDichVu) o;
        return Objects.equals(maLoaiDV, that.maLoaiDV) &&
               Objects.equals(tenLoaiDV, that.tenLoaiDV) &&
               Objects.equals(moTa, that.moTa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maLoaiDV, tenLoaiDV, moTa);
    }

    // toString
     @Override
    public String toString() {
        return tenLoaiDV != null ? tenLoaiDV : "";
    }
}