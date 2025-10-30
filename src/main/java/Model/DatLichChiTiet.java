package Model;

import java.time.LocalDateTime;

public class DatLichChiTiet {
    private Integer maCTDL;
    private Integer maLich;
    private Integer maDichVu;
    private String ghiChu;
    private LocalDateTime ngayTao;
    
    // Reference to DichVu for easy access
    private DichVu dichVu;

    public DatLichChiTiet() {}

    public DatLichChiTiet(Integer maCTDL, Integer maLich, Integer maDichVu, String ghiChu, LocalDateTime ngayTao) {
        this.maCTDL = maCTDL;
        this.maLich = maLich;
        this.maDichVu = maDichVu;
        this.ghiChu = ghiChu;
        this.ngayTao = ngayTao;
    }

    // Getter và Setter
    public Integer getMaCTDL() { return maCTDL; }
    public void setMaCTDL(Integer maCTDL) { this.maCTDL = maCTDL; }

    public Integer getMaLich() { return maLich; }
    public void setMaLich(Integer maLich) { this.maLich = maLich; }

    public Integer getMaDichVu() { return maDichVu; }
    public void setMaDichVu(Integer maDichVu) { this.maDichVu = maDichVu; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }

    public DichVu getDichVu() { return dichVu; }
    public void setDichVu(DichVu dichVu) { this.dichVu = dichVu; }
}