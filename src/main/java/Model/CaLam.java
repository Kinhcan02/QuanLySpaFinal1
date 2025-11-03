package Model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CaLam {
    private Integer maCa;
    private Integer maNhanVien;
    private LocalDate ngayLam;
    private LocalTime gioBatDau;
    private LocalTime gioKetThuc;
    private BigDecimal soGioLam;
    private BigDecimal soGioTangCa;
    private Integer soLuongKhachPhucVu;
    private BigDecimal tienTip;
    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhat;

    // Danh sách lịch sử tip
    private List<TipHistory> lichSuTip;

    // Reference object
    private NhanVien nhanVien;

    // Constructor mặc định
    public CaLam() {
        this.soLuongKhachPhucVu = 0;
        this.tienTip = BigDecimal.ZERO;
        this.ngayTao = LocalDateTime.now();
        this.ngayCapNhat = LocalDateTime.now();
        this.lichSuTip = new ArrayList<>();
    }

    // Constructor với thông tin cơ bản
    public CaLam(Integer maNhanVien, LocalDate ngayLam, LocalTime gioBatDau, LocalTime gioKetThuc) {
        this();
        this.maNhanVien = maNhanVien;
        this.ngayLam = ngayLam;
        this.gioBatDau = gioBatDau;
        this.gioKetThuc = gioKetThuc;
        this.tinhSoGioLam();
    }

    // Constructor đầy đủ
    public CaLam(Integer maCa, Integer maNhanVien, LocalDate ngayLam, LocalTime gioBatDau,
                LocalTime gioKetThuc, BigDecimal soGioLam, BigDecimal soGioTangCa,
                Integer soLuongKhachPhucVu, BigDecimal tienTip, 
                LocalDateTime ngayTao, LocalDateTime ngayCapNhat) {
        this.maCa = maCa;
        this.maNhanVien = maNhanVien;
        this.ngayLam = ngayLam;
        this.gioBatDau = gioBatDau;
        this.gioKetThuc = gioKetThuc;
        this.soGioLam = soGioLam;
        this.soGioTangCa = soGioTangCa;
        this.soLuongKhachPhucVu = soLuongKhachPhucVu != null ? soLuongKhachPhucVu : 0;
        this.tienTip = tienTip != null ? tienTip : BigDecimal.ZERO;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
        this.lichSuTip = new ArrayList<>();
    }

    // Inner class cho lịch sử tip
    public static class TipHistory {
        private LocalDateTime thoiGian;
        private BigDecimal soTien;
        private String ghiChu;

        public TipHistory(BigDecimal soTien, String ghiChu) {
            this.thoiGian = LocalDateTime.now();
            this.soTien = soTien;
            this.ghiChu = ghiChu;
        }

        // Getters
        public LocalDateTime getThoiGian() { return thoiGian; }
        public BigDecimal getSoTien() { return soTien; }
        public String getGhiChu() { return ghiChu; }
    }

    // Getter và Setter
    public Integer getMaCa() { return maCa; }
    public void setMaCa(Integer maCa) { this.maCa = maCa; }

    public Integer getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(Integer maNhanVien) { this.maNhanVien = maNhanVien; }

    public LocalDate getNgayLam() { return ngayLam; }
    public void setNgayLam(LocalDate ngayLam) { this.ngayLam = ngayLam; }

    public LocalTime getGioBatDau() { return gioBatDau; }
    public void setGioBatDau(LocalTime gioBatDau) { this.gioBatDau = gioBatDau; }

    public LocalTime getGioKetThuc() { return gioKetThuc; }
    public void setGioKetThuc(LocalTime gioKetThuc) { this.gioKetThuc = gioKetThuc; }

    public BigDecimal getSoGioLam() { return soGioLam; }
    public void setSoGioLam(BigDecimal soGioLam) { this.soGioLam = soGioLam; }

    public BigDecimal getSoGioTangCa() { return soGioTangCa; }
    public void setSoGioTangCa(BigDecimal soGioTangCa) { this.soGioTangCa = soGioTangCa; }

    public Integer getSoLuongKhachPhucVu() { return soLuongKhachPhucVu; }
    public void setSoLuongKhachPhucVu(Integer soLuongKhachPhucVu) { 
        this.soLuongKhachPhucVu = soLuongKhachPhucVu != null ? soLuongKhachPhucVu : 0; 
    }

    public BigDecimal getTienTip() { return tienTip; }
    public void setTienTip(BigDecimal tienTip) { 
        this.tienTip = tienTip != null ? tienTip : BigDecimal.ZERO; 
    }

    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }

    public LocalDateTime getNgayCapNhat() { return ngayCapNhat; }
    public void setNgayCapNhat(LocalDateTime ngayCapNhat) { this.ngayCapNhat = ngayCapNhat; }

    public List<TipHistory> getLichSuTip() { return lichSuTip; }
    public void setLichSuTip(List<TipHistory> lichSuTip) { this.lichSuTip = lichSuTip; }

    public NhanVien getNhanVien() { return nhanVien; }
    public void setNhanVien(NhanVien nhanVien) { this.nhanVien = nhanVien; }

    // Phương thức thêm tip mới
    public void themTip(BigDecimal soTien, String ghiChu) {
        if (soTien != null && soTien.compareTo(BigDecimal.ZERO) > 0) {
            TipHistory tipMoi = new TipHistory(soTien, ghiChu);
            this.lichSuTip.add(tipMoi);
            this.tienTip = this.tienTip.add(soTien);
            this.ngayCapNhat = LocalDateTime.now();
        }
    }

    // Phương thức tính tổng tip từ lịch sử
    public BigDecimal tinhTongTipTuLichSu() {
        return lichSuTip.stream()
                .map(TipHistory::getSoTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Phương thức tính số giờ làm tự động
    public void tinhSoGioLam() {
        if (gioBatDau != null && gioKetThuc != null) {
            long minutes = java.time.Duration.between(gioBatDau, gioKetThuc).toMinutes();
            double hours = minutes / 60.0;
            this.soGioLam = BigDecimal.valueOf(hours);
        }
    }

    // Phương thức tính tổng số giờ (làm việc + tăng ca)
    public BigDecimal getTongSoGio() {
        BigDecimal total = BigDecimal.ZERO;
        if (soGioLam != null) total = total.add(soGioLam);
        if (soGioTangCa != null) total = total.add(soGioTangCa);
        return total;
    }

    // Phương thức kiểm tra ca làm có hợp lệ không
    public boolean isValid() {
        return maNhanVien != null && ngayLam != null && gioBatDau != null && gioKetThuc != null;
    }

    // Phương thức kiểm tra xem ca làm có phải là tăng ca không
    public boolean isTangCa() {
        return soGioTangCa != null && soGioTangCa.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public String toString() {
        return "CaLam{" +
                "maCa=" + maCa +
                ", maNhanVien=" + maNhanVien +
                ", ngayLam=" + ngayLam +
                ", gioBatDau=" + gioBatDau +
                ", gioKetThuc=" + gioKetThuc +
                ", soGioLam=" + soGioLam +
                ", soGioTangCa=" + soGioTangCa +
                ", soLuongKhachPhucVu=" + soLuongKhachPhucVu +
                ", tienTip=" + tienTip +
                ", ngayTao=" + ngayTao +
                ", ngayCapNhat=" + ngayCapNhat +
                '}';
    }
}