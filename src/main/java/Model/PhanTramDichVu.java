package Model;

import java.util.Objects;

public class PhanTramDichVu {
    private Integer maPhanTram;
    private Integer maLoaiDV;
    private Integer maNhanVien;
    private Double tiLePhanTram;

    // Reference objects
    private LoaiDichVu loaiDichVu;
    private NhanVien nhanVien;

    // Constructors
    public PhanTramDichVu() {
    }

    public PhanTramDichVu(Integer maLoaiDV, Integer maNhanVien, Double tiLePhanTram) {
        this.maLoaiDV = maLoaiDV;
        this.maNhanVien = maNhanVien;
        this.tiLePhanTram = tiLePhanTram;
    }

    public PhanTramDichVu(Integer maPhanTram, Integer maLoaiDV, Integer maNhanVien, Double tiLePhanTram) {
        this.maPhanTram = maPhanTram;
        this.maLoaiDV = maLoaiDV;
        this.maNhanVien = maNhanVien;
        this.tiLePhanTram = tiLePhanTram;
    }

    // Getters and Setters
    public Integer getMaPhanTram() { return maPhanTram; }
    public void setMaPhanTram(Integer maPhanTram) { this.maPhanTram = maPhanTram; }

    public Integer getMaLoaiDV() { return maLoaiDV; }
    public void setMaLoaiDV(Integer maLoaiDV) { this.maLoaiDV = maLoaiDV; }

    public Integer getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(Integer maNhanVien) { this.maNhanVien = maNhanVien; }

    public Double getTiLePhanTram() { return tiLePhanTram; }
    public void setTiLePhanTram(Double tiLePhanTram) { 
        this.tiLePhanTram = tiLePhanTram != null ? tiLePhanTram : 0.0;
    }

    public LoaiDichVu getLoaiDichVu() { return loaiDichVu; }
    public void setLoaiDichVu(LoaiDichVu loaiDichVu) { this.loaiDichVu = loaiDichVu; }

    public NhanVien getNhanVien() { return nhanVien; }
    public void setNhanVien(NhanVien nhanVien) { this.nhanVien = nhanVien; }

    // Validation
    public boolean isValid() {
        return maLoaiDV != null && maNhanVien != null && tiLePhanTram != null && tiLePhanTram >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhanTramDichVu that = (PhanTramDichVu) o;
        return Objects.equals(maPhanTram, that.maPhanTram) &&
               Objects.equals(maLoaiDV, that.maLoaiDV) &&
               Objects.equals(maNhanVien, that.maNhanVien) &&
               Objects.equals(tiLePhanTram, that.tiLePhanTram);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maPhanTram, maLoaiDV, maNhanVien, tiLePhanTram);
    }

    @Override
    public String toString() {
        return "PhanTramDichVu{" +
                "maPhanTram=" + maPhanTram +
                ", maLoaiDV=" + maLoaiDV +
                ", maNhanVien=" + maNhanVien +
                ", tiLePhanTram=" + tiLePhanTram +
                '}';
    }
}